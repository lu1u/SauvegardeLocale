package com.lpi.sauvegardelocale.Network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lpi.sauvegardelocale.utils.Protocole;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;


/***
 * Classe de communication avec le serveur de sauvegarde locale
 */
public class Connexion // Singleton
{
	public static final char TERMINAL = '\u0000';
	static public final String TAG = "Connexion";
	public static final String SEPARATEUR_CHAINES = "\n";
	private static final String SEPARATEUR_DATE = "=";
	private String _hostName;
	private int _port = 0;
	private Socket _socket;
	private InputStream _input;
	private OutputStream _output;

	public Connexion(@NonNull String serveur, int port) throws Exception
	{
		_hostName = serveur;
		_port = port;
		_socket = new Socket();
		_socket.connect(new InetSocketAddress(_hostName, _port), 1000);
		_input = _socket.getInputStream();
		_output = _socket.getOutputStream();
	}

	@Override
	protected void finalize() throws Throwable
	{
		close();
	}

	/***
	 * Fermer la socket et la rendre inutilisable, liberer les ressources utilisees
	 * @throws
	 */
	public void close() throws Exception
	{
		if (_socket != null)
		{
			_socket.close();
			_socket = null;
		}
	}

	public boolean isConnecte()
	{
		if (_socket == null)
			return false;

		return _socket.isConnected();
	}

	public void sendCommande(@NonNull final String commande, @Nullable final String parametre) throws Exception
	{
		Log.d(TAG, "sendCommande '" + commande + "', parametre: '" + (parametre == null ? "null'" : parametre + "'"));
		write(commande);
		if (parametre != null)
		{
			write(parametre);
		}
	}

	public void sendCommande(@NonNull final String commande, @NonNull final String param1, @NonNull final String param2)
	{
		Log.d(TAG, "sendCommande '" + commande + "', parametres: '" + param1 + "," + param2);
		write(commande);
		write(param1);
		write(param2);
	}

	/***
	 * Ecrit une chaine dans la socket
	 * @param chaine
	 */
	private void write(final String chaine)
	{
		try
		{
			_output.write(chaine.getBytes());
			_output.write(TERMINAL);
			_output.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * Lit une ligne dans la socket (terminée par caractere 0)
	 *
	 * @return
	 */
	public String readLine()
	{
		StringBuilder b = new StringBuilder();
		try
		{
			char charCur = (char) _input.read();

			while (charCur != TERMINAL)
			{
				b.append(charCur); // ... si non, on concatène le caractère dans le message
				charCur = (char) _input.read();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return b.toString();
	}


	/***
	 * Lit une reponse booleenne depuis le adresse
	 * @return
	 * @throws
	 */
	public boolean readBoolResponse()
	{
		String rep = readLine();
		return rep.equalsIgnoreCase(Protocole.REPONSE_OUI);
	}

	/***
	 * Envoie un fichier.
	 * Protocole: on envoie d'abord la taille, puis le contenu
	 * @param bytes
	 */
	public void envoiFichier(@NonNull byte[] bytes) throws Exception
	{
		// Envoyer la taille, en chaine de caracteres
		String taille = Integer.toString(bytes.length);
		Log.d(TAG, "envoiFichier, longueur=" + taille);
		write(taille);
		Log.d(TAG, "envoi de " + bytes.length + " octets");
		_output.write(bytes, 0, bytes.length);
		_output.flush();
	}

	/***
	 * Envoi un fichier texte initialement contenu dans une chaine
	 */
	public void envoiFichier(@NonNull String fichier) throws Exception
	{
		envoiFichier(fichier.getBytes());
	}


	public void envoiFichier(final long taille, final InputStream ips) throws Exception
	{
		// Envoyer la taille, en chaine de caracteres
		Log.d(TAG, "envoiFichier, longueur=" + taille);
		write(Long.toString(taille));
		Log.d(TAG, "envoi de " + taille + " octets");

		byte[] buffer = new byte[1024 * 4];
		int nbLus = 0;
		try
		{
			while (nbLus < taille)
			{
				int nb = ips.read(buffer);
				_output.write(buffer, 0, nb);

				nbLus += nb;
				//System.out.println("(" + nb + ") " + nbLus + "octets envoyés / " + taille);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_output.flush();
	}



	/***
	 * Construction la representation textuelle d'une date pour l'envoyer au serveur
	 * @param date
	 * @return
	 */
	public String date(final long date)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date);

		return c.get(Calendar.YEAR)
				+ SEPARATEUR_DATE + (c.get(Calendar.MONTH)+1)
				+ SEPARATEUR_DATE +c.get(Calendar.DAY_OF_MONTH)
				+ SEPARATEUR_DATE +c.get(Calendar.HOUR_OF_DAY)
				+ SEPARATEUR_DATE +c.get(Calendar.MINUTE)
				+ SEPARATEUR_DATE +c.get(Calendar.SECOND) ;
	}

	public byte[] recoitFichier() throws Exception
	{
		// Recoit la taille, en chaine de caracteres
		String sTaille = readLine() ;

		Log.d(TAG, "recoitFichier, longueur=" + sTaille);
		int taille = Integer.parseInt(sTaille);
		Log.d(TAG, "envoi de " + taille + " octets");
		byte[] b = new byte[taille];
		_input.read(b, 0, taille);
		return b;
	}
}
