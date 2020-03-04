package com.lpi.sauvegardelocale.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ContactUtils
{
	public static @Nullable
	Bitmap getPhotoContact(@NonNull final Context context, @Nullable final String photoUri)
	{
		if (photoUri == null)
			return null;
		Bitmap res = null;
		Cursor c = null;

		try
		{
			Uri uri = Uri.parse(photoUri);
			c = context.getContentResolver().query(uri, new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
			if (c == null)
				return null;

			if (c.moveToFirst())
			{
				byte[] data = c.getBlob(0);
				if (data != null)
				{
					InputStream is = null;
					try
					{
						is = new ByteArrayInputStream(data);
						res = BitmapFactory.decodeStream(is);
						is.close();
					} catch (IOException e)
					{
						Log.e("Contact Utils",  e.getLocalizedMessage());
						res = null;
					}

					if ( is !=null)
						is.close();
				}
			}
		} catch (Exception e)
		{
			Log.e("Contact Utils",  e.getLocalizedMessage());
			res = null;
		} finally
		{
			if ( c!=null)
				c.close();
		}

		return res;
	}

	public static @Nullable
	Bitmap getPhotoFromContact(@NonNull final Context context, @NonNull final String contactAdress)
	{
		long contactId = getContactIdFromAdress(context, contactAdress);
		if (contactId == -1)
			return null;
		Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		Cursor cursor = context.getContentResolver().query(photoUri, new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
		if (cursor == null)
			return null;

		try
		{
			if (cursor.moveToFirst())
			{
				byte[] data = cursor.getBlob(0);
				if (data != null)
					return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
			}
		} finally
		{
			cursor.close();
		}
		return null;
	}


	/***
	 * Retrouve l'id d'un contact a partir de son adress
	 * @param context
	 * @param contactAdress
	 * @return
	 */
	private static long getContactIdFromAdress(@NonNull final Context context, @NonNull final String contactAdress)
	{
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactAdress));
		String[] projection = {ContactsContract.PhoneLookup._ID};
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

		if (cursor == null)
			return -1;

		try
		{

			if (cursor.moveToFirst())
			{
				return cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
			}

		} finally
		{
			cursor.close();
		}

		return -1;

	}


	/**
	 * Retrouve le nom du ic_contact a partir de son numero
	 *
	 * @param a
	 * @param numero
	 * @return
	 * @throws Exception
	 */
	public static @Nullable
	String getContact(@NonNull Context a, @Nullable String numero)
	{
		if (numero == null)
			return null;

		String res = numero;
		Cursor c = null;
		try
		{
			Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(numero));
			c = a.getContentResolver().query(uri,
					new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
			if (c != null)
			{
				c.moveToFirst();
				res = c.getString(c.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
			}
		} catch (Exception e)
		{
			if (numero.startsWith("+33"))
			{
				numero = "0" + numero.substring(3);
				return getContact(a, numero);
			}
			else
				res = numero;
		} finally
		{
			if (c != null)
				c.close();
		}
		return res;

	}
}
