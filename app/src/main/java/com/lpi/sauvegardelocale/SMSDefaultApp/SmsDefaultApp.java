package com.lpi.sauvegardelocale.SMSDefaultApp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.support.annotation.NonNull;

import com.lpi.sauvegardelocale.MainActivity;

public class SmsDefaultApp
{
	static private String _defaultApp;
	static private SmsReceiver _smsReceiver;
	static private MmsReceiver _mmsReceiver;
	static private IntentFilter _intentFilterSMS;
	static private IntentFilter _intentFilterMMS;

	public static boolean isDefaultApp(@NonNull final Context context)
	{
		final String myPackageName = context.getPackageName();
		return Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName);
	}


	/***
	 * Affiche le dialogue systeme permettant de modifier l'application par defaut pour gerer
	 * les sms
	 * @param context
	 * @param thisApp : true si on essaie de mettre notre application
	 */
	public static void setDefaultSMSApp(@NonNull final Context context, boolean thisApp)
	{
		if (! isDefaultApp(context))
			_defaultApp = Telephony.Sms.getDefaultSmsPackage(context);

		String myPackageName ;
		if ( thisApp )
			myPackageName = context.getPackageName();
		else
			myPackageName = _defaultApp;

		Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
		intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
		context.startActivity(intent);
	}

	/***
	 * Initialisation des fonctionnalites pour que notre application puisse etre consideree comme
	 * une application de gestion de SMS
	 * @param context
	 */
	public static void init(final Context context)
	{
		// Broadcast receiver SMS
		_smsReceiver = new SmsReceiver();
		_intentFilterSMS = new IntentFilter();

		context.registerReceiver(_smsReceiver, _intentFilterSMS);
	}
}

