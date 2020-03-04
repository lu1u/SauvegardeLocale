package com.lpi.sauvegardelocale.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lpi.sauvegardelocale.Network.ServerList;
import com.lpi.sauvegardelocale.R;

import java.util.List;

public class ServeursArrayAdapter extends ArrayAdapter<ServerList.Serveur>
{

	private Context mContext;

	public ServeursArrayAdapter(Context context, List<ServerList.Serveur> items)
	{
		super(context, R.layout.element_liste_serveur, items);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View v = convertView;

		if (v == null)
		{
			LayoutInflater vi = LayoutInflater.from(mContext);
			v = vi.inflate(R.layout.element_liste_serveur, null);
		}

		ServerList.Serveur p = getItem(position);

		if (p != null)
		{
			TextView tvAdresse = v.findViewById(R.id.textViewAddress);
			TextView tvNom = v.findViewById(R.id.textViewNom);
			TextView tvEtat = v.findViewById(R.id.textViewEtatServeur);

			tvAdresse.setText(p.adresse);
			tvNom.setText(p.nom);
			tvEtat.setText(p.contacte ? "Serveur de sauvegarde actif" : "Pas de serveur de sauvegarde actif");
		}
		return v;
	}
}
