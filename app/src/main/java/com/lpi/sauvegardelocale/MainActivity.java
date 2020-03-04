package com.lpi.sauvegardelocale;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lpi.sauvegardelocale.Network.ServerList;
import com.lpi.sauvegardelocale.Sauvegardes.InterfaceSauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.ProfilSauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.Sauvegarde;
import com.lpi.sauvegardelocale.Sauvegardes.SauvegardeAppels;
import com.lpi.sauvegardelocale.Sauvegardes.SauvegardeContacts;
import com.lpi.sauvegardelocale.Sauvegardes.SauvegardeImages;
import com.lpi.sauvegardelocale.Sauvegardes.SauvegardeMMS;
import com.lpi.sauvegardelocale.Sauvegardes.SauvegardeManager;
import com.lpi.sauvegardelocale.Sauvegardes.SauvegardeMessages;
import com.lpi.sauvegardelocale.Sauvegardes.SauvegardeVideos;
import com.lpi.sauvegardelocale.Sauvegardes.database.ProfilsDatabase;
import com.lpi.sauvegardelocale.Sauvegardes.database.ProfilsSauvegardeAdapter;
import com.lpi.sauvegardelocale.utils.Preferences;
import com.lpi.sauvegardelocale.utils.ServeursArrayAdapter;
import com.lpi.sauvegardelocale.utils.SnackbarUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements ServerList.ServerListListener, Sauvegarde.SauvegardeListener
{
	EditText _editServer;
	EditText _editPort;
	CheckBox _cbContacts, _cbAppels, _cbMessages, _cbMMS, _cbPhotos, _cbVideos;
	ProgressBar _pbContacts, _pbAppels, _pbMessages, _pbMMS, _pbImages, _pbVideos;
	TextView _tvContacts, _tvAppels, _tvMessages, _tvMMS, _tvImages, _tvVideos;
	ToggleButton _modeSauvegarde, _modeRestauration;
	ImageButton _boutonListeServeurs;
	ProgressBar _progressBarRechercheServeurs;
	FloatingActionButton _fab;
	TextView _textViewNbTrouves;
	private int _nbSauvegardesEnCours;
	private Sauvegarde.STATUS _statusSauvegarde;
	int nbRestaurations = 0;

	public enum MODE_SAUVEGARDE
	{
		SAUVEGARDE, RESTAURATION;
		static public MODE_SAUVEGARDE fromInt(int v)
		{
			return v == 0 ? SAUVEGARDE : RESTAURATION;
		}

		public static int toInt(MODE_SAUVEGARDE m)
		{
			return m == SAUVEGARDE ? 0 : 1;
		}
	};
	private MODE_SAUVEGARDE _modeInterface = MODE_SAUVEGARDE.SAUVEGARDE;

	private static final Pattern PARTIAl_IP_ADDRESS =
			Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}" +
					"((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$");

	@Override
	protected void onPause()
	{
		super.onPause();
		ProfilSauvegarde p = new ProfilSauvegarde();
		p.adresse = _editServer.getText().toString();
		p.port = Integer.parseInt(_editPort.getText().toString());
		p.contacts = _cbContacts.isChecked();
		p.appels = _cbAppels.isChecked();
		p.messages = _cbMessages.isChecked();
		p.photos = _cbPhotos.isChecked();
		p.videos = _cbVideos.isChecked();
		p.MMS = _cbMMS.isChecked();
		p.saveToPreferences(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		ProfilSauvegarde p = new ProfilSauvegarde();
		p.fromPreferences(this);

		_editServer.setText(p.adresse);
		_editPort.setText(Integer.toString(p.port));
		_cbContacts.setChecked(p.contacts);
		_cbAppels.setChecked(p.appels);
		_cbMessages.setChecked(p.messages);
		_cbPhotos.setChecked(p.photos);
		_cbVideos.setChecked(p.videos);
		_cbMMS.setChecked(p.MMS);

		_modeInterface = MODE_SAUVEGARDE.fromInt( Preferences.getInstance(this).getInt(Preferences.PREF_MODE_UI, MODE_SAUVEGARDE.toInt(_modeInterface)));
		initCompteObjets();
		updateModeUI();
	}

	/**
	 * Affiche les nombres d'objets par catégorie
	 */
	private void initCompteObjets()
	{
		SauvegardeContacts.Compte(this, new Sauvegarde.CompteObjetsListener()
		{
			@Override
			public void onObjetsComptes(final int nb)
			{
				_tvContacts.setText("" + nb);
				_tvContacts.setVisibility(View.VISIBLE);
			}
		});
		SauvegardeMessages.Compte(this, new Sauvegarde.CompteObjetsListener()
		{
			@Override
			public void onObjetsComptes(final int nb)
			{
				_tvMessages.setText("" + nb);
				_tvMessages.setVisibility(View.VISIBLE);
			}
		});
		SauvegardeAppels.Compte(this, new Sauvegarde.CompteObjetsListener()
		{
			@Override
			public void onObjetsComptes(final int nb)
			{
				_tvAppels.setText("" + nb);
				_tvAppels.setVisibility(View.VISIBLE);
			}
		});
		SauvegardeMMS.Compte(this, new Sauvegarde.CompteObjetsListener()
		{
			@Override
			public void onObjetsComptes(final int nb)
			{
				_tvMMS.setText("" + nb);
				_tvMMS.setVisibility(View.VISIBLE);
			}
		});
		SauvegardeImages.Compte(this, new Sauvegarde.CompteObjetsListener()
		{
			@Override
			public void onObjetsComptes(final int nb)
			{
				_tvImages.setText("" + nb);
				_tvImages.setVisibility(View.VISIBLE);
			}
		});
		SauvegardeVideos.Compte(this, new Sauvegarde.CompteObjetsListener()
		{
			@Override
			public void onObjetsComptes(final int nb)
			{
				_tvVideos.setText("" + nb);
				_tvVideos.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		_fab = findViewById(R.id.fab);
		_fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (_nbSauvegardesEnCours == 0)
				{
					if (_modeInterface == MODE_SAUVEGARDE.SAUVEGARDE)
						lanceSauvegardes();
					else
						lanceRestaurations();
				}
				else
				{
					Snackbar.make(view, "Annulation", Snackbar.LENGTH_INDEFINITE).show();
					Sauvegarde.annule();
				}
			}
		});

		checkPermissions();
		initCompteObjets();
		getControlsFromId();
		lanceRechercheServeurs();
		_editServer.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			private String mPreviousText = "";

			@Override
			public void afterTextChanged(Editable s)
			{
				if (PARTIAl_IP_ADDRESS.matcher(s).matches())
				{
					mPreviousText = s.toString();
				}
				else
				{
					s.replace(0, s.length(), mPreviousText);
				}
			}
		});
	}

	/**
	 * Demarre le processus de tache de fond pour chercher des serveurs joignables sur le reseau
	 */
	private void lanceRechercheServeurs()
	{
		final int port = Integer.parseInt(_editPort.getText().toString());
		ServerList.chercheServeurs(this, this, port);
		_progressBarRechercheServeurs.setProgress(0);
		_progressBarRechercheServeurs.setVisibility(View.VISIBLE);
		_boutonListeServeurs.setVisibility(View.GONE);
	}


	/***
	 * Verifie et demandes les permissions necessaires à l'application
	 */
	private void checkPermissions()
	{
		ActivityCompat.requestPermissions(this,
				new String[]{
						Manifest.permission.READ_CONTACTS,
						Manifest.permission.WRITE_CONTACTS,
						Manifest.permission.READ_CALL_LOG,
						Manifest.permission.WRITE_CALL_LOG,
						Manifest.permission.READ_SMS,
						"android.permission.WRITE_SMS",
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.BROADCAST_WAP_PUSH,
						Manifest.permission.SEND_RESPOND_VIA_MESSAGE
				}, 0);
	}

	/***
	 * Lancer les sauvegardes
	 */
	private void lanceSauvegardes()
	{
		Snackbar.make(getWindow().getDecorView().getRootView(), "Lancement de la sauvegarde", Snackbar.LENGTH_LONG).show();

		String serveur = _editServer.getText().toString();
		int port = Integer.parseInt(_editPort.getText().toString());

		ServerList.stop();
		SauvegardeManager.actionSiConnecte(this, serveur, port, new SauvegardeManager.ActionSiConnecte()
		{
			@Override
			public void onServeurConnecte(final boolean connecte)
			{
				if (connecte)
					lancerToutesLesSauvegardes();
				else
				{
					// Message d'erreur
					SnackbarUtils.showIndeterminate(MainActivity.this, "Serveur injoignable");
					lanceRechercheServeurs();
				}

			}
		});
	}

	/***
	 * Lancer les sauvegardes
	 */
	private void lanceRestaurations()
	{
		Snackbar.make(getWindow().getDecorView().getRootView(), "Lancement de la restauration", Snackbar.LENGTH_LONG).show();

		String serveur = _editServer.getText().toString();
		int port = Integer.parseInt(_editPort.getText().toString());

		ServerList.stop();
		SauvegardeManager.actionSiConnecte(this, serveur, port, new SauvegardeManager.ActionSiConnecte()
		{
			@Override
			public void onServeurConnecte(final boolean connecte)
			{
				if (connecte)
					lancerToutesLesRestaurations();
				else
				{
					// Message d'erreur
					SnackbarUtils.showIndeterminate(MainActivity.this, "Serveur injoignable");
					lanceRechercheServeurs();
				}

			}
		});
	}

	private void lancerToutesLesSauvegardes()
	{
		SnackbarUtils.show(this, "Sauvegarde lancée", "Annuler", new SnackbarUtils.SnackBarListener()
		{
			@Override
			public void onSnackbarAction()
			{
				SnackbarUtils.showIndeterminate(MainActivity.this, "Annulation de la sauvegarde");
				Sauvegarde.annule();
			}
		});
		String serveur = _editServer.getText().toString();
		String port = _editPort.getText().toString();

		ArrayList<InterfaceSauvegarde> sauvegardes = new ArrayList<>();
		if (_cbContacts.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeContacts(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvContacts;
			i.progressBar = _pbContacts;
			sauvegardes.add(i);
		}

		if (_cbAppels.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeAppels(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvAppels;
			i.progressBar = _pbAppels;
			sauvegardes.add(i);
		}

		if (_cbMessages.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeMessages(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvMessages;
			i.progressBar = _pbMessages;
			sauvegardes.add(i);
		}
		if (_cbMMS.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeMMS(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvMMS;
			i.progressBar = _pbMMS;
			sauvegardes.add(i);
		}
		if (_cbPhotos.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeImages(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvImages;
			i.progressBar = _pbImages;
			sauvegardes.add(i);
		}

		if (_cbVideos.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeVideos(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvVideos;
			i.progressBar = _pbVideos;
			sauvegardes.add(i);
		}

		if (sauvegardes.size() > 0)
		{
			try
			{
				_nbSauvegardesEnCours = sauvegardes.size();

				_cbContacts.setEnabled(false);
				_cbAppels.setEnabled(false);
				_cbMessages.setEnabled(false);
				_cbPhotos.setEnabled(false);
				_cbVideos.setEnabled(false);
				_cbMMS.setEnabled(false);

				_pbContacts.setProgress(0);
				_pbAppels.setProgress(0);
				_pbMessages.setProgress(0);
				_pbImages.setProgress(0);
				_pbVideos.setProgress(0);
				_pbMMS.setProgress(0);

				_pbContacts.setVisibility(View.VISIBLE);
				_pbAppels.setVisibility(View.VISIBLE);
				_pbMessages.setVisibility(View.VISIBLE);
				_pbImages.setVisibility(View.VISIBLE);
				_pbVideos.setVisibility(View.VISIBLE);
				_pbMMS.setVisibility(View.VISIBLE);

				_tvContacts.setVisibility(View.VISIBLE);
				_tvAppels.setVisibility(View.VISIBLE);
				_tvMessages.setVisibility(View.VISIBLE);
				_tvImages.setVisibility(View.VISIBLE);
				_tvVideos.setVisibility(View.VISIBLE);
				_tvMMS.setVisibility(View.VISIBLE);

				ServerList.stop();
				_statusSauvegarde = Sauvegarde.STATUS.OK;

				Sauvegarde.setConnexion(serveur, Integer.valueOf(port));
				for (InterfaceSauvegarde i : sauvegardes)
					i.sauvegarde.execute();
				//_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop_circle));
				_fab.hide();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void lancerToutesLesRestaurations()
	{
		SnackbarUtils.show(this, "Restauration lancée", "Annuler", new SnackbarUtils.SnackBarListener()
		{
			@Override
			public void onSnackbarAction()
			{
				SnackbarUtils.showIndeterminate(MainActivity.this, "Annulation de la restauration");
				Sauvegarde.annule();
			}
		});
		String serveur = _editServer.getText().toString();
		String port = _editPort.getText().toString();

		final ArrayList<InterfaceSauvegarde> restaurations = new ArrayList<>();
		if (_cbContacts.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeContacts(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvContacts;
			i.progressBar = _pbContacts;
			restaurations.add(i);
		}

		if (_cbAppels.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeAppels(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvAppels;
			i.progressBar = _pbAppels;
			restaurations.add(i);
		}

		if (_cbMessages.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeMessages(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvMessages;
			i.progressBar = _pbMessages;
			restaurations.add(i);
		}
		if (_cbMMS.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeMMS(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvMMS;
			i.progressBar = _pbMMS;
			restaurations.add(i);
		}
		if (_cbPhotos.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeImages(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvImages;
			i.progressBar = _pbImages;
			restaurations.add(i);
		}

		if (_cbVideos.isChecked())
		{
			InterfaceSauvegarde i = new InterfaceSauvegarde();
			Sauvegarde s = new SauvegardeVideos(this, i, this);
			i.sauvegarde = s;
			i.tvStatus = _tvVideos;
			i.progressBar = _pbVideos;
			restaurations.add(i);
		}

		if (needSystemAction(restaurations))
			return;

		if (restaurations.size() > 0)
		{
			try
			{
				_nbSauvegardesEnCours = restaurations.size();

				_cbContacts.setEnabled(false);
				_cbAppels.setEnabled(false);
				_cbMessages.setEnabled(false);
				_cbPhotos.setEnabled(false);
				_cbVideos.setEnabled(false);
				_cbMMS.setEnabled(false);

				_pbContacts.setProgress(0);
				_pbAppels.setProgress(0);
				_pbMessages.setProgress(0);
				_pbImages.setProgress(0);
				_pbVideos.setProgress(0);
				_pbMMS.setProgress(0);

				_pbContacts.setVisibility(View.VISIBLE);
				_pbAppels.setVisibility(View.VISIBLE);
				_pbMessages.setVisibility(View.VISIBLE);
				_pbImages.setVisibility(View.VISIBLE);
				_pbVideos.setVisibility(View.VISIBLE);
				_pbMMS.setVisibility(View.VISIBLE);

				_tvContacts.setVisibility(View.VISIBLE);
				_tvAppels.setVisibility(View.VISIBLE);
				_tvMessages.setVisibility(View.VISIBLE);
				_tvImages.setVisibility(View.VISIBLE);
				_tvVideos.setVisibility(View.VISIBLE);
				_tvMMS.setVisibility(View.VISIBLE);

				ServerList.stop();
				_statusSauvegarde = Sauvegarde.STATUS.OK;

				Sauvegarde.setConnexion(serveur, Integer.valueOf(port));

				for (InterfaceSauvegarde i : restaurations)
				{
					Sauvegarde.RestaurationListener listener = new Sauvegarde.RestaurationListener()
					{
						@Override
						public void onDebutRestauration()
						{
							nbRestaurations ++;
						}

						@Override
						public void onFinRestauration(final Sauvegarde.STATUS status)
						{
							nbRestaurations --;
							if ( nbRestaurations <= 0)
								finToutesRestaurations(restaurations);
						}

						@Override
						public void onRestaurationProgress(final InterfaceSauvegarde interf, final int progress, final int total)
						{
							interf.progressBar.setMax(total);
							interf.progressBar.setProgress(progress);
							interf.progressBar.invalidate();
							interf.tvStatus.setText(progress + "/" + total);
						}
					};
					i.sauvegarde.executeRestauration(listener);
				}
				//_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop_circle));
				_fab.hide();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/***
	 * La dernier restauration vient de se terminer
	 */
	private void finToutesRestaurations(ArrayList<InterfaceSauvegarde> restaurations)
	{
		for (InterfaceSauvegarde restauration : restaurations)
			restauration.sauvegarde.systemActionPostRestauration(this);

		// Fin de toutes les sauvegardes
		_cbContacts.setEnabled(true);
		_cbAppels.setEnabled(true);
		_cbMessages.setEnabled(true);
		_cbMMS.setEnabled(true);
		_cbPhotos.setEnabled(true);
		_cbVideos.setEnabled(true);
		_fab.show();
		//_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.send_network));
		String message;
		switch (_statusSauvegarde)
		{
			case OK:
				message = getResources().getString(R.string.restauration_ok);
				break;

			case FAILED:
				message = getResources().getString(R.string.restauration_failed);
				break;
			case CANCELED:
				message = getResources().getString(R.string.restauration_annulee);
				break;

			default:
				message = "";
		}

		SnackbarUtils.showIndeterminate(this, message);

		// Relance la recherche de serveurs
		lanceRechercheServeurs();
	}

	/***
	 * Verifie que les permissions/configurations du systeme pour permettre de lancer la restauration
	 * @param restaurations
	 * @return
	 */
	private boolean needSystemAction(@NonNull final ArrayList<InterfaceSauvegarde> restaurations)
	{
		for (InterfaceSauvegarde restauration : restaurations)
		if ( restauration.sauvegarde.needSystemActionForRestauration(this))
			return true;

		return false;
	}

	/***
	 * Recherche les controles
	 */
	private void getControlsFromId()
	{
		_editServer = findViewById(R.id.editTextServeur);
		_editPort = findViewById(R.id.editTextPort);
		_boutonListeServeurs = findViewById(R.id.imageButtonListeServeurs);
		_progressBarRechercheServeurs = findViewById(R.id.progressBarChercheServeurs);
		_textViewNbTrouves = findViewById(R.id.textViewNbTrouves);
		_cbContacts = findViewById(R.id.checkBoxContacts);
		_cbAppels = findViewById(R.id.checkBoxAppels);
		_cbMessages = findViewById(R.id.checkBoxMessages);
		_cbMMS = findViewById(R.id.checkBoxMMS);
		_cbPhotos = findViewById(R.id.checkBoxImages);
		_cbVideos = findViewById(R.id.checkBoxVideos);

		_pbContacts = findViewById(R.id.progressBarContacts);
		_pbAppels = findViewById(R.id.progressBarAppels);
		_pbMessages = findViewById(R.id.progressBarMessages);
		_pbMMS = findViewById(R.id.progressBarMMS);
		_pbImages = findViewById(R.id.progressBarPhotos);
		_pbVideos = findViewById(R.id.progressBarVideos);

		_tvContacts = findViewById(R.id.textViewStatusContacts);
		_tvAppels = findViewById(R.id.textViewStatusAppels);
		_tvMessages = findViewById(R.id.textViewStatusMessages);
		_tvMMS = findViewById(R.id.textViewStatusMMS);
		_tvImages = findViewById(R.id.textViewStatusPhotos);
		_tvVideos = findViewById(R.id.textViewStatusVideos);

		_modeRestauration = findViewById(R.id.toggleButtonRestauration);
		_modeSauvegarde= findViewById(R.id.toggleButtonSauvegarde);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id)
		{
			case R.id.action_sauver_profil:
			{
				ProfilSauvegarde p = new ProfilSauvegarde();
				p.adresse = _editServer.getText().toString();
				p.port = Integer.parseInt(_editPort.getText().toString());
				p.contacts = _cbContacts.isChecked();
				p.appels = _cbAppels.isChecked();
				p.messages = _cbMessages.isChecked();
				p.MMS = _cbMMS.isChecked();
				p.photos = _cbPhotos.isChecked();
				p.videos = _cbVideos.isChecked();
				SauverProfilActivity.start(this, p);
				return true;
			}

			case R.id.action_charger_profil:
				chargerProfil();
				return true;

			case R.id.action_settings:
				ParametresActivity.start(this);
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Afficher une liste pour choisir un profil à charger
	 */
	private void chargerProfil()
	{
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
		builderSingle.setTitle("Charger un profil");

		final ProfilsSauvegardeAdapter adapter = new ProfilsSauvegardeAdapter(this, ProfilsDatabase.getInstance(this).getCursor());
		builderSingle.setAdapter(
				adapter,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// Charger le profil sélectionné
						ProfilSauvegarde p = adapter.getItem(which);
						if (p != null)
						{
							_editServer.setText(p.adresse);
							_editPort.setText(Integer.toString(p.port));
							_cbContacts.setChecked(p.contacts);
							_cbAppels.setChecked(p.appels);
							_cbMessages.setChecked(p.messages);
							_cbMMS.setChecked(p.MMS);
							_cbPhotos.setChecked(p.photos);
							_cbVideos.setChecked(p.videos);
							Snackbar.make(getWindow().getDecorView().getRootView(), "Profil " + p.nom + " chargé", Snackbar.LENGTH_LONG).show();
						}
					}
				});

		builderSingle.show();
	}

	/***
	 * Le module de recherche de serveurs nous préviens qu'il a trouvé un nouveau adresse
	 * @param nb de serveurs trouves
	 */
	@Override
	public void onNouveauServeurJoignable(final int nb)
	{
		_boutonListeServeurs.setEnabled(true);
		_textViewNbTrouves.setText(getResources().getQuantityString(R.plurals.nombre_serveurs_trouvés, nb, nb));
		_boutonListeServeurs.setVisibility(nb > 0 ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onRechercheFinie(final int nbTrouves)
	{
		_progressBarRechercheServeurs.setVisibility(View.GONE);
		_textViewNbTrouves.setText(ServerList.getServeurs().size() + " serveurs trouvés");
	}

	@Override
	public void onRechercheCommence()
	{
		_boutonListeServeurs.setEnabled(false);
		_progressBarRechercheServeurs.setVisibility(View.VISIBLE);
	}

	@Override
	public void onNewServerTested(final int nbTested, final int nbMax)
	{
		_progressBarRechercheServeurs.setMax(nbMax);
		_progressBarRechercheServeurs.setProgress(nbTested);
	}

	public void onClickListeServeurs(View v)
	{
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
		builderSingle.setTitle("Serveurs trouvés sur ce réseau");

		//final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, _serveurs);
		final ServeursArrayAdapter arrayAdapter = new ServeursArrayAdapter(this, ServerList.getServeurs());
		builderSingle.setAdapter(
				arrayAdapter,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						_editServer.setText(arrayAdapter.getItem(which).adresse);
					}
				});
		builderSingle.show();
	}

	@Override
	public void onDebutSauvegarde()
	{

	}

	/***
	 * Fin d'une sauvegarde
	 * @param status
	 */
	@Override
	public void onFinSauvegarde(final Sauvegarde.STATUS status)
	{
		_nbSauvegardesEnCours--;
		if (status != Sauvegarde.STATUS.OK)
			_statusSauvegarde = status;

		if (_nbSauvegardesEnCours <= 0)
		{
			// Fin de toutes les sauvegardes
			_cbContacts.setEnabled(true);
			_cbAppels.setEnabled(true);
			_cbMessages.setEnabled(true);
			_cbMMS.setEnabled(true);
			_cbPhotos.setEnabled(true);
			_cbVideos.setEnabled(true);
			_fab.show();
			//_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.send_network));
			String message;
			switch (_statusSauvegarde)
			{
				case OK:
					message = getResources().getString(R.string.sauvegarde_ok);
					break;

				case FAILED:
					message = getResources().getString(R.string.sauvegarde_failed);
					break;
				case CANCELED:
					message = getResources().getString(R.string.sauvegarde_annulee);
					break;

				default:
					message = "";
			}

			SnackbarUtils.showIndeterminate(this, message);

			// Relance la recherche de serveurs
			lanceRechercheServeurs();
		}
	}

	@Override
	public void onSauvegardeProgress(InterfaceSauvegarde interf, final int progress, final int total)
	{
		interf.progressBar.setMax(total);
		interf.progressBar.setProgress(progress);
		interf.progressBar.invalidate();
		interf.tvStatus.setText(progress + "/" + total);
	}


	/***
	 * Bascule entre le mode Sauvegarde et le mode Restauration
	 * @param v
	 */
	public void onClickModeSauvegarde(View v)
	{
		_modeInterface = MODE_SAUVEGARDE.SAUVEGARDE;
		Preferences.getInstance(this).put(Preferences.PREF_MODE_UI, MODE_SAUVEGARDE.toInt(_modeInterface));
		updateModeUI();
	}
	/***
	 * Bascule entre le mode Sauvegarde et le mode Restauration
	 * @param v
	 */
	public void onClickModeRestauration(View v)
	{
		_modeInterface = MODE_SAUVEGARDE.RESTAURATION;
		Preferences.getInstance(this).put(Preferences.PREF_MODE_UI, MODE_SAUVEGARDE.toInt(_modeInterface));
		updateModeUI();
	}

	/***
	 * Met l'interface a jour en fonction du mode de l'application
	 */
	private void updateModeUI()
	{
		if (_modeInterface == MODE_SAUVEGARDE.SAUVEGARDE)
		{
			_modeSauvegarde.setChecked(true);
			_modeRestauration.setChecked(false);
		}
		else
		{
			_modeSauvegarde.setChecked(false);
			_modeRestauration.setChecked(true);
		}
	}
}
