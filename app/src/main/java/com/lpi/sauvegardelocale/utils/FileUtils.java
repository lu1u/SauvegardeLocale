package com.lpi.sauvegardelocale.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


/**
 * Created by lucien on 15/01/2018.
 */

public class FileUtils
{
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	//	final static int[] ILLEGAL_CHARS = {34, 60, '+', 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};
	public static final String PATH_SEPARATOR = "\\";
	static private String _invalidPathChars;

	//static
	//{
	//	Arrays.sort(ILLEGAL_CHARS);
	//}

	/***
	 * Lire le contenu d'un InputStream et le copier dans un tableau d'octets
	 * @param is
	 * @return
	 */
	public static byte[] readArray(InputStream is)
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try
		{
			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1)
				buffer.write(data, 0, nRead);

			buffer.flush();
		} catch (Exception e)
		{

		}

		return buffer.toByteArray();
	}


	/***
	 * Change l'extension d'un nom de fichier
	 * @param name
	 * @param extension
	 * @return
	 */
	@NonNull
	public static String changeExtension(@NonNull String name, @Nullable String extension)
	{
		int indicePoint = name.lastIndexOf('.');
		if (indicePoint != -1)
		{
			int indiceSlash = name.lastIndexOf(PATH_SEPARATOR);
			if (indiceSlash < indicePoint)
				name = name.substring(0, indicePoint);
		}

		if (extension != null)
		{
			if ( extension.startsWith("."))
				name += extension;
			else
				name += "." + extension;
		}
		else
		{
			// Eventuellement, supprimer le . a la fin
			if (name.charAt(name.length() - 1) == '.')
			{
				name = name.substring(0, name.length() - 1);
			}
		}
		return name;
	}

	/***
	 * Met l'extension d'un nom de fichier
	 * @param name
	 * @param extension
	 * @return
	 */
	@NonNull
	public static String setExtension(@NonNull String name, @NonNull String extension)
	{
		String res;
		if (extension.charAt(0) != '.')
			extension = "." + extension;

		int indicePoint = name.lastIndexOf('.');
		if (indicePoint == -1)
			res = name + extension;
		else
			res = name + extension.substring(1);

		return res;
	}


	/***
	 * Lit une image dans un fichier
	 * @param is InputStream
	 * @return Bitmap lue
	 */
	public static Bitmap readImage(InputStream is)
	{
		byte[] byteArray = FileUtils.readArray(is);
		return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
	}

	/***
	 * Supprime les caracteres illegaux d'un nom de fichier
	 * @param badFileName
	 * @return
	 */
	public static String cleanPathName(String badFileName)
	{
		badFileName = badFileName.replace("\n", "").replace("\r", "")
				.replace("\u0000", "")
				.replace("/", "\\")
				.replace("\\\\", "\\");
		if (_invalidPathChars == null)
			return badFileName;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < badFileName.length(); i++)
			if (_invalidPathChars.indexOf(badFileName.charAt(i)) == -1)
				sb.append(badFileName.charAt(i));

		return sb.toString();
	}

	public static String cleanFileName(String fileName)
	{
		fileName = fileName.replace("\n", "").replace("\r", "").replace("\u0000", "")
				.replace("/", "").replace("\\", "");
		if (_invalidPathChars != null)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < fileName.length(); i++)
				if (_invalidPathChars.indexOf(fileName.charAt(i)) == -1)
					sb.append(fileName.charAt(i));

			fileName = sb.toString();
		}

		if (fileName.length() > 128)
			fileName = fileName.substring(0, 127);

		return fileName;
	}

	/***
	 * Verifie q'un nom de fichier est legals
	 * @param fileName
	 * @return
	 */
	public static boolean fileNameOk(String fileName)
	{
		if ( _invalidPathChars == null)
			return true;

		for (int i = 0; i < fileName.length(); i++)
		{
			//int c = (int) fileName.charAt(i);
			//if (Arrays.binarySearch(ILLEGAL_CHARS, c) >= 0)
			//	return false;
			if (_invalidPathChars.indexOf(fileName.charAt(i)) != -1)
				return false;
		}

		return true;
	}

	/***
	 * Copie d'un fichier
	 * @param i : fichier d'entree
	 * @param o : fichier de sortie
	 * @throws IOException
	 */
	public static void copyLarge(InputStream i, OutputStream o) throws IOException
	{
		BufferedOutputStream output = new BufferedOutputStream(o);
		BufferedInputStream input = new BufferedInputStream(i);

		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int n;
		while (-1 != (n = input.read(buffer)))
		{
			output.write(buffer, 0, n);
		}
	}

	/***
	 * Retourne vrai si le nom de fichier se termine par l'extension donnee
	 * @param name
	 * @param extension
	 * @return
	 */
	public static boolean extensionOK(String name, String extension)
	{
		if (extension == null)
			return true;

		if (name == null)
			return false;

		if (name.length() < extension.length())
			return false;

		if (!extension.startsWith("."))
			extension = '.' + extension;

		int i = name.lastIndexOf(extension);
		if (i == -1)
			return false;

		return i == (name.length() - extension.length());
	}


	/***
	 * Combine deux parties d'un chemin de fichier
	 * @param partage
	 * @param path
	 * @return
	 */
	public static String Combine(String partage, String path)
	{
		if (partage.endsWith(PATH_SEPARATOR))
			return partage + path;
		else
			return partage + PATH_SEPARATOR + path;
	}

	static public String getExtension(@NonNull String fileName)
	{
		return MimeTypeMap.getFileExtensionFromUrl(fileName);
		/*
		char ch;
		int len;
		if(fileName==null ||
				(len = fileName.length())==0 ||
				(ch = fileName.charAt(len-1))=='/' || ch=='\\' || //in the case of a directory
				ch=='.' ) //in the case of . or ..
			return "";
		int dotInd = fileName.lastIndexOf('.'),
				sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if( dotInd<=sepInd )
			return "";
		else
			return fileName.substring(dotInd+1).toLowerCase();
			*/
	}

	/***
	 * Lit un fichier raw contenu dans les ressources et l'ecrit dans un outputstream
	 * @param context
	 * @param outputStream
	 * @param rawFileId
	 */
	public static void copyRawResource(final Context context,
	                                   final BufferedOutputStream outputStream, final int rawFileId) throws Exception
	{
		InputStream inputStream = context.getResources().openRawResource(rawFileId);

		int size;
		byte[] buffer = new byte[1024];
		while ((size = inputStream.read(buffer)) != -1)
		{
			outputStream.write(buffer, 0, size);
		}
		inputStream.close();
	}

	/***
	 * Extrait le dernier repertoire d'un chemin
	 * @param repertoire
	 * @return
	 */
	public static @NonNull
	String dernierRepertoire(@NonNull final String repertoire)
	{
		int indice = repertoire.lastIndexOf(PATH_SEPARATOR);
		if (indice == -1)
			return repertoire;

		return repertoire.substring(indice + 1);
	}

	public static String getInvalidPathChar()
	{
		return _invalidPathChars;
	}
	public static void setInvalidPathChar(String s)
	{
		_invalidPathChars = s + "?:!*><\"|%";
	}

	/***
	 * Retrouve le repertoire parent d'un chemin, en supprimant la derniere partie
	 * @param path
	 * @return
	 */
	public static @NonNull String getParent(@NonNull final String path)
	{
		final int indice = path.lastIndexOf(PATH_SEPARATOR);
		if ( indice==-1)
			return path;

		return path.substring(0, indice);
	}

	/***
	 * Retourne un chemin, en enlevant l'extension
	 * @param path
	 * @return
	 */
	public static String sansExtension(@NonNull final String path)
	{
		int indice = path.lastIndexOf('.');
		if ( indice == -1)
			return path;

		return  path.substring(0, indice ) ;
	}
}
