package com.lpi.sauvegardelocale.Sauvegardes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lpi.sauvegardelocale.utils.FileUtils;

public abstract class Sauvegarde
{
	public static final String TAG = "Sauvegarde";
	protected static boolean _annule = false;

	/***
	 * Retourne true si cette restauration necessite une permission/configuration du systeme pour la restauration
	 * @param Activity
	 * @return
	 */
	public 	boolean needSystemActionForRestauration(final Context Activity)
	{
		return false;
	}

	public void systemActionPostRestauration(final Context context){};


	public interface CompteObjetsListener
	{
		void onObjetsComptes(int nb);
	}

	public static void setConnexion(@NonNull String serveur, final int port)
	{
		_serveur = serveur;
		_port = port;
	}

	public static void annule()
	{
		_annule = true;
	}

	public enum STATUS
	{
		OK, FAILED, CANCELED, NOTHING
	}

	/***
	 * Interface de communication avec l'appelant
	 */
	public interface SauvegardeListener
	{
		void onDebutSauvegarde();
		void onFinSauvegarde(STATUS status);
		void onSauvegardeProgress(InterfaceSauvegarde intrfce, int progress, int total);
	}

	public interface RestaurationListener
	{
		void onDebutRestauration();
		void onFinRestauration(STATUS status);
		void onRestaurationProgress(InterfaceSauvegarde intrfce, int progress, int total);
	}

	protected abstract int getSauvegardeCount();

	protected abstract STATUS executeSauvegarde(@NonNull SauvegardeAsynchrone t);
	protected abstract STATUS executeRestauration(@NonNull RestaurationAsynchrone t);

	private static String _serveur;
	private static int _port;

	protected SauvegardeListener _listener;
	protected Context _context;
	protected InterfaceSauvegarde _interfce;

	protected @NonNull
	ConnexionSauvegarde getConnexion() throws Exception
	{
		return new ConnexionSauvegarde(_context, _serveur, _port);
	}

	protected class RestaurationAsynchrone extends AsyncTask<Void, Integer, STATUS>
	{
		RestaurationListener _rListener;
		RestaurationAsynchrone(@NonNull RestaurationListener listener)
		{
			_rListener = listener;
		}
		@Override
		protected void onPreExecute()
		{
			Log.d(TAG, "preexecute " + this.getClass().getSimpleName());
			if (_rListener != null)
				_rListener.onDebutRestauration();
		}

		/**
		 * Update list ui after process finished.
		 */
		protected void onPostExecute(STATUS result)
		{
			Log.d(TAG, "postexecute " + this.getClass().getSimpleName());
			if (_rListener != null)
				_rListener.onFinRestauration(result);
		}

		@Override
		protected void onProgressUpdate(final Integer... values)
		{
			Log.d(TAG, "postupdate" + this.getClass().getSimpleName());

			super.onProgressUpdate(values);
			if (_rListener != null)
				_rListener.onRestaurationProgress(_interfce, values[0], getSauvegardeCount());
		}

		@Override
		protected STATUS doInBackground(final Void... voids)
		{
			try
			{
				if (FileUtils.getInvalidPathChar() == null)
				{
					ConnexionSauvegarde connexion = new ConnexionSauvegarde(_context, _serveur, _port);
					FileUtils.setInvalidPathChar(connexion.getInvalidPathChars());
					connexion.close();
				}
				return executeRestauration(this);
			} catch (Exception e)
			{
				e.printStackTrace();
				return STATUS.FAILED;
			}
		}

		public void publieProgres(int progres)
		{
			publishProgress(progres);
		}
	}

	protected class SauvegardeAsynchrone extends AsyncTask<Void, Integer, STATUS>
	{
		@Override
		protected void onPreExecute()
		{
			Log.d(TAG, "preexecute " + this.getClass().getSimpleName());
			if (_listener != null)
				_listener.onDebutSauvegarde();
		}

		/**
		 * Update list ui after process finished.
		 */
		protected void onPostExecute(STATUS result)
		{
			Log.d(TAG, "postexecute " + this.getClass().getSimpleName());
			if (_listener != null)
				_listener.onFinSauvegarde(result);
		}

		@Override
		protected void onProgressUpdate(final Integer... values)
		{
			Log.d(TAG, "postupdate" + this.getClass().getSimpleName());

			super.onProgressUpdate(values);
			if (_listener != null)
				_listener.onSauvegardeProgress(_interfce, values[0], getSauvegardeCount());
		}

		@Override
		protected STATUS doInBackground(final Void... voids)
		{
			try
			{
				if (FileUtils.getInvalidPathChar() == null)
				{
					ConnexionSauvegarde connexion = new ConnexionSauvegarde(_context, _serveur, _port);
					FileUtils.setInvalidPathChar(connexion.getInvalidPathChars());
					connexion.close();
				}
				return executeSauvegarde(this);
			} catch (Exception e)
			{
				e.printStackTrace();
				return STATUS.FAILED;
			}
		}

		public void publieProgres(int progres)
		{
			publishProgress(progres);
		}
	}

	protected Sauvegarde(@NonNull Context c, @NonNull InterfaceSauvegarde interfce, @NonNull SauvegardeListener listener)
	{
		_interfce = interfce;
		_context = c;
		_listener = listener;
	}

	public final void execute()
	{
		Log.d(TAG, "Lancement sauvegarde asynchrone " + this.getClass().getSimpleName());
		// Lancement d'une tache asynchrone pour sauvegarder les objets
		SauvegardeAsynchrone task = new SauvegardeAsynchrone();
		_annule = false;

		if (Build.VERSION.SDK_INT >= 11)
		{
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			task.execute();
		}
	}

	public final void executeRestauration(@NonNull RestaurationListener listener)
	{
		Log.d(TAG, "Lancement restauration asynchrone " + this.getClass().getSimpleName());
		// Lancement d'une tache asynchrone pour sauvegarder les objets
		RestaurationAsynchrone task = new RestaurationAsynchrone(listener);
		_annule = false;

		if (Build.VERSION.SDK_INT >= 11)
		{
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			task.execute();
		}
	}
}
