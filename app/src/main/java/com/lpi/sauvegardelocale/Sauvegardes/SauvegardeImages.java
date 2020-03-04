package com.lpi.sauvegardelocale.Sauvegardes;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lpi.sauvegardelocale.Sauvegardes.objets.SavedImage;
import com.lpi.sauvegardelocale.utils.FileUtils;
import com.lpi.sauvegardelocale.utils.Preferences;

public class SauvegardeImages extends Sauvegarde
{
	public static String REPERTOIRE = "Images";
	private int nbObjets;


	@Override
	protected int getSauvegardeCount()
	{
		return nbObjets;
	}
	@Override
	protected STATUS executeRestauration(final RestaurationAsynchrone t)
	{
		return STATUS.OK;
	}

	@Override
	protected STATUS executeSauvegarde(@NonNull SauvegardeAsynchrone t)
	{
		ConnexionSauvegarde connexion = null;
		REPERTOIRE = Preferences.getInstance(_context).getString(Preferences.PREF_REPERTOIRE_PHOTOS, REPERTOIRE);
		try
		{
			// Socket de communication avec le adresse
			connexion = getConnexion();

			// Curseur pour parcourir les contacts d'Android
			Cursor cursor = SavedImage.getCursor(_context);
			if (cursor == null)
				return STATUS.FAILED;

			// Creer le repertoire de sauvegarde des contacts
			connexion.creerRepertoire(REPERTOIRE);

			nbObjets = cursor.getCount();   // Nombre de contacts total
			int current = 0;

			while (cursor.moveToNext() && !_annule)
			{
				current++;
				t.publieProgres(current);
				SavedImage objet = new SavedImage(cursor, _context);
				Log.d(TAG, "Image " + objet.Nom(_context));

				// Creation d'un fichier pour cet appel
				String categorie = FileUtils.Combine(REPERTOIRE, objet.getCategorie());

				// Chemin pour cet appel
				String appelPath = FileUtils.cleanPathName(FileUtils.Combine(categorie, objet.getFileName()));
				if (!connexion.exists(appelPath))
				{
					connexion.creerRepertoire(categorie);
					objet.sauvegarde(connexion, _context, appelPath);
				}
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

	public SauvegardeImages(@NonNull final Context c, @NonNull InterfaceSauvegarde interfce, @NonNull final SauvegardeListener listener)
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
				Cursor cursor = SavedImage.getCursor(context);
				if (cursor == null)
					return 0;

				int nombre = cursor.getCount();
				cursor.close();
				return nombre;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}