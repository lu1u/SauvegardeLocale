package com.lpi.sauvegardelocale.Sauvegardes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.lpi.sauvegardelocale.Sauvegardes.ProfilSauvegarde;

public class ProfilsDatabase
{
	public static final int INVALID_ID = -1;

	/**
	 * Instance unique non préinitialisée
	 */
	private static ProfilsDatabase INSTANCE = null;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	public static synchronized ProfilsDatabase getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ProfilsDatabase(context);
		}
		return INSTANCE;
	}

	private ProfilsDatabase(Context context)
	{
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	@Override
	public void finalize()
	{
		dbHelper.close();
	}

	/***
	 * Retourne un profil cree a partir de la base de donnnees
	 *
	 * @param Id
	 * @return profil
	 */
	public @Nullable ProfilSauvegarde getProfil(int Id)
	{
		ProfilSauvegarde profil = null;
		Cursor cursor = null;
		try
		{
			String[] colonnes = null;
			String where = DatabaseHelper.COLONNE_PROFIL_ID + " = " + Id;
			cursor = database.query(DatabaseHelper.TABLE_PROFILS, colonnes, where, null, null, null, null);
			cursor.moveToFirst();
			profil = new ProfilSauvegarde(cursor);
			return profil;
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			if (cursor != null)
				cursor.close();
		}

		return profil;

	}
	/***
	 * Compter les profils presents dans la base
	 * @return
	 */
	public int nbProfils()
	{
		int count = 0;
		try
		{
			Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM " + DatabaseHelper.TABLE_PROFILS, null);
			if (null != cursor)
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					count = cursor.getInt(0);
				}
			cursor.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return count;
	}
	public boolean ajouteProfil(ProfilSauvegarde profil)
	{
		ContentValues initialValues = new ContentValues();
		profil.toContentValues(initialValues, false);

		try
		{
			profil.Id = (int) database.insert(DatabaseHelper.TABLE_PROFILS, null, initialValues);
		} catch (Exception e)
		{
			//MainActivity.SignaleErreur("ajout du profil", e);
			return false;
		}

		return true;
	}

	public Cursor getCursor()
	{
		return database.query(DatabaseHelper.TABLE_PROFILS, null, null, null, null, null, null);
	}

	/***
	 * Suppprime le profil
	 * @param id
	 */
	public void deleteProfil(final int id)
	{
		try
		{
			database.delete(DatabaseHelper.TABLE_PROFILS, DatabaseHelper.COLONNE_PROFIL_ID + " = " + id, null);
		} catch (Exception e)
		{
			//MainActivity.SignaleErreur("suppression profil", e);
		}
	}
}
