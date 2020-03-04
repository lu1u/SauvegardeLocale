package com.lpi.sauvegardelocale.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.lpi.sauvegardelocale.MainActivity;

public class SnackbarUtils
{
	private static Snackbar _zeSnackbar;

	public static void showIndeterminate(Activity a, String message)
	{
		if (_zeSnackbar != null)
			_zeSnackbar.dismiss();


		_zeSnackbar = Snackbar.make(a.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_INDEFINITE);
		_zeSnackbar.setAction("OK", new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Call your action method here
				if (_zeSnackbar != null)
					_zeSnackbar.dismiss();
				_zeSnackbar = null;
			}
		});
		_zeSnackbar.show();
	}

	public static void show(final MainActivity a, final String message)
	{
		if (_zeSnackbar != null)
			_zeSnackbar.dismiss();


		_zeSnackbar = Snackbar.make(a.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG);
		_zeSnackbar.show();
	}

	public interface SnackBarListener
	{
		public void onSnackbarAction();
	}
	public static void show(@NonNull final MainActivity a, @NonNull final String message, @NonNull final String action, @NonNull final SnackBarListener listener)
	{
		if (_zeSnackbar != null)
			_zeSnackbar.dismiss();


		_zeSnackbar = Snackbar.make(a.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_INDEFINITE);
		_zeSnackbar.setAction(action, new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Call your action method here
				if (_zeSnackbar != null)
					_zeSnackbar.dismiss();
				_zeSnackbar = null;
				listener.onSnackbarAction();
			}
		});
		_zeSnackbar.show();
	}
}
