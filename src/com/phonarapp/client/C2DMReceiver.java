package com.phonarapp.client;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Receives both registration information and messages from our server.
 */
public class C2DMReceiver extends BroadcastReceiver {
	// keys for items in the intent returned by Google servers
	private static final String GOOGLE_REGISTRATION_ID_KEY = "registration_id";
	private static final String GOOGLE_REGISTRATION_ERROR = "error";
	private static final String GOOGLE_REGISTRATION_UNREGISTERED
			= "unregistered";

	// key for which type of push message provided by server
	private static final String TYPE = "type";
	// the possible results from type
	private static final String TYPE_PROVIDING_LOCATION = "result";
	private static final String TYPE_REQUESTING_LOCATION = "request";

	// keys for items in intent of TYPE_REQUESTING_LOCATION
	private static final String STALKER_NUMBER = "number";

	// keys for items in intent of TYPE_PROVIDING_LOCATION
	private static final String TARGET_NUMBER = "target";
	private static final String STALKEE_NUMBER = "number";
	private static final String STALKEE_LONGITUDE = "longitude";
	private static final String STALKEE_LATITUDE = "latitude";
	private static final String STALKEE_ALTITUDE = "altitude";

	private String mExternalNumber;
	private String mMyNumber;
	private LocationManager mLocationManager = null;

	private final LocationListener mLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
		    Log.d(PhonarApplication.TAG, "latitude: " + location.getLatitude());
		    // TODO: might want to do this in another thread
		    try {
		    	Log.d(PhonarApplication.TAG, "number: " + mExternalNumber);
		    	Log.d(PhonarApplication.TAG, "target: " + mMyNumber);
				String url = PhonarApplication.LOCATION_REPORT_URL
					+ STALKEE_NUMBER + "=" + mExternalNumber + "&"
					+ TARGET_NUMBER + "=" + mMyNumber + "&"
					+ STALKEE_LONGITUDE + "=" + location.getLongitude() + "&"
					+ STALKEE_LATITUDE + "=" + location.getLatitude() + "&"
					+ STALKEE_ALTITUDE + "=" + location.getAltitude();
				new DefaultHttpClient().execute(new HttpGet(url));
			} catch (Exception e) {
				Log.e(PhonarApplication.TAG, "Network exception: " + e);
			}
		    mLocationManager.removeUpdates(this);
		}

		// TODO: these
		public void onProviderDisabled(String provider) {
			Log.d(PhonarApplication.TAG, "location provider disabled");
		}
		public void onProviderEnabled(String provider) {
			Log.d(PhonarApplication.TAG, "location provider enabled");
		}
		public void onStatusChanged(String provider,
				int status, Bundle extras) {
			Log.d(PhonarApplication.TAG, "onstatuschanged");
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(PhonarApplication.TAG, "receiving something...");
		mMyNumber = getNumber(context);
		if (intent.getAction().equals(
				"com.google.android.c2dm.intent.REGISTRATION")) {
			Log.d(PhonarApplication.TAG, "registration");
			handleRegistration(context, intent);
		} else if (intent.getAction().equals(
				"com.google.android.c2dm.intent.RECEIVE")) {
			Log.d(PhonarApplication.TAG, "about to handle message that came");
			handleMessage(context, intent);
		}
		Log.d(PhonarApplication.TAG, "nothing...");
	}

	/**
	 * Handles messages received from our server, stored as extras in the
	 * intent. (The names of these extras should be defined in the common
	 * library.)
	 */
	private void handleMessage(final Context context, Intent intent) {
		String type = intent.getStringExtra(TYPE);
		if (TYPE_PROVIDING_LOCATION.equals(type)) {
			// blah
			Log.d(PhonarApplication.TAG,
					intent.getStringExtra(STALKEE_LATITUDE));
		} else if (TYPE_REQUESTING_LOCATION.equals(type)){
			// Ask user if they want to share with this person and share if so.

			final String number = intent.getStringExtra(STALKEE_NUMBER);
			Log.d(PhonarApplication.TAG,
					"extracted number in type_request_location: " + number);

			// use contacts to find the name of the person with the #

			mExternalNumber = number;
			mLocationManager = (LocationManager) context.getSystemService(
					Context.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 2000L, 0.01F, mLocationListener);
		} else {
			Log.e(PhonarApplication.TAG, "Message came back null. Did we give"
					+ "it the right name?");
		}
	}

	public static String getNumber(Context context) {
		return context.getSharedPreferences("default", Context.MODE_PRIVATE)
				.getString(Phonar.KEY_USER_NUMBER, null);
	}

	/**
	 * Handles the registration of this app for this device. Should handle
	 * errors, unregistered, and new registrations. Be ready to re-register
	 * when Google refreshes the registration ID (happens periodically).
	 */
	private void handleRegistration(final Context context, Intent intent) {
		String registration =
				intent.getStringExtra(GOOGLE_REGISTRATION_ID_KEY);
		if (intent.getStringExtra(GOOGLE_REGISTRATION_ERROR) != null) {
			// Registration failed, should try again later.
		} else if (intent.getStringExtra(GOOGLE_REGISTRATION_UNREGISTERED)
				!= null) {
			// unregistered; new messages will be rejected
		} else if (registration != null) {
			// When done, remember that all registration is done.
			new AsyncTask<String, Void, Void>() {
				@Override
				protected Void doInBackground(String... registration) {
					try {
						// TODO: this is magical. should be device number
						String url = PhonarApplication.REGISTRATION_URL
							+ PhonarApplication.MY_NUMBER_PARAM + "="
							+ mMyNumber
							+ "&" + PhonarApplication.REGISTRATION_ID_PARAM
							+ "=" + registration[0];
						new DefaultHttpClient().execute(new HttpGet(url));
					} catch (Exception e) {
						Log.e(PhonarApplication.TAG, "Network exception: " + e);
					}
					return null;
				}
			}.execute(registration);
		}
	}
}
