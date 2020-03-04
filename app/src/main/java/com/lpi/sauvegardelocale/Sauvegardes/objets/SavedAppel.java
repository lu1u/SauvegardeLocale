package com.lpi.sauvegardelocale.Sauvegardes.objets;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.lpi.sauvegardelocale.Sauvegardes.ConnexionSauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.Sauvegarde;
import com.lpi.sauvegardelocale.utils.ContactUtils;
import com.lpi.sauvegardelocale.utils.FileUtils;

import java.util.Map;

public class SavedAppel extends SavedObject
{
	public static final String TAG = "SavedAppel";
	static final private String[] COLONNES = new String[]
			{
					CallLog.Calls.NUMBER,
					CallLog.Calls.DATE,
					CallLog.Calls.DURATION,
					CallLog.Calls.TYPE,
					CallLog.Calls.PHONE_ACCOUNT_ID,
					CallLog.Calls.GEOCODED_LOCATION,
					CallLog.Calls.IS_READ,
			};


	//private static int _colNumber;
	//private static int _colDate;
	//private static int _colDuration;
	//private static int _colType;
	//private static int _colPhoneAccountId;
	//private static int _colGeocodedLocation;
	//private static int _colIsRead;
//
//
	//public String _number;
	//public long _date;
	//public long _duration;
	//public int _type;
	//public String _phoneAccountId;
	//public String _address;
	//public String _geoCodedLocation;
	//public int _isRead;
//

	public String getNumber()
	{
		return _attributs.get(CallLog.Calls.NUMBER);
	}
	public String getAdress(Context context)
	{
		return ContactUtils.getContact(context, getNumber());
	}

	public int getType()
	{
		return Integer.parseInt(_attributs.get(CallLog.Calls.TYPE));
	}
	public long getDuree()
	{
		return Long.parseLong(_attributs.get(CallLog.Calls.DURATION));
	}

	public long getDate()
	{
		return Long.parseLong(_attributs.get(CallLog.Calls.DATE));
	}


	public SavedAppel(@NonNull Cursor cursor, @NonNull Context context)
	{
		litColonnes(cursor);
		//_date = cursor.getLong(_colDate);
		//_duration = cursor.getLong(_colDuration);
		//_number = cursor.getString(_colNumber);
		//_phoneAccountId = cursor.getString(_colPhoneAccountId);
		//_type = cursor.getInt(_colType);
		//_address = ContactUtils.getContact(context, _number);
//
		//_geoCodedLocation = cursor.getString(_colGeocodedLocation);
		//_isRead = cursor.getInt(_colIsRead);
	}

	/***
	 * Retrouve un contact a partir d'un fichier sur le serveur
	 * @param fileName : nom du fichier sur le serveur
	 */
	public SavedAppel(final String fileName, ConnexionSauvegarde c)
	{
		try
		{
			String[] reader = c.getFichierTexte(fileName);
			if (reader == null)
				return;
			litAttributs(reader);
//			int indiceLigne = 0;
//			// Chercher la partie DATA
//			boolean data = false;
//			while (indiceLigne < reader.length && !data)
//			{
//				if ("<<<DATA>>>".equals(reader[indiceLigne]))
//					data = true;
//
//				indiceLigne++;
//			}
//
//
//			while (indiceLigne < reader.length && data)
//			{
//				if (reader[indiceLigne].startsWith("<<<ENDDATA>>>"))
//					data = false;
//				else
//				{
//					String key = extraitKey(reader[indiceLigne]);
//					String value = extraitValue(reader[indiceLigne]);
//					_attributs.put(key, value);
//				}
//				//if (reader[indiceLigne].startsWith("ADDRESS"))
//				//	_address = extrait("ADDRESS", reader[indiceLigne]);
//				//else
//				//	if (reader[indiceLigne].startsWith("TYPE"))
//				//		_type = extraitInt("TYPE", reader[indiceLigne]);
//				//	else
//				//		if (reader[indiceLigne].startsWith("NUMBER"))
//				//			_number = extrait("NUMBER", reader[indiceLigne]);
//				//		else
//				//			if (reader[indiceLigne].startsWith("PHONE_ACCOUNT_ID"))
//				//				_phoneAccountId = extrait("PHONE_ACCOUNT_ID", reader[indiceLigne]);
//				//			else
//				//				if (reader[indiceLigne].startsWith("DATE"))
//				//					_date = extraitLong("DATE", reader[indiceLigne]);
//				//				else
//				//					if (reader[indiceLigne].startsWith("DURATION"))
//				//						_duration = extraitLong("DURATION", reader[indiceLigne]);
//				//					else
//				//						if (reader[indiceLigne].startsWith("TYPE"))
//				//							_type = extraitInt("TYPE", reader[indiceLigne]);
//				//						else
//				//							if (reader[indiceLigne].startsWith("ADDRESS"))
//				//								_address = extrait("ADDRESS", reader[indiceLigne]);
//				//							else
//				//								if (reader[indiceLigne].startsWith("GEOLOCATION"))
//				//									_geoCodedLocation = extrait("GEOLOCATION", reader[indiceLigne]);
//				//								else
//				//									if (reader[indiceLigne].startsWith("IS_READ"))
//				//										_isRead = extraitInt("IS_READ", reader[indiceLigne]);
//				//									else
//				//										if (reader[indiceLigne].startsWith("<<<ENDDATA>>>"))
//				//											data = false;
//
//				indiceLigne++;
//			}

		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public String getCategorie(Context context)
	{
		return getAdress(context);
	}

	@NonNull
	public String getFileName(@NonNull Context context)
	{
		return FileUtils.cleanFileName(Nom(context)) + ".txt";
	}

	@Nullable
	public static Cursor getCursor(@NonNull Context context)
	{
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
		{
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return null;
		}
		Cursor cursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, COLONNES, null, null, null);
		//if (cursor != null)
		//	try
		//	{
		//		_colNumber = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER);
		//		_colDate = cursor.getColumnIndexOrThrow(CallLog.Calls.DATE);
		//		_colDuration = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION);
		//		_colPhoneAccountId = cursor.getColumnIndexOrThrow(CallLog.Calls.PHONE_ACCOUNT_ID);
		//		_colType = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE);
		//		_colGeocodedLocation = cursor.getColumnIndexOrThrow(CallLog.Calls.GEOCODED_LOCATION);
		//		_colIsRead = cursor.getColumnIndexOrThrow(CallLog.Calls.IS_READ);
		//	} catch (Exception e)
		//	{
		//		return null;
		//	}

		return cursor;
	}

	@Override
	public Sauvegarde.STATUS sauvegarde(@NonNull final ConnexionSauvegarde connexion, @NonNull Context context, @NonNull String path) throws Exception
	{
		StringBuilder b = new StringBuilder();

		switch (getType())
		{
			case Telephony.Sms.MESSAGE_TYPE_INBOX:
				b.append("Appel le " + sqliteDateHourToString(getDate()) + " de " + getAdress(context));
				break;

			case Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX:
			case Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT:
				b.append("Appel le " + sqliteDateHourToString(getDate()) + " à " + getAdress(context));
				break;
			default:
		}

		b.append("\nDurée: " + sqliteDurationToString(getDuree()));
		b.append(BEGIN_DATA);
		writeAttributs(b);
		b.append(ENDDATA);

		connexion.envoiFichier(path, getDate(), b.toString());
		return Sauvegarde.STATUS.OK;
	}


	@NonNull
	@Override
	public String Nom(@NonNull final Context context)
	{
		String res = sqliteDateHourToString(getDate());

		switch (getType())
		{
			case Telephony.Sms.MESSAGE_TYPE_INBOX:
				res += " de " + getAdress(context);
				break;


			case Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX:
			case Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT:
				res += " à " + getAdress(context);
				break;
			default:
		}

		res += " " + sqliteDurationToString(getDuree());

		return res;
	}

	public void restaure(final Context context)
	{
		String number = getNumber();
		if (number != null)
		{
			while (number.contains("-"))
			{
				number = number.substring(0, number.indexOf('-')) + number.substring(number.indexOf('-') + 1, number.length());
			}
			ContentValues values = new ContentValues();
			//values.put(CallLog.Calls.NUMBER, _number);
			//values.put(CallLog.Calls.DATE, _date);
			//values.put(CallLog.Calls.DURATION, _duration);
			//values.put(CallLog.Calls.TYPE, _type);
			//values.put(CallLog.Calls.NEW, 1);
			//values.put(CallLog.Calls.CACHED_NAME, _address);
			//values.put(CallLog.Calls.PHONE_ACCOUNT_ID, _phoneAccountId);
			//values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
			//values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
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
			context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
		}
	}
}
