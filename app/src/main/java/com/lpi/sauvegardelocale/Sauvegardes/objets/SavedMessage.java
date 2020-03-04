package com.lpi.sauvegardelocale.Sauvegardes.objets;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.lpi.sauvegardelocale.R;
import com.lpi.sauvegardelocale.Sauvegardes.ConnexionSauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.Sauvegarde;
import com.lpi.sauvegardelocale.utils.ComposeTextFile;
import com.lpi.sauvegardelocale.utils.ContactUtils;
import com.lpi.sauvegardelocale.utils.FileUtils;
import com.lpi.sauvegardelocale.utils.HTMLUtils;
import com.lpi.sauvegardelocale.utils.StringOutputStream;

import java.io.BufferedOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SavedMessage extends SavedObject
{
	static final int LG_TITRE_MAX = 100;
	private static final String TAG = "SavedMessage";
	static int COLONNE_DATE;
	static int COLONNE_ADDRESS;
	static int COLONNE_DATE_SENT;
	static int COLONNE_BODY;
	static int COLONNE_LOCKED;
	static int COLONNE_PERSON;
	static int COLONNE_PROTOCOL;
	static int COLONNE_READ;
	static int COLONNE_REPLY_PATH_PRESENT;
	static int COLONNE_SEEN;
	static int COLONNE_SERVICE_CENTER;
	static int COLONNE_STATUS;
	static int COLONNE_SUBJECT;
	static int COLONNE_THREAD_ID;
	static int COLONNE_TYPE;
	static int COLONNE_ID;

	//long _date;
	//String _address;
	//Long _dateSent;
	//String _body;
	//int _locked;
	//int _person;
	//int _protocol;
	//int _read;
	//int _replyPathPresent;
	//int _seen;
	//String _serviceCenter;
	//int _status;
	//String _subject;
	//int _threadId;
	//int _type;
	//String _contact;
	//long _id;
	//COLONNE_ID = cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID);
	//COLONNE_DATE = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE);
	//COLONNE_ADDRESS = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.ADDRESS);
	//COLONNE_DATE_SENT = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE_SENT);
	//COLONNE_BODY = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY);
	//COLONNE_LOCKED = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.LOCKED);
	//COLONNE_PERSON = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.PERSON);
	//COLONNE_PROTOCOL = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.PROTOCOL);
	//COLONNE_READ = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.READ);
	//COLONNE_REPLY_PATH_PRESENT = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.REPLY_PATH_PRESENT);
	//COLONNE_SEEN = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.SEEN);
	//COLONNE_SERVICE_CENTER = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.SERVICE_CENTER);
	//COLONNE_STATUS = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.STATUS);
	//COLONNE_SUBJECT = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.SUBJECT);
	//COLONNE_THREAD_ID = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.THREAD_ID);
	//COLONNE_TYPE = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.TYPE);
	public String getAdresse()
	{
		return _attributs.get(Telephony.TextBasedSmsColumns.ADDRESS);
	}

	public String getBody()
	{
		return _attributs.get(Telephony.TextBasedSmsColumns.BODY);
	}
	public String getSubject()
	{
		return _attributs.get(Telephony.TextBasedSmsColumns.SUBJECT);
	}

	public String getServiceCenter()
	{
		return _attributs.get(Telephony.TextBasedSmsColumns.SERVICE_CENTER);
	}

	public String getContact(Context context)
	{
		return ContactUtils.getContact(context, getAdresse());
	}

	public long getId()
	{
		return getLong(ContactsContract.PhoneLookup._ID);
	}
	public long getDate()
	{
		return getLong(Telephony.TextBasedSmsColumns.DATE);
	}

	public long getDateSent()
	{
		return getLong(Telephony.TextBasedSmsColumns.DATE_SENT);
	}

	public int getLocked()
	{
		return getInt(Telephony.TextBasedSmsColumns.LOCKED);
	}

	public int getPerson()
	{
		return getInt(Telephony.TextBasedSmsColumns.PERSON);
	}
	public int getProtocole()
	{
		return getInt(Telephony.TextBasedSmsColumns.PROTOCOL);
	}
	public int getRead()
	{
		return getInt(Telephony.TextBasedSmsColumns.READ);
	}
	public int getReplyPathPresent()
	{
		return getInt(Telephony.TextBasedSmsColumns.REPLY_PATH_PRESENT);
	}
	public int getSeen()
	{
		return getInt(Telephony.TextBasedSmsColumns.SEEN);
	}
	public int getStatus()
	{
		return getInt(Telephony.TextBasedSmsColumns.STATUS);
	}
	public int getThreadId()
	{
		return getInt(Telephony.TextBasedSmsColumns.THREAD_ID);
	}
	public int getType()
	{
		return getInt(Telephony.TextBasedSmsColumns.TYPE);
	}

	public SavedMessage(Cursor cursor, Context context)
	{
		litColonnes(cursor);
		//_id = cursor.getLong(COLONNE_ID);
		//_date = cursor.getLong(COLONNE_DATE);
		//_address = cursor.getString(COLONNE_ADDRESS);
		//_dateSent = cursor.getLong(COLONNE_DATE_SENT);
		//_body = cursor.getString(COLONNE_BODY);
		//_locked = cursor.getInt(COLONNE_LOCKED);
		//_person = cursor.getInt(COLONNE_PERSON);
		//_protocol = cursor.getInt(COLONNE_PROTOCOL);
		//_read = cursor.getInt(COLONNE_READ);
		//_replyPathPresent = cursor.getInt(COLONNE_REPLY_PATH_PRESENT);
		//_seen = cursor.getInt(COLONNE_SEEN);
		//_serviceCenter = cursor.getString(COLONNE_SERVICE_CENTER);
		//_status = cursor.getInt(COLONNE_STATUS);
		//_subject = cursor.getString(COLONNE_SUBJECT);
		//_threadId = cursor.getInt(COLONNE_THREAD_ID);
		//_type = cursor.getInt(COLONNE_TYPE);
		//_contact = ContactUtils.getContact(context, _address);
	}

	public SavedMessage(final String fileName, final ConnexionSauvegarde c)
	{
		try
		{
			String[] reader = c.getFichierTexte(fileName);
			if (reader == null)
				return;

			litAttributs(reader);
/*
			int indiceLigne = 0;
			// Chercher la partie DATA
			boolean data = false;
			while (indiceLigne < reader.length && !data)
			{
				if ("<<<DATA>>>".equals(reader[indiceLigne]))
					data = true;

				indiceLigne++;
			}



			while (indiceLigne < reader.length && data)
			{
				if (reader[indiceLigne].startsWith("DATESENT"))
					_dateSent = extraitLong("DATESENT", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("DATE"))
					_date = extraitLong("DATE", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("ADDRESS"))
				{
					_address = extrait("ADDRESS", reader[indiceLigne]);
					//_contact = ContactUtils.getContact(context, _address);
				}
				else if (reader[indiceLigne].startsWith("LOCKED"))
					_locked = extraitInt("LOCKED", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("PERSON"))
					_person = extraitInt("PERSON", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("PROTOCOL"))
					_protocol = extraitInt("PROTOCOL", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("READ"))
					_read = extraitInt("READ", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("REPLYPATHPRESENT"))
					_replyPathPresent = extraitInt("REPLYPATHPRESENT", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("SEEN"))
					_seen = extraitInt("SEEN", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("SERVICECENTER"))
					_serviceCenter = extrait("SERVICECENTER", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("STATUS"))
					_status = extraitInt("STATUS", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("SUBJECT"))
					_subject = extrait("SUBJECT", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("THREADID"))
					_threadId = extraitInt("THREADID", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("TYPE"))
					_type = extraitInt("TYPE", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("ID"))
					_id = extraitLong("ID", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("BODY"))
					_body = extrait("BODY", reader[indiceLigne]);
				else if (reader[indiceLigne].startsWith("<<<ENDDATA>>>"))
					data = false;
				else
				{
					if (_body == null)
						_body = reader[indiceLigne];
					else
						_body += "\n" + reader[indiceLigne];
				}
				indiceLigne++;
			}
		 */
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}


	}


	@Nullable
	public static Cursor getCursor(Context context)
	{
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
		{
			return null;
		}

		Uri u = Uri.parse("content://sms");

		try
		{
			final String[] projection = new String[]{"*"};
			Cursor cursor = context.getContentResolver().query(u, projection, null, null, null);
			if (cursor != null)
			{
				COLONNE_ID = cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID);
				COLONNE_DATE = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE);
				COLONNE_ADDRESS = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.ADDRESS);
				COLONNE_DATE_SENT = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE_SENT);
				COLONNE_BODY = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY);
				COLONNE_LOCKED = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.LOCKED);
				COLONNE_PERSON = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.PERSON);
				COLONNE_PROTOCOL = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.PROTOCOL);
				COLONNE_READ = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.READ);
				COLONNE_REPLY_PATH_PRESENT = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.REPLY_PATH_PRESENT);
				COLONNE_SEEN = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.SEEN);
				COLONNE_SERVICE_CENTER = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.SERVICE_CENTER);
				COLONNE_STATUS = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.STATUS);
				COLONNE_SUBJECT = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.SUBJECT);
				COLONNE_THREAD_ID = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.THREAD_ID);
				COLONNE_TYPE = cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.TYPE);
			}
			return cursor;
		} catch (Exception e)
		{
			Log.e(TAG, e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public Sauvegarde.STATUS sauvegarde(final ConnexionSauvegarde connexion, final Context context, final String path) throws Exception
	{
		HashMap<String, String> hashmap = new HashMap<>();
		imageContact(context, connexion, path, hashmap);

		hashmap.put("SMS_CHARSET", Charset.defaultCharset().name());
		hashmap.put("SMS_TITRE", "SavedMessage de " + HTMLUtils.filtreHTML(getAdresse()));
		hashmap.put("SMS_DATERECEPTION", HTMLUtils.filtreHTML(sqliteDateHourToString(getDate())));
		//hashmap.put("SMS_DATE", HTMLUtils.filtreHTML(Long.toString(getDate())));
		//hashmap.put("SMS_ADDRESS", HTMLUtils.filtreHTML(getAdresse()));
		hashmap.put("SMS_DESTINATAIRE", HTMLUtils.filtreHTML(getAdresse()));
		//hashmap.put("SMS_DATESENT", HTMLUtils.filtreHTML(Long.toString(getDateSent())));
		hashmap.put("SMS_TEXTE", HTMLUtils.filtreHTML(getBody()));
		//hashmap.put("SMS_ID", HTMLUtils.filtreHTML(Long.toString(getId())));
		//hashmap.put("SMS_BODY", getBody());
		//hashmap.put("SMS_LOCKED", HTMLUtils.filtreHTML(Integer.toString(getLocked())));
		//hashmap.put("SMS_PERSON", HTMLUtils.filtreHTML(Integer.toString(getPerson())));
		//hashmap.put("SMS_PROTOCOL", HTMLUtils.filtreHTML(Integer.toString(getProtocole())));
		//hashmap.put("SMS_READ", HTMLUtils.filtreHTML(Integer.toString(getRead())));
		//hashmap.put("SMS_REPLYPATHPRESENT", HTMLUtils.filtreHTML(Integer.toString(getReplyPathPresent())));
		//hashmap.put("SMS_SEEN", HTMLUtils.filtreHTML(Integer.toString(getSeen())));
		//hashmap.put("SMS_SERVICECENTER", HTMLUtils.filtreHTML(getServiceCenter()));
		//hashmap.put("SMS_STATUS", HTMLUtils.filtreHTML(Integer.toString(getStatus())));
		//hashmap.put("SMS_SUBJECT", HTMLUtils.filtreHTML(getSubject()));
		//hashmap.put("SMS_THREADID", HTMLUtils.filtreHTML(Integer.toString(getThreadId())));
		//hashmap.put("SMS_TYPE", HTMLUtils.filtreHTML(Integer.toString(getType())));
		hashmap.put("SMS_LU", getRead() == 0 ? "non lu" : "lu");
		hashmap.put("SMS_TEXTE", HTMLUtils.filtreHTML(getBody()));
		hashmap.put("SMS_DATA", getAllData());
		StringOutputStream bs = new StringOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(bs);
		ComposeTextFile.composeFichierTexte(context, bos, R.raw.template_sms, hashmap);
		bos.close();
		connexion.envoiFichier(path, getDate(), bs.toString());
		return Sauvegarde.STATUS.OK;
	}



	public String getFileName(Context context)
	{
		String res = sqliteDateHourToString(getDate()) + "-";

		switch (getType())
		{
			case Telephony.Sms.MESSAGE_TYPE_INBOX:
				res += getContact(context) + "-";
				break;


			case Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX:
			case Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT:
				res += "moi-";
				break;
			default:
		}

		String titre;
		String subject = getSubject();
		if (subject != null)
			titre = subject;
		else
			titre = getBody();

		if (titre.length() <= LG_TITRE_MAX)
			res += titre;
		else
			res += titre.substring(0, LG_TITRE_MAX - 1);

		res = FileUtils.cleanFileName(res) + ".html";
		Log.d(TAG, "file name " + res);
		return res;
	}

	@NonNull
	public String getCategorie(Context context)
	{
		return getContact(context);
	}

	/***
	 * Ajoute l'image du contact si on en possede une
	 * @param context
	 * @param messagePath
	 */
	private void imageContact(final Context context, ConnexionSauvegarde connexion, final String messagePath, HashMap<String, String> valeurs) throws Exception
	{
		Bitmap photo = ContactUtils.getPhotoFromContact(context, getAdresse());
		if (photo == null)
			// Pas de photo pour ce contact
			return;

		String IMAGE_PATH = "contact.png";
		String imagePath = FileUtils.Combine(FileUtils.getParent(messagePath), IMAGE_PATH);
		if (!connexion.exists(imagePath))
			connexion.envoiFichier(imagePath, photo);
		valeurs.put("SMS_CONTACT_PHOTO", IMAGE_PATH);
	}

	@NonNull
	@Override
	public String Nom(@NonNull final Context context)
	{
		String res = "[" + sqliteDateHourToString(getDate()) + "]";

		switch (getType())
		{
			case Telephony.Sms.MESSAGE_TYPE_INBOX:
				res += " de " + getContact(context);
				break;


			case Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX:
			case Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT:
				res += " à " + getContact(context);
				break;
			default:
		}

		return res;
	}

	/***
	 * Restaure le sms dans la base du telephone
	 * @param context
	 */
	public void restaure(final Context context)
	{
		if (existe(context))
			// Ce sms existe deja
			return;

		try
		{
			ContentValues values = new ContentValues();
			for (Map.Entry<String, String> entry : _attributs.entrySet())
			{
				String key = entry.getKey();
				if ( key!=null)
				{
					String value = entry.getValue();
					if ( value!=null)
						values.put(key, value);
				}
			}
			//values.put(Telephony.TextBasedSmsColumns.DATE, _date);
			//values.put(Telephony.TextBasedSmsColumns.ADDRESS, _address);
			//values.put(Telephony.TextBasedSmsColumns.DATE_SENT, _dateSent);
			//values.put(Telephony.TextBasedSmsColumns.BODY, _body);
			//values.put(Telephony.TextBasedSmsColumns.LOCKED, _locked);
			//values.put(Telephony.TextBasedSmsColumns.PERSON, _person);
			//values.put(Telephony.TextBasedSmsColumns.PROTOCOL, _protocol);
			//values.put(Telephony.TextBasedSmsColumns.READ, _read);
			//values.put(Telephony.TextBasedSmsColumns.REPLY_PATH_PRESENT, _replyPathPresent);
			//values.put(Telephony.TextBasedSmsColumns.SEEN, _seen);
			//values.put(Telephony.TextBasedSmsColumns.SERVICE_CENTER, _serviceCenter);
			//values.put(Telephony.TextBasedSmsColumns.STATUS, _status);
			//values.put(Telephony.TextBasedSmsColumns.SUBJECT, _subject);
			//values.put(Telephony.TextBasedSmsColumns.THREAD_ID, _threadId);
			//values.put(Telephony.TextBasedSmsColumns.TYPE, _type);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			{
				Uri uri = Telephony.Sms.Sent.CONTENT_URI;
				if (getType() == Telephony.Sms.MESSAGE_TYPE_INBOX)
					uri = Telephony.Sms.Inbox.CONTENT_URI;
				context.getContentResolver().insert(uri, values);
			}
			else
			{
				String folderName = getType() == Telephony.Sms.MESSAGE_TYPE_INBOX ? "inbox" : "sent";
				/* folderName  could be inbox or sent */
				context.getContentResolver().insert(Uri.parse("content://sms/" + folderName), values);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

		/***
		 * Retourne vrai si le SMS existe deja dans le telephone
		 * @param context
		 * @return
		 */
		private boolean existe ( final Context context)
		{
			//TODO: determiner si ce sms existe deja (ADRESS/Body)
			return false;
		}
	}
