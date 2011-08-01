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
 * TODO: this is very bad code. The broadcastreceiver shouldn't actually be
 * processing this stuff so much: it should delegate it to a service or
 * something.
 */
public class C2DMReceiver extends BroadcastReceiver {
	private String mExternalNumber;
	private String mMyNumber;
	private LocationManager mLocationManager = null;

	// keys for items in the intent returned by Google servers
	private static final String GOOGLE_REGISTRATION_ID = "registration_id";
	private static final String GOOGLE_REGISTRATION_ERROR = "error";
	private static final String GOOGLE_REGISTRATION_UNREGISTERED
			= "unregistered";

	// key for which type of push message provided by server
	private static final String TYPE = "type";
	// the possible results from type
	private static final String TYPE_PROVIDING_LOCATION = "result";
	private static final String TYPE_REQUESTING_LOCATION = "request";

	// keys for items to send to and receive from server
	/** Number of the person that is Phonaring another*/
	public static final String KEY_ORIGINATOR = "number";
	/** The bro being phonar'd */
	public static final String KEY_TARGET = "target";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_ALTITUDE = "altitude";

	/**
	 * Receives location updates from the GPS. With each each message received,
	 * creates a new thread to send the info out. Currently only sends one
	 * and then immediately removes itself as a listener.
	 * TODO: thread
	 */
	private final LocationListener mLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
		    try {
		    	Log.d(PhonarApplication.TAG, "number: " + mExternalNumber);
		    	Log.d(PhonarApplication.TAG, "target: " + mMyNumber);
				String url = PhonarApplication.LOCATION_REPORT_URL
					+ KEY_ORIGINATOR + "=" + mExternalNumber + "&"
					+ KEY_TARGET + "=" + mMyNumber + "&"
					+ KEY_LONGITUDE + "=" + location.getLongitude() + "&"
					+ KEY_LATITUDE + "=" + location.getLatitude() + "&"
					+ KEY_ALTITUDE + "=" + location.getAltitude();
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
		mMyNumber = getNumber(context);
		if (intent.getAction().equals(
				"com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals(
				"com.google.android.c2dm.intent.RECEIVE")) {
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
			// use the info for something
			Log.d(PhonarApplication.TAG,
					intent.getStringExtra(KEY_LATITUDE));
		} else if (TYPE_REQUESTING_LOCATION.equals(type)){
			// Ask user if they want to share with this person and share if so.

			final String number = intent.getStringExtra(KEY_ORIGINATOR);
			Log.d(PhonarApplication.TAG,
					"extracted number in type_request_location: " + number);

			// use contacts to find the name of the person with the #

			mExternalNumber = number;
			mLocationManager = (LocationManager) context.getSystemService(
					Context.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 2000L, 0.01F, mLocationListener);
		} else {
			Log.e(PhonarApplication.TAG, "handleMessage has intent of "
					+ "unknown type.");
		}
	}

	/**
	 * Handles the registration of this app for this device. Should handle
	 * errors, unregistered, and new registrations. Be ready to re-register
	 * when Google refreshes the registration ID (happens periodically).
	 */
	private void handleRegistration(final Context context, Intent intent) {
		String registration =
				intent.getStringExtra(GOOGLE_REGISTRATION_ID);
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
						String url = PhonarApplication.REGISTRATION_URL
							+ KEY_ORIGINATOR + "=" + mMyNumber
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

	/** Returns this device's number as entered by the user */
	public static String getNumber(Context context) {
		return context.getSharedPreferences("default", Context.MODE_PRIVATE)
				.getString(Phonar.KEY_USER_NUMBER, null);
	}
}
