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

public class SavedImage extends SavedObject
{
	public static final String TAG = "SavedImage" ;

	static private int _colonneDisplayName;
	static private int _colonneDateModified;
	static private int _colonneDateTaken;
	static private int _colonneAbsolutePath;
	static private int _colonneBucketDisplayName;
	public final String _absolutePath;
	public final String _displayName;
	public final long _dateTaken;
	public final long _dateModified;
	public final String _bucketName;

	public SavedImage(Cursor cursor, Context context)
	{
		_absolutePath = cursor.getString(_colonneAbsolutePath);
		_displayName = cursor.getString(_colonneDisplayName);
		_dateModified = cursor.getLong(_colonneDateModified);
		_dateTaken = cursor.getLong(_colonneDateTaken);
		_bucketName = cursor.getString(_colonneBucketDisplayName);
	}

	@Nullable
	public static Cursor getCursor(Context context)
	{
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			return null;
		}

		Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				null,
				null,
				null,
				null);

		if (cursor != null)
			try
			{
				_colonneAbsolutePath = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
				_colonneDisplayName = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
				_colonneDateModified = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
				_colonneDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);
				_colonneBucketDisplayName = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
			} catch (IllegalArgumentException e)
			{
				Log.e(TAG, e.getLocalizedMessage());
				return null;
			}
		return cursor;
	}

	/***
	 * Calcule un nom de fichier pour enregistrer ce ic_contact
	 *
	 * @return
	 */
	public String getFileName()
	{
		return FileUtils.cleanFileName(_displayName);
	}
	@NonNull
	public String getCategorie()
	{
		return _bucketName;
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

	@NonNull
	@Override
	public String Nom(@NonNull final Context context)
	{
		return _displayName;
	}
}
