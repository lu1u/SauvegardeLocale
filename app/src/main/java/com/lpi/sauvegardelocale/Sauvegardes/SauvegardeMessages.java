package com.lpi.sauvegardelocale.Sauvegardes;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lpi.sauvegardelocale.R;
import com.lpi.sauvegardelocale.Sauvegardes.objets.SavedMessage;
import com.lpi.sauvegardelocale.utils.FileUtils;
import com.lpi.sauvegardelocale.utils.MessageBoxUtils;
import com.lpi.sauvegardelocale.utils.Preferences;
import com.lpi.sauvegardelocale.SMSDefaultApp.SmsDefaultApp;

public class SauvegardeMessages extends Sauvegarde
{
	private static final String FILTRE_MESSAGES = "*.html";
	public static String REPERTOIRE = "Messages";
	private int _nbMessages;

	/***
	 * Fait la restauration
	 * @param t
	 * @return
	 */
	@Override protected STATUS executeRestauration(final RestaurationAsynchrone t)
	{
		// Restriction de securite du systeme Android, cette application doit etre l'application de
		// gestion des SMS par defaut pour etre autorisee à creer des SMS
		if (!SmsDefaultApp.isDefaultApp(_context))
			return STATUS.FAILED;

		REPERTOIRE = Preferences.getInstance(_context).getString(Preferences.PREF_REPERTOIRE_MESSAGES, REPERTOIRE);
		ConnexionSauvegarde connexion = null;

		try
		{
			connexion = getConnexion();
			String[] listeRepertoires = connexion.getListeRepertoires(REPERTOIRE);
			if (listeRepertoires == null)
				return STATUS.FAILED;

			int current = 0;
			_nbMessages = listeRepertoires.length;
			for (String rep : listeRepertoires)
			{
				// Chaque repertoire correspond a un correspondant
				try
				{
					current++;
					t.publieProgres(current);
					String repertoire = FileUtils.Combine(REPERTOIRE, rep);
					String[] listeMessages = connexion.getListeFichiers(repertoire, FILTRE_MESSAGES);
					if (listeMessages == null)
						return STATUS.FAILED;

					// Parcourir la liste des messages dans ce repertoire
					for (String c : listeMessages)
					{
						SavedMessage message = new SavedMessage(FileUtils.Combine(repertoire, c), connexion);
						message.restaure(_context);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return STATUS.FAILED;
		} finally
		{
			if (connexion != null)
				connexion.close();
		}
		return _annule ? STATUS.CANCELED : STATUS.OK;
	}

	/***
	 * Compte les elements qu'il y aura a sauvegarder dans une tache en arriere plan, previent un
	 * listener quand les elements sont comptés
	 * @param context
	 * @param listener
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void Compte(@NonNull final Context context, @NonNull final CompteObjetsListener listener)
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
				Cursor cursor = SavedMessage.getCursor(context);
				if (cursor == null)
					return 0;

				int nombre = cursor.getCount();
				cursor.close();
				return nombre;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/***
	 * Retrouve le nombre d'objets a sauvegarder
	 * @return
	 */
	@Override protected int getSauvegardeCount()
	{
		return _nbMessages;
	}

	/***
	 * Fait la sauvegarde
	 * @param t
	 * @return
	 */
	@Override protected STATUS executeSauvegarde(final SauvegardeAsynchrone t)
	{
		ConnexionSauvegarde connexion = null;
		REPERTOIRE = Preferences.getInstance(_context).getString(Preferences.PREF_REPERTOIRE_MESSAGES, REPERTOIRE);
		try
		{
			// Socket de communication avec le serveur
			connexion = getConnexion();

			// Curseur pour parcourir les contacts d'Android
			Cursor cursor = SavedMessage.getCursor(_context);
			if (cursor == null)
				return STATUS.FAILED;

			// Creer le repertoire de sauvegarde des contacts
			connexion.creerRepertoire(REPERTOIRE);

			_nbMessages = cursor.getCount();   // Nombre de messages total
			int current = 0;

			while (cursor.moveToNext() && !_annule)
			{
				current++;
				t.publieProgres(current);
				SavedMessage message = new SavedMessage(cursor, _context);
				Log.d(TAG, "Appel " + message.Nom(_context));

				// Creation d'un fichier pour cet appel
				String categorie = FileUtils.Combine(REPERTOIRE, message.getCategorie(_context));

				// Chemin pour cet appel
				String appelPath = FileUtils.cleanPathName(FileUtils.Combine(categorie, message.getFileName(_context)));
				if (!connexion.exists(appelPath))
				{
					connexion.creerRepertoire(categorie);
					message.sauvegarde(connexion, _context, appelPath);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return STATUS.FAILED;
		} finally
		{
			if (connexion != null)
				connexion.close();
		}

		return _annule ? STATUS.CANCELED : STATUS.OK;
	}

	public SauvegardeMessages( final Context c, InterfaceSauvegarde interfce, final SauvegardeListener listener)
	{
		super(c, interfce, listener);
	}

	/***
	 * Retourne true si cette restauration necessite une permission/configuration du systeme pour la restauration
	 * @param context
	 * @return
	 */
	@Override
	public boolean needSystemActionForRestauration(final Context context)
	{
		// Restriction de securite d'Android: l'application doit être l'application SMS par defaut
		// pour avoir le droit de creer des SMS
		// https://android-developers.googleblog.com/2013/10/getting-your-sms-apps-ready-for-kitkat.html
		if (SmsDefaultApp.isDefaultApp(context))
			return false;

		// Afficher une fenetre pour inviter l'utilisateur a modifier l'application par defaut
		MessageBoxUtils.messageBox(context,
				context.getString(R.string.sms_app_defaut),
				context.getString(R.string.sms_app_defaut_texte),
				MessageBoxUtils.BOUTON_OK | MessageBoxUtils.BOUTON_CANCEL,
				new MessageBoxUtils.Listener()
				{
					public void onOk()
					{
						// Afficher le dialogue permettant de selectionner l'appli par defaut pour les SMS
						SmsDefaultApp.setDefaultSMSApp(context, true);
					}

					public void onCancel()
					{

					}
				});
		return true;
	}

	public void systemActionPostRestauration(final Context context)
	{
		// Restriction de securite d'Android: l'application doit être l'application SMS par defaut
		// pour avoir le droit de creer des SMS
		// https://android-developers.googleblog.com/2013/10/getting-your-sms-apps-ready-for-kitkat.html
		if (! SmsDefaultApp.isDefaultApp(context))
			return ;

		// Afficher une fenetre pour inviter l'utilisateur a modifier l'application par defaut
		MessageBoxUtils.messageBox(context,
				context.getString(R.string.sms_app_defaut),
				"Maintenant que la restauration est terminée, vous pouvez remettre votre application de gestion des SMS par défaut. Le système Android va afficher un dialogue vous permettant de choisir une application, veuillez choisir votre application par défaut et valider",
				MessageBoxUtils.BOUTON_OK,
				new MessageBoxUtils.Listener()
				{
					public void onOk()
					{
						// Afficher le dialogue permettant de selectionner l'appli par defaut pour les SMS
						SmsDefaultApp.setDefaultSMSApp(context, false);
					}

					public void onCancel()
					{

					}
				});
	};

}
