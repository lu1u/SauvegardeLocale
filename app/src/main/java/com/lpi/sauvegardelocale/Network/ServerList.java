package com.lpi.sauvegardelocale.Network;
/***
 * Recherche multitache de serveurs accessibles sur le reseau
 */

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lpi.sauvegardelocale.Sauvegardes.ConnexionSauvegarde;
import com.lpi.sauvegardelocale.utils.Preferences;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerList
{
	public static final String TAG = "ServerList";
	static int _nbRecherchesEnCours;
	static int _nbTestes;
	static int _port; // Le port sur lequel on s'attend a trouver un adresse de sauvegarde


	public static class Serveur
	{
		public String adresse;
		public String nom;
		public boolean contacte;
	}

	private static ArrayList<Serveur> _serveurs = new ArrayList<>();

	public static ArrayList<Serveur> getServeurs()
	{
		return _serveurs;
	}

	public static void stop()
	{
		_stop = true;
	}


	public interface ServerListListener
	{
		void onNouveauServeurJoignable(int nbTrouves);
		void onRechercheFinie(int nbTrouves);
		void onRechercheCommence();
		void onNewServerTested(int nbTested, int nbMax);
	}


	private static class ChercheServeurs extends AsyncTask<Void, Serveur, Integer>
	{
		int _min, _max;

		public ChercheServeurs(int min, int max)
		{
			_min = min;
			_max = max;
		}

		/**
		 * Demarrage d'une recherche
		 */
		@Override
		protected void onPreExecute()
		{
			if (_serverListListener != null)
				_serverListListener.onRechercheCommence();
		}

		/**
		 * Fin d'une recherche
		 */
		protected void onPostExecute(Integer result)
		{
			Log.d(TAG, "ChercheServeur " + _min + "..." + _max + " terminé!!!");
			_nbRecherchesEnCours--;
			if (_nbRecherchesEnCours <= 0 && _serverListListener != null)
			{
				_serverListListener.onRechercheFinie(result);
				_context = null;
			}
		}


		/***
		 * Mise a jours de la progression
		 * @param values
		 */
		@Override
		protected void onProgressUpdate(final Serveur... values)
		{
			super.onProgressUpdate(values);
			if (_serverListListener != null)
			{
				if (values != null && values[0] != null)
				{
					// Nouveau adresse
					Serveur se = values[0];
					if (nouvelleAdresse(se.adresse))
						_serveurs.add(se);

					_serverListListener.onNouveauServeurJoignable(_serveurs.size());
				}
				else
					// Adresse testee, pas joignable
					_serverListListener.onNewServerTested(_nbTestes, 255);
			}
		}

		@Override
		protected Integer doInBackground(final Void... v)
		{
			String prefix = getSubNetworkPrefix();
			if (prefix == null)
				return 0;

			for (int i = _min; i <= _max; i++)
			{
				if (_stop)
				{
					Log.d(TAG, "Thread " + _min + "..." + _max + " stop");
					break;
				}

				try
				{
					String testIp = prefix + i;
					Log.i(TAG, "Essai de l'adresse " + testIp);
					InetAddress address = InetAddress.getByName(testIp);
					if (nouvelleAdresse(address.getHostAddress()))
						if (NetworkUtils.isReachable(address))
						{
							Log.i(TAG, address.getCanonicalHostName() + "(" + testIp + ") is reachable!");
							Serveur serveur = new Serveur();
							serveur.adresse = address.getHostAddress();
							try
							{
								ConnexionSauvegarde c = new ConnexionSauvegarde(_context, serveur.adresse, _port);
								serveur.contacte = c.testeConnexion();
								c.close();
							} catch (Exception e)
							{
								serveur.contacte = false;
							}
							serveur.nom = address.getHostName();
							publishProgress(serveur);
						}

					_nbTestes++;
					publishProgress((Serveur) null);
				} catch (UnknownHostException e)
				{
					Log.e(TAG, e.getLocalizedMessage());
				}
			}
			return 0;
		}

		/***
		 * Retourne vrai si l'adresse est nouvelle dans la liste
		 * @param hostAddress
		 * @return
		 */
		private boolean nouvelleAdresse(final String hostAddress)
		{
			for (Serveur s : _serveurs)
				if (s.adresse.equals(hostAddress))
					return false;

			return true;
		}


		/***
		 * Retourne le prefixe pour toutes les adresses du reseau local
		 * @return
		 */
		private @Nullable
		String getSubNetworkPrefix()
		{
			String network = NetworkUtils.getActiveNetwork(_context);
			if (network == null)
				return null;
			return network.substring(0, network.lastIndexOf(".") + 1);
		}
	}

	private static ServerListListener _serverListListener;
	private static Context _context;
	private static boolean _stop = false;

	/***
	 * Lancement de la recherche de serveurs
	 * @param c
	 * @param listener
	 */
	public static int chercheServeurs(@NonNull Context c, @NonNull ServerListListener listener, int port)
	{
		final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
		int nb = Math.min(CPU_COUNT / 2, Preferences.getInstance(c).getInt(Preferences.PREF_CHERCHESERVEURS_NBTHREADS, 4));
		Log.d(TAG, "Création de " + nb + " threads pour rechercher les serveurs");

		verifAdressesConnues();

		_context = c;
		_nbRecherchesEnCours = 0;
		_nbTestes = 0;
		_port = port;
		_serverListListener = listener;

		int min = 0;
		int pas = Math.min(255, (256 / nb) + 1);

		while (min < 256)
		{
			int max = Math.min(255, min + pas);
			_nbRecherchesEnCours++;

			ChercheServeurs task = new ChercheServeurs(min, max);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			min = max + 1;
		}
		return nb;
	}

	private static void verifAdressesConnues()
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected void onProgressUpdate(final Void... values)
			{
				super.onProgressUpdate(values);
				_serverListListener.onNouveauServeurJoignable(_serveurs.size());
			}

			@Override
			protected Void doInBackground(final Void... voids)
			{
				if ( _serveurs!=null)
					for (Serveur s: _serveurs)
					{
						try
						{
							if ( NetworkUtils.isReachable( InetAddress.getByName(s.adresse)))
							{
								publishProgress();
								break;
							}
						} catch (UnknownHostException e)
						{
						}
					}
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}

