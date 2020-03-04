package com.lpi.sauvegardelocale.Sauvegardes.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lpi.sauvegardelocale.R;
import com.lpi.sauvegardelocale.Sauvegardes.ProfilSauvegarde;


/**
 * Adapter pour afficher les profils
 */
public class ProfilsSauvegardeAdapter extends CursorAdapter
{
	private Context _context;
	public ProfilsSauvegardeAdapter(Context context, Cursor cursor)
	{
		super(context, cursor, 0);
		_context = context;
	}

	// The newView method is used to inflate a new view and return it,
	// you don't bind any data to the view at this point.
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		return LayoutInflater.from(context).inflate(R.layout.element_liste_profil, parent, false);
	}

	// The bindView method is used to bind all data to a given view
	// such as setting the text on a TextView.
	@Override
	public void bindView(final View view, final Context context, Cursor cursor)
	{
		// Find fields to populate in inflated template
		final ProfilSauvegarde profil = new ProfilSauvegarde(cursor);
		TextView tvNom = view.findViewById(R.id.textViewNom);
		if (tvNom != null)
			tvNom.setText(profil.nom);
		TextView tvServeur = view.findViewById(R.id.textViewServeur);
		if (tvServeur != null)
			tvServeur.setText(profil.adresse);
		TextView tvPort = view.findViewById(R.id.textViewPort);
		if (tvPort != null)
			tvPort.setText(Integer.toString(profil.port));

		ImageView btn = view.findViewById(R.id.imageViewDelete);
		btn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				onDeleteProfil(profil);

			}
		});
	}

	void onDeleteProfil(@NonNull final ProfilSauvegarde profil)
	{
		new AlertDialog.Builder(_context)
				.setTitle("Supprimer")
				.setMessage("Voulez-vous vraiment supprimer le profil '" + profil.nom + "'?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						ProfilsDatabase database = ProfilsDatabase.getInstance(_context);
						database.deleteProfil(profil.Id);
						ProfilsSauvegardeAdapter.this.swapCursor(ProfilsDatabase.getInstance(_context).getCursor());
						//ProfilsSauvegardeAdapter.this.onContentChanged();//notifyDataSetChanged();
					}
				})
				.setNegativeButton(android.R.string.no, null).show();
	}

	@Nullable
	public ProfilSauvegarde getItem(int position)
	{
		Cursor cursor = getCursor();

		if (cursor.moveToPosition(position))
			return new ProfilSauvegarde(cursor);
		return null;
	}

}