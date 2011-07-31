package com.phonarapp.client;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Receives both registration information and messages from our server.
 */
public class C2DMReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(PhonarApplication.TAG, "receiving something...");
		if (intent.getAction().equals(
				"com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals(
				"com.google.android.c2dm.intent.RECEIVE")) {
			Log.d(PhonarApplication.TAG, "about to handle message that came");
			handleMessage(context, intent);
		}
	}

	/**
	 * Handles messages received from our server, stored as extras in the
	 * intent. (The names of these extras should be defined in the common
	 * library.)
	 */
	private void handleMessage(Context context, Intent intent) {
		String message = intent.getStringExtra("lol");
		if (message != null) {
			(Toast.makeText(context, message, Toast.LENGTH_LONG)).show();
		} else {
			Log.e(PhonarApplication.TAG, "Message came back null. Did we give"
					+ "it the right name?");
		}
	}

	/**
	 * Handles the registration of this app for this device. Should handle
	 * errors, unregistered, and new registrations. Be ready to re-register
	 * when Google refreshes the registration ID (happens periodically).
	 */
	private void handleRegistration(Context context, Intent intent) {
		String registration = intent.getStringExtra("registration_id");
		if (intent.getStringExtra("error") != null) {
			// Registration failed, should try again later.
		} else if (intent.getStringExtra("unregistered") != null) {
			// unregistered; new messages will be rejected
		} else if (registration != null) {
			// When done, remember that all registration is done.
			new AsyncTask<String, Void, Void>() {
				@Override
				protected Void doInBackground(String... registration) {
					try {
						// TODO: this is magical. should be device number
						String baseUrl = "http://phonarapp.appspot.com/register"
							+ "?number=3474704757&registrationId=";
						String url = baseUrl + registration[0];
						HttpClient httpclient = new DefaultHttpClient();
					    httpclient.execute(new HttpGet(url));
					} catch (Exception e) {
						Log.e(PhonarApplication.TAG, "Network exception: " + e);
					}
					return null;
				}
			}.execute(registration);
		}
	}
}
