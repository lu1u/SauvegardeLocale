package com.lpi.sauvegardelocale.Sauvegardes.objets;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.lpi.sauvegardelocale.Sauvegardes.ConnexionSauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.Sauvegarde;
import com.lpi.sauvegardelocale.utils.ContactUtils;
import com.lpi.sauvegardelocale.utils.FileUtils;
import com.lpi.sauvegardelocale.utils.StringOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;


/**
 * Objet representant un contact stocke sur le telephone
 * Created by lucien on 29/01/2016.
 */
public class SavedContact extends SavedObject
{
	public static final String TAG = "SavedContact";


	private static final String[] COLONNES_PHONENUMBER = {ContactsContract.CommonDataKinds.Phone.NUMBER};
	private static final String[] COLONNES_EMAIL = {ContactsContract.CommonDataKinds.Email.DATA};

	public Vector<String> _numeros;
	public Vector<String> _eMails;
	public Bitmap _photo;

	private String getId()
	{
		return _attributs.get(ContactsContract.Contacts._ID);
	}

	public String getNom()
	{
		return _attributs.get(ContactsContract.Contacts.DISPLAY_NAME);
	}

	private String getPhotoUri()
	{
		return _attributs.get(ContactsContract.Contacts.PHOTO_URI);
	}

	private int getTimesContacted()
	{
		return Integer.parseInt(_attributs.get(ContactsContract.Contacts.TIMES_CONTACTED));
	}

	private long getLastContacted()
	{
		return Long.parseLong(_attributs.get(ContactsContract.Contacts.LAST_TIME_CONTACTED));
	}

	private int getHasPhoneNumber()
	{
		return Integer.parseInt(_attributs.get(ContactsContract.Contacts.HAS_PHONE_NUMBER));
	}


	public SavedContact(@NonNull Cursor cursor, @NonNull Context context)
	{
		litColonnes(cursor);

		{
			// Numeros de telephone
			int hasPhoneNumber = getHasPhoneNumber(); //Integer.parseInt(cursor.getString(_colonneHasPhoneNumber));
			if (hasPhoneNumber > 0)
			{
				Cursor phoneCursor = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, COLONNES_PHONENUMBER,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] //$NON-NLS-1$
								{getId()}, null);
				if (phoneCursor != null)
				{
					_numeros = new Vector<String>();
					final int _colonneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					while (phoneCursor.moveToNext())
					{
						_numeros.add(phoneCursor.getString(_colonneIndex));
					}
					phoneCursor.close();
				}
			}
		}

		{
			// Adresses mail
			Cursor emailCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					COLONNES_EMAIL, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
					new String[]{getId()}, null);

			if (emailCursor != null)
			{
				if (emailCursor.getCount() > 0)
				{
					final int _colonneIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
					_eMails = new Vector<>();
					while (emailCursor.moveToNext())
					{
						_eMails.add(emailCursor.getString(_colonneIndex));
					}
				}
				emailCursor.close();
			}
		}

		String photoUri = getPhotoUri();
		if (photoUri != null)
			_photo = ContactUtils.getPhotoContact(context, photoUri);
	}

	/***
	 * Retrouve un contact a partir d'un fichier sur le serveur
	 * @param fileName : nom du fichier sur le serveur
	 */
	public SavedContact(final String fileName, ConnexionSauvegarde c)
	{
		try
		{
			String[] reader = c.getFichierTexte(fileName);
			if (reader == null)
				return;
			litAttributs(reader);
//			int indiceLigne = 0 ;
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
//			while (indiceLigne < reader.length && data)
//			{
//				if (reader[indiceLigne].startsWith("<<<ENDDATA>>>"))
//					data = false;
//				else
//				if (reader[indiceLigne].startsWith("PHONE"))
//				{
//					if (_numeros == null)
//						_numeros = new Vector<>();
//
//					_numeros.add(extrait("PHONE", reader[indiceLigne]));
//				}
//				else
//				if (reader[indiceLigne].startsWith("EMAIL"))
//				{
//					if (_eMails == null)
//						_eMails = new Vector<>();
//
//					_eMails.add(extrait("EMAIL", reader[indiceLigne]));
//				}
//				else
//					{
//					String key = extraitKey(reader[indiceLigne]);
//					String value = extraitValue(reader[indiceLigne]);
//					_attributs.put(key, value);
//				}
//
////				if (reader[indiceLigne].startsWith("ID"))
////					_id = extrait("ID", reader[indiceLigne]);
////				if (reader[indiceLigne].startsWith("NAME"))
////					_nom = extrait("NAME", reader[indiceLigne]);
////
////				if (reader[indiceLigne].startsWith("TIMESCONTACTED"))
////					_timesContacted = extraitLong("TIMESCONTACTED", reader[indiceLigne]);
////
////				if (reader[indiceLigne].startsWith("LASTCONTACTED"))
////					_lastContacted = extraitLong("LASTCONTACTED", reader[indiceLigne]);
////
////				if (reader[indiceLigne].startsWith("LOOKUPKEY"))
////					_LookupKey = extrait("LOOKUPKEY", reader[indiceLigne]);
////
////				if (reader[indiceLigne].startsWith("DISPLAYNAMEPRIMARY"))
////					_DisplayNamePrimary = extrait("DISPLAYNAMEPRIMARY", reader[indiceLigne]);
////
////				if (reader[indiceLigne].startsWith("ISVISIBLEGROUP"))
////					_InVisibleGroup = extraitInt("ISVISIBLEGROUP", reader[indiceLigne]);
////
////				if (reader[indiceLigne].startsWith("ISUSERPROFILE"))
////					_IsUserProfile = extrait("ISUSERPROFILE", reader[indiceLigne]);
//
//
//				indiceLigne ++ ;
//			}

			// Chercher l'image
			String imagePath = FileUtils.changeExtension(fileName, ".png");
			_photo = c.getImage(imagePath);

		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}


	@Nullable
	public static Cursor getCursor(Context context)
	{
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
		{
			Log.e(TAG, "Permission READ_CONTACTS non accordée");
			return null;
		}

		//Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, COLONNES, null, null, ContactsContract.Contacts.DISPLAY_NAME);
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
		//if (cursor != null)
		//	try
		//	{
		//		_colonneID = cursor.getColumnIndexOrThrow(BaseColumns._ID);
		//		_colonneDisplayName = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
		//		_colonneHasPhoneNumber = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		//		_colonneTimesContacted = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.TIMES_CONTACTED);
		//		_colonneLastContacted = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LAST_TIME_CONTACTED);
		//		_colonneLookupKey = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY);
		//		_colonneDisplayNamePrimary = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
		//		_colonnePhotoId = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID);
		//		_colonnePhotoUri = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI);
		//		_colonneInVisibleGroup = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.IN_VISIBLE_GROUP);
		//		_colonneIsUserProfile = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.IS_USER_PROFILE);
//
		//	} catch (Exception e)
		//	{
		//		return null;
		//	}

		return cursor;
	}

	@Override
	public
	@NonNull
	String Nom(@NonNull Context context)
	{
		return getNom();
	}

	/***
	 * Calcule un nom de fichier pour enregistrer ce ic_contact
	 */
	public String getFileName(Context context, String extension)
	{
		return FileUtils.cleanFileName(getNom()) + extension;
	}

	public String getFileName(Context context)
	{
		return getFileName(context, ".txt");
	}

	/**
	 * Sauvegarde le ic_contact dans le fichier samba donne en parametre
	 */
	public Sauvegarde.STATUS sauvegarde(ConnexionSauvegarde connexion, @NonNull Context context, String racine) throws Exception
	{
		// Path de cet appel
		String contactPath = FileUtils.cleanPathName(FileUtils.Combine(racine, getFileName(context)));
		Log.d(TAG, "SavedContact path:" + contactPath);
		if (connexion.exists(contactPath))
			return Sauvegarde.STATUS.NOTHING;

		StringOutputStream sops = null;
		try
		{
			sops = new StringOutputStream();

			// Infos lisibles
			sops.write((getNom() + "\n").getBytes());
			int timesContacted = getTimesContacted();
			if (timesContacted > 0)
			{
				sops.write(("Contacté " + timesContacted + " fois\n").getBytes());

				try
				{
					sops.write(("Contacté la dernière fois: " + sqliteDateHourToString(getLastContacted()) + "\n").getBytes());
				} catch (NumberFormatException e)
				{
					Log.e(TAG, "Erreur lors de l'ecriture du contact" + getNom());
					Log.e(TAG, e.getLocalizedMessage());
				}
			}

			if (_numeros != null)
			{
				// Numeros de telephone
				for (String tel : _numeros)
					sops.write(("Téléphone: " + tel + "\n").getBytes());
			}

			if (_eMails != null)
			{
				// Adresses mail
				for (String mail : _eMails)
					sops.write(("E-mail: " + mail + "\n").getBytes());
			}

			// Donnees qui seront interpretees lors de la restauration
			sops.write(BEGIN_DATA.getBytes());
			writeAttributs(sops);

			if (_numeros != null)
			{
				// Numeros de telephone
				for (String tel : _numeros)
					sops.write(("\nPHONE " + tel).getBytes());
			}

			if (_eMails != null)
			{
				// Adresses mail
				for (String mail : _eMails)
					sops.write(("\nEMAIL " + mail + "\n").getBytes());
			}
			sops.write(ENDDATA.getBytes());
			connexion.envoiFichier(contactPath, sops.toString());

		} catch (IOException e)
		{
			Log.e(TAG, "Erreur lors de la sauvegarde du ic_contact " + getNom());
			Log.e(TAG, e.getLocalizedMessage());
			return Sauvegarde.STATUS.FAILED;
		} finally
		{
			if (sops != null)
				sops.close();
		}

		if (_photo != null)
		{
			String imagePath = FileUtils.cleanPathName(FileUtils.Combine(racine, getFileName(context, ".png")));
			if (!connexion.exists(imagePath))
			{
				connexion.envoiFichier(imagePath, _photo);
			}
		}
		return Sauvegarde.STATUS.OK;
	}


	public boolean quelqueChoseASauvegarder()
	{
		return (getTimesContacted() > 0) || (_numeros != null) || (_eMails != null);
	}


	/***
	 * Restauration du contact
	 * @param context
	 * @return
	 */
	public Sauvegarde.STATUS restaure(Context context)
	{
		if (existeDansTelephone(context))
			return Sauvegarde.STATUS.OK;

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, getNom()).build());

		if (_numeros != null)
			for (String numero : _numeros)
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, numero)
						.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
		if (_eMails != null)
			for (String email : _eMails)
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
						.withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER).build());


		if (_photo != null)
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			_photo.compress(Bitmap.CompressFormat.PNG, 75, stream);
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
					.build());
			try
			{
				stream.flush();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try
		{
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (Exception e)
		{
			e.printStackTrace();
			return Sauvegarde.STATUS.FAILED;
		}

		return Sauvegarde.STATUS.OK;
	}

	/***
	 * Retourne true si un des numeros du contact existe deja dans le telephone
	 * @param context
	 * @return
	 */
	private boolean existeDansTelephone(Context context)
	{

		//return true;
		if (_numeros == null)
			return false;

		for (String numero : _numeros)
			if (existeDansTelephone(context, numero))
				return true;

		return false;
	}

	/***
	 * Retourne true si le contact existe deja dans le telephone avec ce numero
	 * @param context
	 * @return
	 */
	private boolean existeDansTelephone(Context context, String number)
	{
		Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
		Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
		try
		{
			if (cur.moveToFirst())
			{
				// if contact are in contact list it will return true
				return true;
			}
		} finally
		{
			if (cur != null)
				cur.close();
		}
		//if contact are not match that means contact are not added
		return false;
	}
}


