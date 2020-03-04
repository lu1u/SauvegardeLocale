package com.lpi.sauvegardelocale.Sauvegardes;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.lpi.sauvegardelocale.Sauvegardes.objets.SavedAppel;
import com.lpi.sauvegardelocale.utils.FileUtils;
import com.lpi.sauvegardelocale.utils.Preferences;

public class SauvegardeAppels extends Sauvegarde
{
	private static final String FILTRE_APPELS = "*.txt";
	public static String REPERTOIRE = "Appels";
	private int nbAppels;

	@Override
	protected int getSauvegardeCount()
	{
		return nbAppels;
	}

	@Override
	protected STATUS executeRestauration(final RestaurationAsynchrone t)
	{
		ConnexionSauvegarde connexion = null;
		REPERTOIRE = Preferences.getInstance(_context).getString(Preferences.PREF_REPERTOIRE_APPELS, REPERTOIRE);
		try
		{
			connexion = getConnexion();
			String[] listeRepertoires = connexion.getListeRepertoires( REPERTOIRE);
			if ( listeRepertoires==null)
				return STATUS.FAILED;
			int current = 0;
			nbAppels = listeRepertoires.length;
			for ( String rep : listeRepertoires)
			{
				current++;
				t.publieProgres(current);
				String repertoire = FileUtils.Combine(REPERTOIRE, rep) ;
				String[] listeAppels = connexion.getListeFichiers( repertoire, FILTRE_APPELS);
				if ( listeAppels == null)
					return STATUS.FAILED ;

				for (String c : listeAppels )
				{
					SavedAppel appel= new SavedAppel(FileUtils.Combine(repertoire, c), connexion);
					appel.restaure(_context);
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

	@Override
	protected STATUS executeSauvegarde(final SauvegardeAsynchrone t)
	{
		ConnexionSauvegarde connexion = null;
		REPERTOIRE = Preferences.getInstance(_context).getString(Preferences.PREF_REPERTOIRE_APPELS, REPERTOIRE);
		try
		{
			// Socket de communication avec le adresse
			connexion = getConnexion();

			// Curseur pour parcourir les contacts d'Android
			Cursor cursor = SavedAppel.getCursor(_context);
			if ( cursor == null)
				return STATUS.FAILED ;

			// Creer le repertoire de sauvegarde des contacts
			connexion.creerRepertoire(REPERTOIRE);

			nbAppels = cursor.getCount() ;   // Nombre de contacts total
			int current = 0;

			while (cursor.moveToNext() && !_annule)
			{
				current++;
				t.publieProgres(current);
				SavedAppel appel = new SavedAppel(cursor, _context);
				Log.d(TAG, "Appel " + appel.Nom(_context));

				// Creation d'un fichier pour cet appel
				String categorie = FileUtils.Combine( REPERTOIRE, appel.getCategorie(_context));

				// Chemin pour cet appel
				String appelPath = FileUtils.cleanPathName(FileUtils.Combine(categorie, appel.getFileName(_context)));
				if (! connexion.exists(appelPath))
				{
					connexion.creerRepertoire(categorie);
					appel.sauvegarde(connexion, _context, appelPath);
				}
			}
		} catch (Exception e)
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

	public SauvegardeAppels(@NonNull final Context c, @NonNull InterfaceSauvegarde interfce, @NonNull final SauvegardeListener listener)
	{
		super(c, interfce, listener);
	}

	/***
	 * Compte les objets a sauvegarder
	 * @param context
	 * @param listener
	 */
	@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
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
				Cursor cursor = SavedAppel.getCursor(context);
				if (cursor == null)
					return 0;

				int nombre = cursor.getCount();
				cursor.close();
				return nombre;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}