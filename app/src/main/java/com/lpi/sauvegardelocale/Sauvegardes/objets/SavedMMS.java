package com.lpi.sauvegardelocale.Sauvegardes.objets;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SavedMMS extends SavedObject
{
	private static final String TAG = "SavedMMS" ;
	//private String _mmsId;
	//private long _date;
	//private String _thread;
	private ArrayList<String> _adresses;
	private ArrayList<String> _contacts;

	private static final long SECONDES = 1;
	private static final long MINUTES = SECONDES * 60L;
	private static final long HEURES = MINUTES * 60L;
	private static final long JOURS = HEURES * 24L;
	private static final long ANNEE = JOURS * 365;

	private String getId()
	{
		return _attributs.get("_id");
	}
	private long getDate()
	{
		return getLong("date");
	}

	public SavedMMS(Cursor c, Context context)
	{
		litColonnes(c);
		//_mmsId = c.getString(c.getColumnIndex("_id"));
		//_thread = c.getString(c.getColumnIndex("thread_id"));
		//_date = c.getLong(c.getColumnIndex("date"));
		_adresses = getMmsAddr(context, getId());
		_contacts = new ArrayList<>();
		for( String adresse : _adresses)
		{
			String contact = ContactUtils.getContact(context, adresse);
			if( contact!=null)
				_contacts.add(contact);
		}

		//if (_contacts.size()>1)
		//	Report.getInstance(context).log(Report.NIVEAU.DEBUG, _contacts.toString());
	}

	@Nullable
	public static Cursor getCursor(Context context)
	{
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
		{
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return null;
		}
		Uri u = Uri.parse("content://mms");
		try
		{
			Cursor cursor = context.getContentResolver().query(u, null, null, null, null);

			return cursor;
		} catch (Exception e)
		{
			return null;
		}
	}
	@Override
	public Sauvegarde.STATUS sauvegarde(final ConnexionSauvegarde connexion, final Context context, final String path) throws Exception
	{
		Log.d("SAVE", "SavedMessage path:" + path);

		try
		{
			HashMap<String, String> hashmap = new HashMap<>();
			hashmap.put("MMS_CHARSET", Charset.defaultCharset().name());
			hashmap.put("MMS_DATE", HTMLUtils.filtreHTML(convertiMMSDate(getDate())));
			hashmap.put("MMS_CONTACT_EXPEDITEUR", HTMLUtils.filtreHTML((String)dernier(_contacts)));
			hashmap.put("MMS_CONTACT_RECEVEUR", HTMLUtils.filtreHTML((String)avantDernier(_contacts)));
			hashmap.put("MMS_DATA", getAllData());
			// Creer un repertoire pour stocker les fichiers propres a ce sms
			String repertoireMorceaux = FileUtils.changeExtension(path, null) ;
			connexion.creerRepertoire(repertoireMorceaux);

			imageContact(context, FileUtils.getParent(path), connexion, hashmap);
			String selectionPart = "mid=" + getId();
			Uri uri = Uri.parse("content://mms/part");
			Cursor cursor = context.getContentResolver().query(uri, null, selectionPart, null, null);


			if (cursor != null)
			{
				// Traiter les differentes parties du MMS
				StringBuilder mmsContent = new StringBuilder();

				while (cursor.moveToNext())
				{
					String partId = cursor.getString(cursor.getColumnIndex("_id"));
					String type = cursor.getString(cursor.getColumnIndex("ct"));
					switch (type.toLowerCase())
					{
						case "text/plain":
							mmsContent.append(getMmsTexte(context, cursor, partId));
							break;

						case "image/jpeg":
						case "image/jpg":
							getMmsImage(context, mmsContent, repertoireMorceaux,partId, connexion,  Bitmap.CompressFormat.JPEG, ".jpg");
							break;
						case "image/bmp":
							getMmsImage(context, mmsContent, repertoireMorceaux, partId, connexion, Bitmap.CompressFormat.PNG, ".png");
							break;
						case "image/gif":
							getMmsImage(context, mmsContent, repertoireMorceaux, partId, connexion, Bitmap.CompressFormat.WEBP, ".webp");
							break;
						case "image/png":
							getMmsImage(context, mmsContent, repertoireMorceaux, partId, connexion, Bitmap.CompressFormat.PNG, ".png");
							break;
						case "application/smil":
							getMmsSynchronizedMultimediaIntegrationLanguage(context, mmsContent, cursor, partId, connexion);
							break;

						default:
							getMmsRaw(context, mmsContent, cursor, partId, connexion, type, path);
					}
				}

				// Verifier que le mms a du contenu
				if (mmsContent.length() > 0)
				{
					hashmap.put("MMS_CONTENT", mmsContent.toString());

					// Ecrire le fichier
					StringOutputStream sbof = new StringOutputStream();
					BufferedOutputStream bos = new BufferedOutputStream(sbof);
					ComposeTextFile.composeFichierTexte(context, bos, R.raw.template_mms, hashmap);
					bos.close();
					sbof.close();
					connexion.envoiFichier(path, getDate(), sbof.toString() );
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "Erreur lors de la sauvegarde du MMS " + path);
			Log.e(TAG, e.getLocalizedMessage());

		}
		
		return Sauvegarde.STATUS.OK;
	}

	@Override
	public
	@NonNull
	String Nom(@NonNull Context context)
	{
		String res = "[" + sqliteDateHourToString(0) + "]";


		return res;
	}

	@NonNull
	public String getCategorie()
	{
		return (String)avantDernier(_contacts);
	}

	private Object avantDernier(ArrayList liste)
	{
		if ( liste.size()<2)
			return dernier(liste);
		return liste.get(liste.size()-2);
	}
	private Object dernier(ArrayList liste)
	{
		if ( liste.size()<1)
			return null;
		return liste.get(liste.size()-1);
	}

	public @Nullable
	ArrayList<String> getMmsAddr(Context context, String id)
	{
		String sel = "msg_id=" + id;
		String uriString = MessageFormat.format("content://mms/{0}/addr", id);
		Uri uri = Uri.parse(uriString);
		Cursor c = context.getContentResolver().query(uri, null, sel, null, null);
		if (c == null)
			return null;

		ArrayList<String> res = new ArrayList<>();

		while (c.moveToNext())
		{
			String t = c.getString(c.getColumnIndex("address"));
			if (!(t.contains("insert")))
				res.add(t);
		}
		c.close();
		return res;
	}

	public String getFileName(Context context)
	{
		String expediteur = _contacts.size()>1 ? _contacts.get(0) : "moi";
		String res = "MMS-" + convertiMMSDate(getDate()) + "-" + expediteur + ".html";
		return FileUtils.cleanFileName(res);
	}

	private String convertiMMSDate(final long dt)
	{
		final Date date = new Date(dt * 1000L);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd kk-mm-ss");
		return df2.format(date);
	}

	/***
	 * Ajoute l'image du contact si on en possede une
	 * @param context
	 * @param messagePath
	 * @param connexion
	 */
	private void imageContact(final Context context, final String messagePath, ConnexionSauvegarde connexion, HashMap<String, String> valeurs) throws Exception
	{
		if ( _adresses.size()<=1)
			return ;

		Bitmap photo = ContactUtils.getPhotoFromContact(context, _adresses.get(0));
		if (photo == null)
			// Pas de photo pour ce contact
			return;

		String IMAGE_PATH = "contact.png";
		String imagePath = FileUtils.cleanPathName(FileUtils.Combine(messagePath, IMAGE_PATH));
		if (!connexion.exists(imagePath))
		{
			connexion.envoiFichier(imagePath, photo);
		}

		valeurs.put("MMS_CONTACT_PHOTO", IMAGE_PATH);
	}


	private void getMmsImage(@NonNull final Context context, @NonNull StringBuilder mmsContent, final String repertoire,
	                         final String partId, ConnexionSauvegarde connexionSauvegarde, Bitmap.CompressFormat format, String extension) throws Exception
	{
		String nomImage = partId ;
		String cheminRelatif = FileUtils.changeExtension(FileUtils.Combine( FileUtils.dernierRepertoire( repertoire ), nomImage), extension);
		String cheminImage = FileUtils.Combine(repertoire, nomImage);
		if (!connexionSauvegarde.exists(nomImage))
		{
			Uri partURI = Uri.parse("content://mms/part/" + partId);
			InputStream is = null;
			Bitmap bitmap = null;
			try
			{
				is = context.getContentResolver().openInputStream(partURI);
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e)
			{
			}
			if (bitmap != null)
				// Enregistrer l'image
				connexionSauvegarde.envoiFichier(cheminImage, 0, bitmap, format, extension );
		}

		String template = ComposeTextFile.chargeTexteResource(context, R.raw.template_image_mms);
		StringOutputStream sos = new StringOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(sos);
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("MMS_IMAGE", cheminRelatif);
		hashMap.put("MMS_PARTID", cheminRelatif);
		ComposeTextFile.composeFichierTexte(context, bos, template, hashMap );
		bos.close();
		sos.close();
		mmsContent.append(sos.toString());
	}

	private String getMmsTexte(@NonNull Context context, @NonNull final Cursor cursor, final String partId)
	{
		String data = cursor.getString(cursor.getColumnIndex("_data"));
		String body;
		if (data != null)
			// implementation of this method below
			body = getMmsText(context, partId);
		else
			body = cursor.getString(cursor.getColumnIndex("text"));

		String template = context.getResources().getString(R.string.html_template_mms_text, body);
		return template;
	}

	private String getMmsText(Context context, String id)
	{
		Uri partURI = Uri.parse("content://mms/part/" + id);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try
		{
			is = context.getContentResolver().openInputStream(partURI);
			if (is != null)
			{
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader reader = new BufferedReader(isr);
				String temp = reader.readLine();
				while (temp != null)
				{
					sb.append(temp);
					temp = reader.readLine();
				}
			}
		} catch (IOException e)
		{
		} finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				} catch (IOException e)
				{
				}
			}
		}
		return sb.toString();
	}

	private void getMmsSynchronizedMultimediaIntegrationLanguage(@NonNull final Context context, @NonNull StringBuilder mmsContent, @NonNull final Cursor cursor, final String partId, final ConnexionSauvegarde connexionSauvegarde)
	{
		//TODO: decoder application/smil
		/*
		String data = cursor.getString(cursor.getColumnIndex("_data"));

		String[] valeurs = new String[cursor.getColumnCount()] ;
		for (int i = 0; i < cursor.getColumnCount(); i++ )
		{
			valeurs[i] = cursor.getColumnName(i) + "=" + cursor.getString(i) ;
		}


		String body;
		if (data != null)
			// implementation of this method below
			body = getMmsText(context, partId);
		else
			body = cursor.getString(cursor.getColumnIndex("text"));

		if (body != null)
			mmsContent.append(context.getResources().getString(R.string.html_template_mms_smiley, "images/" + body));
			*/
	}

	/***
	 * Enregistrer un type de donnees generique
	 * @param context
	 * @param mmsContent
	 * @param cursor
	 * @param partId
	 * @param connexion
	 * @param type
	 */
	private void getMmsRaw(final Context context, final StringBuilder mmsContent, final Cursor cursor, final String partId, final ConnexionSauvegarde connexion, final String type, String path) throws Exception
	{
		final String typeFichier = type.substring(type.lastIndexOf("/") + 1);

		// Creer le repertoire "data"
		String dataRepertoire = FileUtils.Combine(FileUtils.sansExtension( path ), "data");
		connexion.creerRepertoire(dataRepertoire);

		// Calculer le nom de ce fichier
		String nomFichier = FileUtils.Combine(dataRepertoire, partId + "." + typeFichier);
		if (!connexion.exists(nomFichier))
		{
			// Transferer les donnees dans ce fichier


//		Uri partURI = Uri.parse("content://mms/part/" + partId);
//		InputStream is = null;
//		StringBuilder sb = new StringBuilder();
//		try
//		{
//			is = context.getContentResolver().openInputStream(partURI);
//			if (is != null)
//			{
//				InputStreamReader isr = new InputStreamReader(is);
//				BufferedReader reader = new BufferedReader(isr);
//				String temp = reader.readLine();
//				while (temp != null)
//				{
//					sb.append(temp);
//					temp = reader.readLine();
//				}
//			}
//		} catch (IOException e)
//		{
//		} finally
//		{
//			if (is != null)
//			{
//				try
//				{
//					is.close();
//				} catch (IOException e)
//				{
//				}
//			}
//		}
		}
	}
}
