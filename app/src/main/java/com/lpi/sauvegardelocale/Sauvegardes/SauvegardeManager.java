package com.lpi.sauvegardelocale.Sauvegardes;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.lpi.sauvegardelocale.Network.Connexion;
import com.lpi.sauvegardelocale.R;
import com.lpi.sauvegardelocale.utils.SnackbarUtils;

import java.util.ArrayList;

public class SauvegardeManager
{
	private String _serveur;
	private int _port;


	public interface ActionSiConnecte
	{
		public void onServeurConnecte(boolean connecte);
	}

	/***
	 * Teste la connexion et averti si OK ou Non
	 */
	static public void actionSiConnecte(@NonNull final Context context, @NonNull final String serveur, final int port, @NonNull final ActionSiConnecte action)
	{
		new AsyncTask<Void, Void, Boolean>()
		{
			@Override
			protected Boolean doInBackground(final Void... voids)
			{
				boolean resultat;
				try
				{
					ConnexionSauvegarde connexion = new ConnexionSauvegarde(context, serveur, port);
					resultat = connexion.testeConnexion();
					connexion.close();
				} catch (Exception e)
				{
					System.err.println("Impossible de contacter " + serveur);
					System.err.println(e.getLocalizedMessage());
					resultat = false;
				}

				return resultat;
			}

			@Override
			protected void onPostExecute(final Boolean aBoolean)
			{
				super.onPostExecute(aBoolean);
				action.onServeurConnecte(aBoolean);
			}
		}.execute();//OnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
