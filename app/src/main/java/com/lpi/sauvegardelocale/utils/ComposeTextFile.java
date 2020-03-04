package com.lpi.sauvegardelocale.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/***
 * Creer un fichier texte a partir d'un fichier contenant des "placeholders" et d'une
 * hashmap contenant les valeurs a inserer:
 * Forme des places holders: @@NOM@@, @@NOM2@@, @@VALEUR@@
 * Contenu de la hashmap:
 *  NOM=VALEUR
 *  NOM2=VALEUR2
 *  VALEUR=Quelque chose
 */
public class ComposeTextFile
{
	public static final String TAG = "ComposeTextFile";
	public static final String PLACEHOLDER_MARKER = "@@";
	public static final int LONGUEUR_MARKER = PLACEHOLDER_MARKER.length();

	/***
	 * Compose un fichier texte a partir d'un fichier comportant des placeholders et une hashmap
	 * de valeurs a remplacer
	 * @param context
	 * @param os
	 * @param resourceTemplate
	 * @param valeurs
	 */
	public static void composeFichierTexte(@NonNull Context context, @NonNull BufferedOutputStream os, int resourceTemplate, @NonNull final HashMap<String, String> valeurs)
	{
		try
		{
			composeFichierTexte(context, os, chargeTexteResource(context, resourceTemplate), valeurs);
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	public static void composeFichierTexte(@NonNull Context context, @NonNull BufferedOutputStream os, @NonNull final String template, @NonNull final HashMap<String, String> valeurs)
	{
		try
		{
			// Parcourir le template a la recherche des placeholders
			final int longueurTemplate = template.length();
			int i = 0;
			while (i < longueurTemplate)
			{
				// Debut du prochain placeholder
				int indiceDebut = template.indexOf(PLACEHOLDER_MARKER, i);
				if (indiceDebut == -1)
				{
					os.write(template.substring(i).getBytes());
					i = longueurTemplate;
				}
				else
				{
					// On a trouvé le debut du placeholder, trouver la fin
					int indiceFin = template.indexOf(PLACEHOLDER_MARKER, indiceDebut + LONGUEUR_MARKER);
					if (indiceFin == -1)
						// Pas de marqueur de fin trouvé, c'est une erreur
						throw new Exception("Placeholder sans terminaison dans le fichier, indice=" + indiceDebut);

					// Ecrire la partie de texte qui se trouve avant le placeholder
					final String debut = template.substring(i, indiceDebut);
					os.write(template.substring(i, indiceDebut).getBytes());

					// Extraire le nom du template
					final String placeholder = template.substring(indiceDebut + LONGUEUR_MARKER, indiceFin);
					// Valeur correspondante
					final String valeur = valeurs.get(placeholder);
					if (valeur != null)
					{
						os.write(valeur.getBytes());
					}

					i = indiceFin + LONGUEUR_MARKER;
				}
			}
		} catch (Exception e)
		{
			Log.e(TAG, "Erreur lors de la composition de fichier texte");
			Log.e(TAG, e.getLocalizedMessage());
		}

	}

	/***
	 * Charge un fichier texte a partir d'une resource
	 * @param context
	 * @param resourceTemplate
	 * @return
	 */
	public static String chargeTexteResource(final Context context, final int resourceTemplate) throws Exception
	{
		InputStream inputStream = context.getResources().openRawResource(resourceTemplate);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int size;
		while ((size = inputStream.read(buffer)) != -1)
		{
			outputStream.write(buffer, 0, size);
		}
		outputStream.close();
		inputStream.close();
		return outputStream.toString();
	}

}
