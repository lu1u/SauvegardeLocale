package com.lpi.sauvegardelocale.Sauvegardes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lpi.sauvegardelocale.Sauvegardes.database.DatabaseHelper;
import com.lpi.sauvegardelocale.utils.Preferences;

/***
 * Contient les parametres d'un profil de sauvegarde
 */
public class ProfilSauvegarde
{
	private static final String SEPARATEUR = "!";
	public static int OPTION_CONTACTS = 0b0000001;
	public static int OPTION_APPELS = 0b0000010;
	public static int OPTION_MESSAGES = 0b0000100;
	public static int OPTION_PHOTOS = 0b0001000;
	public static int OPTION_VIDEOS = 0b0010000;
	public static int OPTION_MMS = 0b0100000;
	private static final int TOUTES_OPTIONS = OPTION_CONTACTS|OPTION_APPELS|OPTION_MESSAGES|OPTION_MMS|OPTION_PHOTOS|OPTION_VIDEOS ;

	public String nom;
	public String adresse;
	public int port;
	public boolean contacts, appels, messages, MMS, photos, videos;
	public int Id;

	public ProfilSauvegarde()
	{
	}

	public ProfilSauvegarde(final Cursor cursor)
	{
		if (cursor != null)
		{
			Id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLONNE_PROFIL_ID));
			nom = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLONNE_PROFIL_NOM));
			String connexion = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLONNE_PROFIL_CONNEXION));
			fromConnexion(connexion);

			int options = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLONNE_PROFIL_OPTIONS));
			fromMasqueOption(options);

		}
	}

	private void fromMasqueOption(final int options)
	{
		contacts = (options & OPTION_CONTACTS) != 0;
		appels = (options & OPTION_APPELS) != 0;
		messages = (options & OPTION_MESSAGES) != 0;
		MMS = (options & OPTION_MMS) != 0;
		photos = (options & OPTION_PHOTOS) != 0;
		videos = (options & OPTION_VIDEOS) != 0;
	}

	public void toContentValues(final ContentValues content, final boolean putId)
	{
		if (putId)
			content.put(DatabaseHelper.COLONNE_PROFIL_ID, Id);
		content.put(DatabaseHelper.COLONNE_PROFIL_NOM, nom);
		content.put(DatabaseHelper.COLONNE_PROFIL_CONNEXION, getConnexion());

		int options = getMasqueOptions();
		content.put(DatabaseHelper.COLONNE_PROFIL_OPTIONS, options);
	}

	private String getConnexion()
	{
		return adresse + SEPARATEUR + port;
	}

	private void fromConnexion(String connexion)
	{
		port = ConnexionSauvegarde.PORT_PAR_DEFAUT;
		if (connexion == null)
			return;

		String[] st = connexion.split(SEPARATEUR);
		if (st != null && st.length > 1)
		{
			adresse = st[0];
			port = Integer.parseInt(st[1]);
		}
	}

	private int getMasqueOptions()
	{
		int options = 0;
		options |= contacts ? OPTION_CONTACTS : 0;
		options |= appels ? OPTION_APPELS : 0;
		options |= messages ? OPTION_MESSAGES : 0;
		options |= MMS ? OPTION_MMS : 0;
		options |= photos ? OPTION_PHOTOS : 0;
		options |= videos ? OPTION_VIDEOS : 0;
		return options;
	}

	public void saveToPreferences(Context context)
	{
		Preferences p = Preferences.getInstance(context);
		p.put(Preferences.PREF_CONNEXION, getConnexion());
		p.put(Preferences.PREF_OPTIONS, getMasqueOptions());
	}

	public void fromPreferences(Context context)
	{
		Preferences p = Preferences.getInstance(context);
		String connexion = p.getString(Preferences.PREF_CONNEXION, "");
		fromConnexion(connexion);

		int options = p.getInt(Preferences.PREF_OPTIONS, TOUTES_OPTIONS);
		fromMasqueOption(options);
	}
}
