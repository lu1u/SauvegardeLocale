package com.lpi.sauvegardelocale.Sauvegardes.objets;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.lpi.sauvegardelocale.Sauvegardes.ConnexionSauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.Sauvegarde;
import com.lpi.sauvegardelocale.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SavedVideo extends SavedObject
{
	public static final String TAG = "SavedVideo" ;

	static private int _colonneAbsolutePath;
	static private int _colonneDisplayName;
	static private int _colonneDateModified;
	static private int _colonneDateTaken;
	static private int _colonneBucketName;
	public String _absolutePath;
	public String _displayName;
	public String _bucketName;
	public long _dateTaken;
	public long _dateModified;

	public SavedVideo(Cursor cursor, Context context)
	{
		_absolutePath = cursor.getString(_colonneAbsolutePath);
		_displayName = cursor.getString(_colonneDisplayName);
		_dateModified = cursor.getLong(_colonneDateModified);
		_dateTaken = cursor.getLong(_colonneDateTaken);
		_bucketName = cursor.getString(_colonneBucketName);
	}

	@Nullable
	public static Cursor getCursor(Context context)
	{
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			return null;
		}

		Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				null,
				null,  //$NON-NLS-1$
				null,
				null);

		if (cursor != null)
			try
			{
				_colonneAbsolutePath = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
				_colonneDisplayName = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
				_colonneDateModified = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
				_colonneDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
				_colonneBucketName = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
			} catch (IllegalArgumentException e)
			{
				return null;
			}
		return cursor;
	}

	/***
	 * Calcule un nom de fichier pour enregistrer ce ic_contact
	 */
	public String getFileName()
	{
		return FileUtils.cleanFileName(_displayName);
	}

	@Override
	public Sauvegarde.STATUS sauvegarde(final ConnexionSauvegarde connexion, final Context context, final String path) throws Exception
	{
		OutputStream ops = null;
		InputStream ips = null;
		try
		{
			long longueur = new File(Uri.parse(_absolutePath).getPath()).length();
			ips = new FileInputStream(_absolutePath);
			connexion.envoiFichier( path, _dateTaken, longueur, ips );
			ops.close();
			ops = null;

		}
		catch (Exception e)
		{
			Log.e(TAG, e.getLocalizedMessage());
		}
		finally
		{
			if (ips != null)
				ips.close();

			if (ops != null)
				ops.close();
		}

		return Sauvegarde.STATUS.OK ;
	}

	@Override
	@NonNull
	public String Nom(@NonNull Context context)
	{
		return _displayName;
	}

	@NonNull
	public String getCategorie()
	{
		return _bucketName;
	}


}
