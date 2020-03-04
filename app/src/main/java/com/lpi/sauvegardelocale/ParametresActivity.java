package com.lpi.sauvegardelocale;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lpi.sauvegardelocale.utils.Preferences;

class ParametresActivity
{
	public static void start(@NonNull final Activity context)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.activity_parametres, null);

		final EditText etContacts = dialogView.findViewById(R.id.editTextContacts);
		final EditText etAppels = dialogView.findViewById(R.id.editTextAppels);
		final EditText etMessages= dialogView.findViewById(R.id.editTextMessages);
		final EditText etMMS= dialogView.findViewById(R.id.editTextMMS);
		final EditText etPhotos= dialogView.findViewById(R.id.editTextPhotos);
		final EditText etVideos= dialogView.findViewById(R.id.editTextVideos);
		final Button btnOk = dialogView.findViewById(R.id.buttonOk);
		final Button btnAnnuler = dialogView.findViewById(R.id.buttonAnnuler);

		final Preferences p = Preferences.getInstance(context);
		etContacts.setText( p.getString(Preferences.PREF_REPERTOIRE_CONTACTS, "Contacts"));
		etAppels.setText( p.getString(Preferences.PREF_REPERTOIRE_APPELS, "Appels"));
		etMessages.setText( p.getString(Preferences.PREF_REPERTOIRE_MESSAGES, "Messages"));
		etMMS.setText( p.getString(Preferences.PREF_REPERTOIRE_MMS, "MMS"));
		etPhotos.setText( p.getString(Preferences.PREF_REPERTOIRE_PHOTOS, "Photos"));
		etVideos.setText( p.getString(Preferences.PREF_REPERTOIRE_VIDEOS, "Videos"));

		btnOk.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				// Sauver les valeurs
				String contacts = etContacts.getText().toString();
				String appels = etAppels.getText().toString();
				String messages = etMessages.getText().toString();
				String MMS = etMMS.getText().toString();
				String photos = etPhotos.getText().toString();
				String videos = etVideos.getText().toString();

				if ( contacts.length()==0 || appels.length()==0 || messages.length()==0 || MMS.length() ==0 || photos.length()==0 || videos.length()==0)
				{
					new AlertDialog.Builder(context)
							.setTitle("Répertoire")
							.setMessage("Les noms de répertoires ne doivent pas être vides")
							.setIcon(android.R.drawable.ic_dialog_info).show();
				}
				else
				{
					p.put(Preferences.PREF_REPERTOIRE_CONTACTS, contacts);
					p.put(Preferences.PREF_REPERTOIRE_APPELS, appels);
					p.put(Preferences.PREF_REPERTOIRE_MESSAGES, messages);
					p.put(Preferences.PREF_REPERTOIRE_MMS, MMS);
					p.put(Preferences.PREF_REPERTOIRE_PHOTOS, photos);
					p.put(Preferences.PREF_REPERTOIRE_VIDEOS, videos);
					dialogBuilder.dismiss();
				}
			}
		});


		btnAnnuler.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				dialogBuilder.dismiss();
			}
		});

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Afficher la fenetre
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
