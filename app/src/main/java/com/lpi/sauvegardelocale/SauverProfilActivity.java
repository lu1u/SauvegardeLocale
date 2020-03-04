package com.lpi.sauvegardelocale;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.lpi.sauvegardelocale.Network.NetworkUtils;
import com.lpi.sauvegardelocale.Sauvegardes.ProfilSauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.database.ProfilsDatabase;

public class SauverProfilActivity
{

	public static void start(@NonNull final Activity context, @NonNull final ProfilSauvegarde p)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		//AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, Utils.getTheme(context));
		LayoutInflater inflater = context.getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.activity_sauver_profil, null);

		NetworkUtils.chercheHostname( context, p.adresse, new NetworkUtils.HostnameFromAddress()
		{
			@Override
			public void onHostnameFound(@NonNull final String hostname)
			{
				((TextView)dialogView.findViewById(R.id.textViewNomServeur)).setText(hostname);
			}
		});

		final EditText etNomProfil = dialogView.findViewById(R.id.editTextNomProfil);
		final Button btnAnnuler = dialogView.findViewById(R.id.buttonAnnuler);
		final Button btnOk = dialogView.findViewById(R.id.buttonOk);

		if ( p.nom!= null && p.nom.length()>0)
			((TextView) dialogView.findViewById(R.id.textViewNom)).setText(p.nom);
		else
		{
			int nb = ProfilsDatabase.getInstance(context).nbProfils() + 1;
			String message = context.getResources().getString( R.string.format_nom_profil, nb);
			etNomProfil.setText(message);
		}
		((TextView) dialogView.findViewById(R.id.textViewAdresse)).setText(p.adresse);
		((TextView) dialogView.findViewById(R.id.textViewPort)).setText(Integer.toString(p.port));
		((CheckBox) dialogView.findViewById(R.id.checkBoxContacts)).setChecked(p.contacts);
		((CheckBox) dialogView.findViewById(R.id.checkBoxAppels)).setChecked(p.appels);
		((CheckBox) dialogView.findViewById(R.id.checkBoxMessages)).setChecked(p.messages);
		((CheckBox) dialogView.findViewById(R.id.checkBoxPhotos)).setChecked(p.photos);
		((CheckBox) dialogView.findViewById(R.id.checkBoxVideos)).setChecked(p.videos);

		etNomProfil.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
			                              int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start,
			                          int before, int count)
			{
				btnOk.setEnabled(s.length() > 0);
			}
		});

		btnOk.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				p.nom = etNomProfil.getText().toString();
				sauveProfil(context, p);
				dialogBuilder.dismiss();
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

	/***
	 * Sauve le profil
	 * @param context
	 * @param p
	 */
	private static void sauveProfil(@NonNull final Activity context, @NonNull final ProfilSauvegarde p)
	{
		ProfilsDatabase db = ProfilsDatabase.getInstance(context);
		if  ( db.ajouteProfil(p))
		{
			Snackbar.make(context.getWindow().getDecorView().getRootView(), "Profil " + p.nom + " enregistré", Snackbar.LENGTH_LONG).show();
		}
	}

}
