package com.lpi.sauvegardelocale.Sauvegardes;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lpi.sauvegardelocale.R;
import com.lpi.sauvegardelocale.Sauvegardes.objets.SavedContact;
import com.lpi.sauvegardelocale.utils.ComposeTextFile;
import com.lpi.sauvegardelocale.utils.FileUtils;
import com.lpi.sauvegardelocale.utils.Preferences;
import com.lpi.sauvegardelocale.utils.StringOutputStream;

import java.io.BufferedOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Vector;

public class SauvegardeContacts extends Sauvegarde
{
	public static final String FILTRE_CONTACTS = "*.txt";
	public static String REPERTOIRE = "Contacts";
	private int nbContacts;


	@Override
	protected int getSauvegardeCount()
	{
		return nbContacts;
	}

	@Override
	protected STATUS executeRestauration(final RestaurationAsynchrone t)
	{
		ConnexionSauvegarde connexion = null;
		REPERTOIRE = Preferences.getInstance(_context).getString(Preferences.PREF_REPERTOIRE_CONTACTS, REPERTOIRE);
		try
		{
			connexion = getConnexion();
			String[] listeContacts = connexion.getListeFichiers( REPERTOIRE, FILTRE_CONTACTS);
			if ( listeContacts == null)
				return STATUS.FAILED ;
			nbContacts = listeContacts.length;

			int current = 0;
			for (String c : listeContacts )
			{
				current++;
				t.publieProgres(current);
				SavedContact contact = new SavedContact(FileUtils.Combine(REPERTOIRE, c), connexion);
				if (contact!=null)
					contact.restaure(_context);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return STATUS.FAILED;
		}
		finally
		{
			if ( connexion!=null)
				connexion.close();
		}
		return _annule ? STATUS.CANCELED : STATUS.OK;
	}

	@Override
	protected STATUS executeSauvegarde(@NonNull SauvegardeAsynchrone sauvegardeAsynchrone)
	{
		ConnexionSauvegarde connexion = null;
		REPERTOIRE = Preferences.getInstance(_context).getString(Preferences.PREF_REPERTOIRE_CONTACTS, REPERTOIRE);
		try
		{
			// Curseur pour parcourir les contacts d'Android
			Cursor cursor = SavedContact.getCursor(_context);
			if (cursor == null)
				return STATUS.FAILED;

			connexion = getConnexion();

			// Creer le repertoire de sauvegarde des contacts
			connexion.creerRepertoire(REPERTOIRE);

			nbContacts = cursor.getCount();   // Nombre de contacts total
			int current = 0;
			boolean auMoinsUnContactNouveau = false;

			String template = ComposeTextFile.chargeTexteResource(_context, R.raw.template_ligne_contacts);
			StringOutputStream sos = new StringOutputStream();
			BufferedOutputStream contactbos = new BufferedOutputStream(sos);
			while (cursor.moveToNext() && !_annule)
			{
				current++;
				sauvegardeAsynchrone.publieProgres(current);
				SavedContact contact = new SavedContact(cursor, _context);
				Log.d(TAG, "Contact " + contact.getNom());

				// Sauver le contact dans un fichier individuel
				STATUS status = contact.sauvegarde(connexion, _context, REPERTOIRE );
				if ( status == STATUS.OK)
					auMoinsUnContactNouveau = true;

				// Ajoute une ligne pour ce contact dans le fichier global
				HashMap<String, String> hashContact = new HashMap<>();
				hashContact.put("NOM_CONTACT", contact.getNom());
				hashContact.put("ADRESSE_CONTACT", formateNumeros(contact._numeros));
				hashContact.put("MAILS_CONTACT", formateNumeros(contact._eMails));
				hashContact.put("CONTACT_LIEN", contact.getFileName(_context));

				if (contact._photo != null)
				{
					String nomImage = contact.getFileName(_context, ".png");
					String imagePath = FileUtils.cleanPathName(FileUtils.Combine(REPERTOIRE, nomImage));
					if (!connexion.exists(imagePath))
						connexion.envoiFichier(imagePath, contact._photo);
					hashContact.put("IMAGE_CONTACT", nomImage);
				}
				ComposeTextFile.composeFichierTexte(_context, contactbos, template, hashContact);
			}

			contactbos.close();
			sos.close();
			cursor.close();

			if (auMoinsUnContactNouveau)
				creerFichierTousLesContacts( connexion, sos );
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return STATUS.FAILED;
		}
		finally
		{
			if ( connexion!=null)
				connexion.close();
		}
		return _annule ? STATUS.CANCELED : STATUS.OK;
	}

	/***
	 * Creer le fichier HTML contenant tous les contacts
	 * @param connexion
	 * @param sos
	 * @throws Exception
	 */
	private void creerFichierTousLesContacts( ConnexionSauvegarde connexion, StringOutputStream sos ) throws Exception
	{
		HashMap<String, String> hashmap = new HashMap<>();
		hashmap.put("CONTACT_CHARSET", Charset.defaultCharset().name());
		hashmap.put("TABLE_CONTACTS", sos.toString());

		StringOutputStream sbof = new StringOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(sbof);
		ComposeTextFile.composeFichierTexte(_context, bos, R.raw.template_liste_contacts, hashmap);
		bos.close();
		sbof.close();

		String fichier = FileUtils.cleanPathName(FileUtils.Combine(REPERTOIRE, "contacts.html"));
		connexion.envoiFichier(fichier, sbof.toString());
	}


	private String formateNumeros(@Nullable final Vector<String> coordonnees)
	{
		if (coordonnees == null)
			return "";

		StringBuilder res = new StringBuilder();
		for (String coord : coordonnees)
		{
			res.append(coord).append(" ");
		}

		return res.toString();
	}

	public SauvegardeContacts(@NonNull final Context c, @NonNull InterfaceSauvegarde interfce, @NonNull final Sauvegarde.SauvegardeListener listener)
	{
		super(c, interfce, listener);
	}

	public static void Compte( @NonNull final Context context, @NonNull final CompteObjetsListener listener )
	{
		new AsyncTask<Void, Void, Integer>()
		{
			@Override
			protected void onPostExecute(final Integer integer)
			{
				super.onPostExecute(integer);
				listener.onObjetsComptes(integer);
			}

			@Override
			protected Integer doInBackground(final Void... voids)
			{
				Cursor cursor = SavedContact.getCursor(context);
				if (cursor == null)
					return 0;

				int nombre = cursor.getCount();
				cursor.close();
				return nombre;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
