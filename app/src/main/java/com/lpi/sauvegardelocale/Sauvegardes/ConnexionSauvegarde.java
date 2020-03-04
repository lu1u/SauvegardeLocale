package com.lpi.sauvegardelocale.Sauvegardes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lpi.sauvegardelocale.Network.Connexion;
import com.lpi.sauvegardelocale.utils.FileUtils;
import com.lpi.sauvegardelocale.utils.Protocole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ConnexionSauvegarde
{
	public static final int PORT_PAR_DEFAUT = 50566; // Il faut bien une valeur par defaut...
	private static final String TAG = "ConnexionSauvegarde";
	private Context _context;
	private String _serveur;
	private int _port;
	//Connexion connexion;

	public ConnexionSauvegarde(@NonNull Context context, @NonNull String serveur, int port) throws Exception
	{
		_context = context;
		_serveur = serveur;
		_port = port;
		//connexion = new Connexion(_serveur, _port);
	}

	public void close()
	{
		try
		{
			//if (connexion != null)
			//{
			//	connexion.sendCommande(Protocole.FERMER, null);
			//	connexion.close();
			//}
		} catch (Exception e)
		{

		}

		//connexion = null;
	}


	/***
	 * Creer un repertoire sur le adresse
	 * @param repertoire
	 * @return
	 */
	public boolean creerRepertoire(@NonNull String repertoire) throws Exception
	{
		Log.d(TAG, "Creation du repertoire " + repertoire);
		Connexion connexion = new Connexion(_serveur, _port);
		connexion.sendCommande(Protocole.CREER_REPERTOIRE, repertoire);
		boolean res = connexion.readBoolResponse();
		connexion.close();
		return res;
	}

	/***
	 * Demande au adresse si un fichier existe
	 * @param path
	 * @return
	 */
	public boolean exists(final String path) throws Exception
	{
		Log.d(TAG, "Fichier existe? " + path);
		boolean res = false;
		Connexion connexion = null;
		try
		{
			connexion = new Connexion(_serveur, _port);
			connexion.sendCommande(Protocole.FICHIER_EXISTE, path);
			res = connexion.readBoolResponse();
			Log.d(TAG, "" + res);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			if (connexion != null) connexion.close();
		}

		return res;
	}


	/***
	 * Envoir un fichier texte au adresse
	 * @param path
	 * @param contenu
	 */
	public boolean envoiFichier(final String path, long date, final String contenu) throws Exception
	{
		Log.d(TAG, "envoi fichier " + path);
		boolean res = false;
		Connexion connexion = null;
		try
		{
			connexion = new Connexion(_serveur, _port);
			connexion.sendCommande(Protocole.TRANSFERT_FICHIER_DATE, path, connexion.date(date));
			connexion.envoiFichier(contenu);
			res = connexion.readBoolResponse();
			connexion.close();
			Log.d(TAG, "" + res);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			if (connexion != null) connexion.close();
		}

		return res;
	}

	/***
	 * Envoir un fichier texte au adresse
	 * @param path
	 * @param contenu
	 */
	public boolean envoiFichier(final String path, final String contenu) throws Exception
	{
		Log.d(TAG, "envoi fichier " + path);
		boolean res = false;
		Connexion connexion = null;
		try
		{
			connexion = new Connexion(_serveur, _port);
			connexion.sendCommande(Protocole.TRANSFERT_FICHIER, path);
			connexion.envoiFichier(contenu);
			res = connexion.readBoolResponse();
			connexion.close();
			Log.d(TAG, "" + res);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			if (connexion != null) connexion.close();
		}

		return res;
	}

	/***
	 * Envoir un fichier image au adresse
	 * @param path
	 * @param contenu
	 */
	public void envoiFichier(final String path, final Bitmap contenu) throws Exception
	{
		Log.d(TAG, "envoi fichier " + path);
		Connexion connexion = null;
		try
		{
			connexion = new Connexion(_serveur, _port);
			connexion.sendCommande(Protocole.TRANSFERT_FICHIER, path);
			// Vider le contenu de l'image dans un buffer d'octets
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			contenu.compress(Bitmap.CompressFormat.PNG, 100, bos);
			// Envoyer le buffer d'octets
			connexion.envoiFichier(bos.toByteArray());
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			if (connexion != null) connexion.close();
		}
	}

	/***
	 * Envoir un fichier image au adresse
	 * @param path
	 * @param contenu
	 */
	public void envoiFichier(final String path, long date, final Bitmap contenu) throws Exception
	{
		Log.d(TAG, "envoi fichier PNG " + path);
		envoiFichier(path, date, contenu, Bitmap.CompressFormat.PNG, ".png");
	}

	/***
	 * Envoir un fichier image au adresse
	 * @param path
	 * @param contenu
	 */
	public void envoiFichier(final String path, long date, final Bitmap contenu, Bitmap.CompressFormat format, String extension) throws Exception
	{
		Log.d(TAG, "envoi fichier image " + path + extension);

		Connexion connexion = new Connexion(_serveur, _port);
		connexion.sendCommande(Protocole.TRANSFERT_FICHIER_DATE, path + extension, connexion.date(date));
		// Vider le contenu de l'image dans un buffer d'octets
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		contenu.compress(format, 100, bos);

		// Envoyer le buffer d'octets
		connexion.envoiFichier(bos.toByteArray());
		connexion.close();
	}


	/**
	 * Envoie une requete pour modifier la date d'un fichier
	 */
	public void setDateFichier(final String path, final long date) throws Exception
	{
		Connexion connexion = new Connexion(_serveur, _port);
		connexion.sendCommande(Protocole.SET_DATE, path, connexion.date(date));
		connexion.close();
	}

	public void envoiFichier(final String path, final long date, final long longueur, final InputStream ips) throws Exception
	{
		Log.d(TAG, "envoi fichier " + path);

		Connexion connexion = new Connexion(_serveur, _port);
		connexion.sendCommande(Protocole.TRANSFERT_FICHIER_DATE, FileUtils.cleanPathName(path), connexion.date(date));
		connexion.envoiFichier(longueur, ips);
		connexion.close();
	}

	public String getInvalidPathChars() throws Exception
	{
		Connexion connexion = new Connexion(_serveur, _port);
		connexion.sendCommande(Protocole.INVALIDES_CHEMIN, null);
		String chars = connexion.readLine();
		connexion.close();
		Log.d(TAG, "Caracteres invalides" + chars);
		return chars;
	}

	public boolean testeConnexion() throws Exception
	{
		Connexion connexion = new Connexion(_serveur, _port);
		try
		{
			connexion.sendCommande(Protocole.TEST_ADRESSE, null);
			boolean res = connexion.readBoolResponse();
			connexion.close();
			return res;
		} catch (Exception e)
		{
			return false;
		}
	}

	/***
	 * Obtient la liste des fichiers dans un repertoire
	 * @param repertoire
	 * @param filtre
	 * @return
	 */
	public String[] getListeFichiers(final String repertoire, final String filtre) throws Exception
	{
		Connexion connexion = new Connexion(_serveur, _port);
		try
		{
			connexion.sendCommande(Protocole.LISTE_FICHIERS, repertoire, filtre);
			String res = connexion.readLine();
			connexion.close();
			return res.split(Connexion.SEPARATEUR_CHAINES);
		} catch (Exception e)
		{
			return null;
		}
	}


	/***
	 * Obtient la liste des sous repertoires dans un repertoire
	 * @param repertoire
	 * @return
	 */
	public String[] getListeRepertoires(final String repertoire) throws Exception
	{
		Connexion connexion = new Connexion(_serveur, _port);
		try
		{
			connexion.sendCommande(Protocole.LISTE_REPERTOIRES, repertoire);
			String res = connexion.readLine();
			connexion.close();
			return res.split(Connexion.SEPARATEUR_CHAINES);
		} catch (Exception e)
		{
			return null;
		}
	}
	/***
	 * Downloade une image
	 * @param imagePath
	 * @return
	 * @throws Exception
	 */
	public Bitmap getImage(final String imagePath) throws Exception
	{
		Bitmap res = null;
		Connexion connexion = new Connexion(_serveur, _port);
		connexion.sendCommande(Protocole.DOWNLOAD_FICHIER, imagePath);
		if (connexion.readBoolResponse())
		{
			byte[] bytes = connexion.recoitFichier();
			if (bytes == null)
				return null;
			InputStream is = new ByteArrayInputStream(bytes);
			res = BitmapFactory.decodeStream(is);
			is.close();
		}
		return res;
	}

	/***
	 * Download un fichier texte et le retourne sous forme d'un tableau de chaines
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String[] getFichierTexte(final String fileName) throws Exception
	{
		Connexion connexion = new Connexion(_serveur, _port);
		connexion.sendCommande(Protocole.DOWNLOAD_FICHIER, fileName);
		if (connexion.readBoolResponse())
		{
			byte[] bytes = connexion.recoitFichier();
			if (bytes == null)
				return null;
			String str = new String(bytes, "UTF-8");
			String []lignes = str.split(Connexion.SEPARATEUR_CHAINES);
			if (lignes!=null)
				for ( int i = 0; i < lignes.length;i++)
				{
					if (lignes[i].endsWith("\n"))
						lignes[i] = lignes[i].substring(0, lignes[i].length() - 1);
					if (lignes[i].endsWith("\r"))
						lignes[i] = lignes[i].substring(0, lignes[i].length() - 1);
				}
			return lignes;
		}
		return null;
	}
}
