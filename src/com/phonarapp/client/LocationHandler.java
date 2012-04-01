package com.phonarapp.client;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Receives location updates from the GPS. With each each message received,
 * creates a new thread to send the info out. Currently only sends one
 * and then immediately removes itself as a listener.
 * TODO: thread
 */
class LocationHandler implements LocationListener {
	private final String mExternalNumber;
	private final String mMyNumber;
	private final Context mContext;

	// keys for items to send to and receive from server
	/** Number of the person that is Phonaring another */
	public static final String KEY_ORIGINATOR = "number";
	/** The bro being phonar'd */
	public static final String KEY_TARGET = "target";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_ALTITUDE = "altitude";

	public LocationHandler(Context context, String externalNumber) {
		mContext = context;
		mExternalNumber = externalNumber;
		mMyNumber = MessageService.getNumber(mContext);
	}

	public void onLocationChanged(Location location) {
		Log.d("phonar", "location changed");
		if (location.getAccuracy() < 30F) {
		    try { 
		    	Log.d(PhonarApplication.TAG, "number: " + mExternalNumber);
		    	Log.d(PhonarApplication.TAG, "target: " + mMyNumber);
				String url = PhonarApplication.LOCATION_REPORT_URL
					+ KEY_ORIGINATOR + "=" + mExternalNumber + "&"
					+ KEY_TARGET + "=" + mMyNumber + "&"
					+ KEY_LONGITUDE + "=" + location.getLongitude() + "&"
					+ KEY_LATITUDE + "=" + location.getLatitude() + "&"
					+ KEY_ALTITUDE + "=" + location.getAltitude();
				// TODO: thread
				new DefaultHttpClient().execute(new HttpGet(url));
				Log.d("phonar", "sent the location up");
			} catch (Exception e) {
				Log.e(PhonarApplication.TAG, "Network exception: " + e);
			}
		}
		((LocationManager) mContext.getSystemService(
				Context.LOCATION_SERVICE)).removeUpdates(this);
	}

	public void onLastKnownLocation() {
		LocationManager lm = (LocationManager) mContext.getSystemService(
				Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Log.d("phonar", "location changed");
	    try { 
	    	Log.d(PhonarApplication.TAG, "number: " + mExternalNumber);
	    	Log.d(PhonarApplication.TAG, "target: " + mMyNumber);
			String url = PhonarApplication.LOCATION_REPORT_URL
				+ KEY_ORIGINATOR + "=" + mExternalNumber + "&"
				+ KEY_TARGET + "=" + mMyNumber + "&"
				+ KEY_LONGITUDE + "=" + location.getLongitude() + "&"
				+ KEY_LATITUDE + "=" + location.getLatitude() + "&"
				+ KEY_ALTITUDE + "=" + location.getAltitude();
			// TODO: thread
			new DefaultHttpClient().execute(new HttpGet(url));
			Log.d("phonar", "sent the location up");
		} catch (Exception e) {
			Log.e(PhonarApplication.TAG, "Network exception: " + e);
		}
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

}
