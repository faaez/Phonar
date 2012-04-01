package com.phonarapp.client;

import java.util.ArrayList;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

/**
 * This Application serves two purposes:
 *
 * (1) Allows functionality with Wikitude AR. Without an explicit name for
 * our application, the API would not be pleased.
 *
 * (2) Stores some global constants. If we isolate their usage to just one
 * primary activity, we should consider moving them there to minimize the
 * memory impact of the Application.
 */
public class PhonarApplication extends Application {
	/** POIs */
	private ArrayList<Person> people;
	
	/** debugging tag used throughout the application */
	public static final String TAG = "phonar";

	/** the URL for registering to our server */
	public static final String REGISTRATION_URL =
			"http://phonarapp.appspot.com/register?";
	/** the URL for requesting location */
	public static final String LOCATION_REQUEST_URL =
			"http://phonarapp.appspot.com/phonarserver?";
	/** the URL for providing location to whom requested it */
	public static final String LOCATION_REPORT_URL =
			"http://phonarapp.appspot.com/locationreport?";

	/** key for registrationId from Google that app must send to server */
	public static String REGISTRATION_ID_PARAM = "registrationId";

	public void addPerson(double latitude, double longitude, String phoneNumber) {
		if (this.people == null) this.people = new ArrayList<Person>();
		String name = "";
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		if (cursor.moveToFirst()) {  
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
		    name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}
		
		this.people.add(new Person(phoneNumber, name, latitude, longitude, 0.0));
		Log.d("adding new person:", phoneNumber);
		Log.d("lat:", Double.toString(latitude));
		Log.d("lng:", Double.toString(longitude));
	}

	public ArrayList<Person> getPeople() {
		return people;
	}
}