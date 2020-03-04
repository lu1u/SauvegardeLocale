package com.lpi.sauvegardelocale.utils;
/***
 * Gestion des preferences de l'appli
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lpi.sauvegardelocale.Sauvegardes.database.DatabaseHelper;

public class Preferences
{
	public static final String PREF_CONNEXION = "Connexion" ;
	public static final String PREF_OPTIONS = "Options";
	public static final String PREF_REPERTOIRE_CONTACTS = "RepertoireContacts";
	public static final String PREF_REPERTOIRE_APPELS = "RepertoireAppels";
	public static final String PREF_REPERTOIRE_MESSAGES = "RepertoireMessages";
	public static final String PREF_REPERTOIRE_MMS = "RepertoireMMS";
	public static final String PREF_REPERTOIRE_PHOTOS = "RepertoirePhotos";
	public static final String PREF_REPERTOIRE_VIDEOS = "RepertoireVideos";
	public static final String PREF_CHERCHESERVEURS_NBTHREADS = "NbThreadsChercheServeurs";
	public static final String PREF_MODE_UI = "ModeUi";
	private static final String TAG = "Preferences";

	private static Preferences INSTANCE = null;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	public static synchronized Preferences getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Preferences(context);
		}
		return INSTANCE;
	}

	private Preferences(Context context)
	{
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void put(final String nom, final String valeur)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(DatabaseHelper.COLONNE_PREFERENCE_NOM, nom);
		initialValues.put(DatabaseHelper.COLONNE_PREFERENCE_VALEUR, valeur);

		try
		{
			database.insertWithOnConflict(DatabaseHelper.TABLE_PREFERENCES, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
		} catch (Exception e)
		{
			Log.e(TAG, "Erreur a la sauvegarde d'une preference");
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
	public void put(final String nom, final int valeur)
	{
		put(nom, Integer.toString(valeur));
	}

	public String getString(final String nom, final String defaut)
	{
		String res = defaut;
		Cursor cursor = null;
		try
		{
			String where = DatabaseHelper.COLONNE_PROFIL_NOM + " = '" + nom + "'";
			String []COLONNES = { DatabaseHelper.COLONNE_PREFERENCE_VALEUR };
			cursor = database.query(DatabaseHelper.TABLE_PREFERENCES, COLONNES, where, null, null, null, null);
			cursor.moveToFirst();
			res = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLONNE_PREFERENCE_VALEUR));
		} catch (Exception e)
		{
			Log.e(TAG, "Erreur a la récupération de la preference " + nom);
			Log.e(TAG, e.getLocalizedMessage());
			e.printStackTrace();
		} finally
		{
			if (cursor != null)
				cursor.close();
		}

		return res;
	}

	public int getInt(final String nom, final int defaut)
	{
		String s = getString(nom, null);
		if (s == null)
			return defaut;

		return Integer.parseInt(s);
	}
}
