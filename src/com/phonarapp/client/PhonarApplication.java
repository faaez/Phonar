package com.phonarapp.client;

import android.app.Application;

/**
 * This Application serves two purposes:
 *
 * (1) Allows functionality with Wikitude AR. Without an explicit name for
 * our application, the API would not be pleased.
 *
 * (2) Stores some global constants. If we isolate their usage to just one
 * primary activity, we should consider moving them there to minimize the
 * memory impact of the Application.
 * 
 * TODO: Now that wikitude has been phased out, we can safely move this stuff to 
 * a primary activity
 */
public class PhonarApplication extends Application {
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

	
}