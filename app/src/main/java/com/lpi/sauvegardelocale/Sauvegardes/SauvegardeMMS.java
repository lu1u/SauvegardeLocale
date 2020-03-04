package com.lpi.sauvegardelocale.Sauvegardes;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lpi.sauvegardelocale.Sauvegardes.objets.SavedMMS;
import com.lpi.sauvegardelocale.utils.FileUtils;
import com.lpi.sauvegardelocale.utils.Preferences;

public class SauvegardeMMS extends Sauvegarde
{
	public static String REPERTOIRE = "MMS";
	private int nbMessages;

	@Override
	protected int getSauvegardeCount()
	{
		return nbMessages;
	}
	@Override
	protected STATUS executeRestauration(final RestaurationAsynchrone t)
	{
		return STATUS.OK;
	}

	@Override
	protected Sauvegarde.STATUS executeSauvegarde(final Sauvegarde.SauvegardeAsynchrone t)
	{
		ConnexionSauvegarde connexion = null;
		REPERTOIRE = Preferences.getInstance(_context).getString(Preferences.PREF_REPERTOIRE_MMS, REPERTOIRE);
		try
		{
			// Socket de communication avec le adresse
			connexion = getConnexion();

			// Curseur pour parcourir les contacts d'Android
			Cursor cursor = SavedMMS.getCursor(_context);
			if ( cursor == null)
				return Sauvegarde.STATUS.FAILED ;

			// Creer le repertoire de sauvegarde des contacts
			connexion.creerRepertoire(REPERTOIRE);

			nbMessages = cursor.getCount() ;   // Nombre de contacts total
			int current = 0;

			while (cursor.moveToNext() && !_annule)
			{
				current++;
				t.publieProgres(current);
				SavedMMS message = new SavedMMS(cursor, _context);
				Log.d(TAG, "Appel " + message.Nom(_context));

				// Creation d'un fichier pour cet appel
				String categorie = FileUtils.Combine( REPERTOIRE, message.getCategorie());

				// Chemin pour ce MMS
				String path = FileUtils.cleanPathName(FileUtils.Combine(categorie, message.getFileName(_context)));
				if (! connexion.exists(path))
				{
					connexion.creerRepertoire(categorie);
					message.sauvegarde(connexion, _context, path);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return Sauvegarde.STATUS.FAILED;
		}
		finally
		{
			if ( connexion!=null)
				connexion.close();
		}
		return _annule ? Sauvegarde.STATUS.CANCELED : Sauvegarde.STATUS.OK;
	}

	public SauvegardeMMS(@NonNull final Context c, @NonNull InterfaceSauvegarde interfce, @NonNull final Sauvegarde.SauvegardeListener listener)
	{
		super(c, interfce, listener);
	}

	public static void Compte(final Context context, final CompteObjetsListener listener)
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
				Cursor cursor = SavedMMS.getCursor(context);
				if (cursor == null)
					return 0;

				int nombre = cursor.getCount();
				cursor.close();
				return nombre;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
