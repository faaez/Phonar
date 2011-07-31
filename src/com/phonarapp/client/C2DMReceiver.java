package com.phonarapp.client;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class C2DMReceiver extends BroadcastReceiver {
	private static final String TAG = "phonar";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				"com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals(
				"com.google.android.c2dm.intent.RECEIVE")) {
			// handleMessage(context, intent);
			Log.d(TAG, ":(");
		}
	}

	private void handleRegistration(Context context, Intent intent) {
		String registration = intent.getStringExtra("registration_id");
		if (intent.getStringExtra("error") != null) {
			// Registration failed, should try again later.
		} else if (intent.getStringExtra("unregistered") != null) {
			// unregistered; new messages will be rejected
		} else if (registration != null) {
			// Send registration ID to 3rd party site
			// This should be done in a separate thread.
			// When done, remember that all registration is done.
			Log.d(TAG, "about to upload -- it came in: " + registration);
			new AsyncTask<String, Void, Void>() {
				@Override
				protected Void doInBackground(String... registration) {
					try {
						String baseUrl = "http://phonarapp.appspot.com/register"
							+ "?number=3474704757&registrationId=";
						String url = baseUrl + registration[0];
						HttpClient httpclient = new DefaultHttpClient();
						httpclient.execute(new HttpGet(url));
						Log.d(TAG, "done");
					} catch (Exception e) {
						Log.e(TAG, "Network exception: " + e);
					}
					return null;
				}
			}.execute(registration);
		}
	}
}
