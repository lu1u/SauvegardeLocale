package com.lpi.sauvegardelocale.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
public class MessageBoxUtils
{
	public static final int BOUTON_OK = 1;
	public static final int BOUTON_CANCEL = 2;

	public static void messageBox(@NonNull Context a, @NonNull String titre, @NonNull String texte, int boutons, final Listener listener)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		builder.setTitle(titre);
		builder.setMessage(texte);
		if ( (boutons & BOUTON_OK) != 0)
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id)
				{
				listener.onOk();
				}
			});

		if ( (boutons & BOUTON_CANCEL) != 0)
			builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id)
				{
					listener.onCancel();
				}
			});

		// Create the AlertDialog object and return it
		builder.create().show();
	}
	public interface Listener
	{
		public void onOk();
		public void onCancel();
	}
}
