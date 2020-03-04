package com.lpi.sauvegardelocale.Sauvegardes.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 6;
	public static final String DATABASE_NAME = "database.db";
	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table des profils de sauvegarde
	////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String TABLE_PROFILS = "PROFILS";
	public static final String COLONNE_PROFIL_ID = "_id";
	public static final String COLONNE_PROFIL_NOM = "NOM";
	public static final String COLONNE_PROFIL_CONNEXION = "CONNEXION";
	public static final String COLONNE_PROFIL_OPTIONS = "OPTIONS";

	private static final String DATABASE_PROFILS_CREATE = "create table IF NOT EXISTS "
			+ TABLE_PROFILS + "("
			+ COLONNE_PROFIL_ID + " integer primary key autoincrement, "
			+ COLONNE_PROFIL_NOM + " text not null,"
			+ COLONNE_PROFIL_CONNEXION + " text not null, "
			+ COLONNE_PROFIL_OPTIONS + " integer"
			+ ");";

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table des preferences
	////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String TABLE_PREFERENCES = "PREFERENCES" ;
	public static final String COLONNE_PREFERENCE_NOM = "NOM";
	public static final String COLONNE_PREFERENCE_VALEUR = "VALEUR";
	private static final String DATABASE_PREFERENCES_CREATE = "create table IF NOT EXISTS "
			+ TABLE_PREFERENCES + "("
			+ COLONNE_PREFERENCE_NOM + " text not null,"
			+ COLONNE_PREFERENCE_VALEUR + " text,"
			+ "CONSTRAINT PREFERENCE_NOM_UNIQUE UNIQUE ( " + COLONNE_PREFERENCE_NOM + ")"
			+ ");";




	@Override
	public void onCreate(SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_PROFILS_CREATE);
			database.execSQL(DATABASE_PREFERENCES_CREATE);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			Log.w(DatabaseHelper.class.getName(),"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			//db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILS);
			onCreate(db);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
