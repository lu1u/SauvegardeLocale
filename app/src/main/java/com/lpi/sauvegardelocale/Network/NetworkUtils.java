package com.lpi.sauvegardelocale.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils
{
	public static final String TAG = "NetworkUtils";

	/***
	 * Verifie si une adresse est joignable
	 * @param address
	 * @return
	 */
	public static boolean isReachable(@NonNull final InetAddress address)
	{
		try
		{
			if (address.isReachable(700))
			{
				Log.i(TAG, "INetAdress reachable");
				return true;
			}

		} catch (Exception e)
		{
			Log.e(TAG, "IsReachable " + e.getLocalizedMessage());
		}

		// Comme isReachable n'est pas tres fiable, tenter une communication
		return Ping(address.getHostAddress());
	}

	/***
	 * Utilise une commande Ping pour contacter un ordinateur sur le reseau
	 * @param url
	 * @return true si le ping a fonctionné
	 */
	public static boolean Ping(String url)
	{
		Log.i(TAG, "PING " + url);

		int count = 0;
		boolean reachable = true;
		try
		{
			Process process;
			//if (Build.VERSION.SDK_INT <= 16)
			{
				// shiny APIS
				process = Runtime.getRuntime().exec(
						"/system/bin/ping -w 1 -c 1 " + url);
			}
			//else
			//{
			//	process = new ProcessBuilder()
			//			.command("/system/bin/ping", url )
			//			.redirectErrorStream(true)
			//			.start();
			//}

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String temp;
			while ((count < 10) && (temp = reader.readLine()) != null)
			{
				Log.d(TAG, "PING: " + temp);
				count++;

				temp = temp.toLowerCase();
				if (temp.indexOf("100% packet loss") != -1 || temp.toLowerCase().indexOf("unreachable") != -1 || temp.indexOf("0 received") != -1)
				{
					reachable = false;
					break;
				}
			}

			reader.close();
			process.destroy();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		if (count == 0)
			reachable = false;

		return reachable;
	}

	/***
	 * Retourne le masque de réseau local, ou null
	 * @param context
	 * @return
	 */
	public static @Nullable
	String getActiveNetwork(@NonNull final Context context)
	{
		try
		{
			//ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			////NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			//WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
			//WifiInfo connectionInfo = wm.getConnectionInfo();
			//int ipAddress = connectionInfo.getIpAddress();
			//String ipString = Formatter.formatIpAddress(ipAddress);
			WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiinfo = manager.getConnectionInfo();
			byte[] myIPAddress = BigInteger.valueOf(wifiinfo.getIpAddress()).toByteArray();
			// you must reverse the byte array before conversion. Use Apache's commons library
			reverseArray(myIPAddress);
			InetAddress myInetIP = InetAddress.getByAddress(myIPAddress);
			String ipString= myInetIP.getHostAddress();
			return ipString;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static void reverseArray(@NonNull byte[] array)
	{
		int nb = array.length;
		for (int i = 0; i < (nb/2); i++)
		{
			byte t = array[i];
			array[i] =  array[nb-1-i];
			array[nb-1-i] = t;
		}
	}

	public interface HostnameFromAddress
	{
		public void onHostnameFound(@NonNull String hostname);
	}

	public static void chercheHostname(@NonNull Context context, @NonNull final String addresse, @NonNull final HostnameFromAddress listener )
	{
		// Demarrer une AsyncTask
		AsyncTask<String, String, String> t = new AsyncTask<String, String, String>()
		{
			@Override
			protected String doInBackground(final String... strings)
			{
				try
				{
					InetAddress address = InetAddress.getByName(addresse);
					return address.getCanonicalHostName() ;
				} catch (UnknownHostException e)
				{
					return null ;
				}
			}

			@Override
			protected void onPostExecute(final String s)
			{
				super.onPostExecute(s);
				if ( s!=null)
					listener.onHostnameFound(s);
			}
		};

		t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

}
