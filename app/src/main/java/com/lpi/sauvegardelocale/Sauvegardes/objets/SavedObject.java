package com.lpi.sauvegardelocale.Sauvegardes.objets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.lpi.sauvegardelocale.Sauvegardes.ConnexionSauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.Sauvegarde;
import com.lpi.sauvegardelocale.utils.StringOutputStream;

import java.io.BufferedOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Classe de base pour les objets sauvegardes
 * Created by lucien on 29/01/2016.
 */
public abstract class SavedObject
{
	private static final String TAG = "SavedObject";

	public static final String BEGIN_DATA = "\n<<<DATA>>>";
	public static final String ENDDATA = "\n<<<ENDDATA>>>";
	public static final String DEBUT_MOTCLE = "@@@";
	public static final String FIN_MOTCLE = "@@@";

	// Hashmap qui contient les informations de l'objets enregistrees dans les bases de donnees Android
	protected HashMap<String, String> _attributs;

	/***
	 * Lit les colonnes de l'enregistrement courant du curseur et les met dans la hashmap sous
	 * la forme NOM_DE_COLONNE = VALEUR
	 * @param curseur
	 */
	protected void litColonnes(Cursor curseur)
	{
		_attributs = new HashMap<>();
		for (int i = 0; i < curseur.getColumnCount(); i++)
		{
			_attributs.put(curseur.getColumnName(i), curseur.getString(i));
		}
	}

	/***
	 * Obtient un entier a partir des attributs
	 * @param nom
	 * @return
	 */
	protected int getInt(@NonNull String nom)
	{
		String val = _attributs.get(nom);
		if (val == null)
			return 0;

		try
		{
			return Integer.parseInt(val);
		} catch (Exception e)
		{
			return 0;
		}
	}

	/***
	 * Obtient un long a partir des attributs
	 * @param nom
	 * @return
	 */
	protected long getLong(@NonNull String nom)
	{
		String val = _attributs.get(nom);
		if (val == null)
			return 0;

		try
		{
			return Long.parseLong(val);
		} catch (Exception e)
		{
			return 0;
		}
	}

	protected long extraitLong(@NonNull String line, @NonNull String lineReader)
	{
		String v = extrait(line, lineReader);
		try
		{
			return Long.parseLong(v);
		} catch (Exception e)
		{

		}
		return 0;
	}

	protected int extraitInt(@NonNull String line, @NonNull String lineReader)
	{
		String v = extrait(line, lineReader);
		try
		{
			return Integer.parseInt(v);
		} catch (Exception e)
		{

		}
		return 0;
	}

	@NonNull
	protected String extrait(@NonNull String name, @NonNull String lineReader)
	{
		return lineReader.substring(name.length()).trim();
	}

	/***
	 * Extrait la clef dans une chaine de type @@@MOT_CLEF@@@=valeur
	 * @param ligne
	 * @return
	 */
	@NonNull private String extraitKey(@NonNull String ligne)
	{
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < ligne.length() && ligne.charAt(i) != '=')
			sb.append(ligne.charAt(i));
		String s =  sb.toString();

		if ( s.startsWith(DEBUT_MOTCLE))
			s = s.substring(DEBUT_MOTCLE.length());

		int indice = s.indexOf(FIN_MOTCLE);
		if ( indice != -1)
			s = s.substring(0, indice-1);

		return s;
	}


	/***
	 * Extrait la valeur dans une chaine de type @@@MOT_CLEF@@@=valeur
	 * @param ligne
	 * @return
	 */
	@NonNull private String extraitValue(@NonNull String ligne)
	{
		StringBuilder sb = new StringBuilder();
		int indiceDepart = ligne.indexOf('=');
		if (indiceDepart == -1)
			sb.append(ligne);
		else
		{
			int i = indiceDepart + 1;
			while (i < ligne.length())
				sb.append(ligne.charAt(i));
		}
		return sb.toString();
	}

	/***
	 * Interprete le tableau de lignes pour obtenir la liste des attributs
	 * @param reader
	 */
	protected void litAttributs(String[] reader)
	{
		// Chercher le debut des donnees
		int indiceLigne = 0;
		// Chercher la partie DATA
		boolean data = false;
		while (indiceLigne < reader.length && !data)
		{
			if ("<<<DATA>>>".equals(reader[indiceLigne]))
				data = true;

			indiceLigne++;
		}

		String key = null, value = null ;
		while (indiceLigne < reader.length && data)
		{
			if (reader[indiceLigne].startsWith("<<<ENDDATA>>>"))
			{
				data = false;
				break;              // Sortie de la boucle
			}

			// Si la ligne commence par DEBUT_MOTCLE, c'est un nouveau mot cle, sinon c'est la suite du mot cle courant
			if (reader[indiceLigne].startsWith(DEBUT_MOTCLE))
			{
				// Nouvelle valeur, enregistrer la valeur dans la hashmap
				if (key != null && value != null)
				{
					_attributs.put(key, value);
					key = null;
					value = null;
				}

				key = extraitKey(reader[indiceLigne]);
				value = extraitValue(reader[indiceLigne]);
			}
			else
			{
				// Concatener la ligne a la suite de la valeur
				if (value == null)
					value = reader[indiceLigne];
				else
					value += "\n" + reader[indiceLigne];
			}

			indiceLigne++;
		}

		if (key != null && value != null)
			_attributs.put(key, value);

	}

	/***
	 * Ecrit la liste des attributs dans un output stream sous la forme:
	 * @@@attribut@@@=valeur
	 * @param sops
	 * @throws Exception
	 */
	protected void writeAttributs(final StringOutputStream sops) throws Exception
	{
		if (_attributs != null)
			for (Map.Entry<String, String> entry : _attributs.entrySet())
			{
				String key = entry.getKey();
				if (key != null)
				{
					String value = entry.getValue();
					if (value != null)
						sops.write(("\n" + DEBUT_MOTCLE + key + FIN_MOTCLE + "=" + value).getBytes());
				}
			}
	}


	/***
	 * Ecrit la liste des attributs dans un stringBuilder sous la forme:
	 * @@@attribut@@@=valeur
	 * @param b
	 * @throws Exception
	 */
	protected void writeAttributs(@NonNull StringBuilder b) throws Exception
	{
		if (_attributs != null)
			for (Map.Entry<String, String> entry : _attributs.entrySet())
			{
				String key = entry.getKey();
				if (key != null)
				{
					String value = entry.getValue();
					if (value != null)
						b.append("\n" + DEBUT_MOTCLE + key + FIN_MOTCLE + "=" + value);
				}
			}
	}

	/***
	 * Obtient tous les attributs dans une chaine de carateres
	 * @return
	 * @throws Exception
	 */
	protected @NonNull
	String getAllData() throws Exception
	{
		StringBuilder sb = new StringBuilder();
		writeAttributs(sb);
		return sb.toString();
	}


	public static String sqliteDateToString(@NonNull Context context, long l)
	{
		try
		{
			return android.text.format.DateFormat.getDateFormat(context).format(new Date(l));
		} catch (Exception e)
		{
			return l + " (format de date non reconnue)"; //$NON-NLS-1$
		}
	}

	public static String sqliteDateHourToString(long l)
	{
		try
		{
			@SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Date date = new Date(l);
			return dateFormat.format(date);
		} catch (Exception e)
		{
			return l + " (format de date non reconnue)"; //$NON-NLS-1$
		}
	}

	/***
	 * Converti en texte une valeur representant une duree en secondes
	 *
	 * @param l
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String sqliteDurationToString(long l)
	{
		try
		{
			int secondes = (int) l % 60;
			l /= 60;
			int minutes = (int) l % 60;
			l /= 60;
			int heures = (int) l;

			return String.format("%1$02dh %2$02dm %3$02ds", heures, minutes, secondes);
		} catch (Exception e)
		{
			return l + " (format de date non reconnue)"; //$NON-NLS-1$
		}
	}

	public abstract Sauvegarde.STATUS sauvegarde(ConnexionSauvegarde connexion, Context context, String racine) throws Exception;

	public boolean quelqueChoseASauvegarder()
	{
		return true;
	}


	@NonNull
	public abstract String Nom(@NonNull Context context);


	public static boolean storeImage(Bitmap imageData, BufferedOutputStream bos, Bitmap.CompressFormat imageFormat)
	{
		imageData.compress(imageFormat, 100, bos);
		return true;
	}

	//public abstract void restore(Context context);
}
