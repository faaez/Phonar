package com.phonarapp.client;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * MessageService is an IntentService that handles communicating messages
 * with the server safely, efficiently, and asynchronously. When we need to
 * communicate with the server, it is best to do so here. This is particularly
 * important for messages that are handled and responded to in the
 * C2DMReceiver since it is unsafe to spawn a thread or take too long to
 * handle a message in there.
 */
public class MessageService extends Service {
	/** The action intent, which may be ACTION_REGISTER or ACTION_MESSAGE. */
	public static final String KEY_ACTION = "action";
	/** Action that signifies we are registering the device. */
	public static final String ACTION_REGISTER =
		"com.google.android.c2dm.intent.REGISTRATION";
	/**
	 * Action that signifies we are handling a message, such as a request
	 * to share location or receiving location information.
	 */
	public static final String ACTION_RECEIVE =
		"com.google.android.c2dm.intent.RECEIVE";

	/** key for the bundle of extras in the intent passed to MessageService */
	public static final String KEY_EXTRAS = "extras";

	// keys for items in the intent returned by Google servers
	private static final String GOOGLE_REGISTRATION_ID = "registration_id";
	private static final String GOOGLE_REGISTRATION_ERROR = "error";
	private static final String GOOGLE_REGISTRATION_UNREGISTERED
			= "unregistered";

	/** Key for which type of push message provided by server. */
	public static final String TYPE = "type";
	// the possible results from type
	private static final String TYPE_PROVIDING_LOCATION = "result";
	private static final String TYPE_REQUESTING_LOCATION = "request";
	
	/**
	 * Handles the intent passed to startService() (see
	 * Service.onStartCommand() for more details).
	 *
	 * @param intent This intent should contain a TYPE identifying what
	 * needs to be done. If a valid value is not provided, it logs the error
	 * and returns immediately. Arguments should be provided in a bundle
	 * added with KEY_EXTRAS_BUNDLE.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// If the process is killed and restarted, the intent may be null.
		if (intent != null ) {
			Context context = getBaseContext();
			if (intent.getStringExtra(KEY_ACTION).equals(ACTION_REGISTER)) {
				handleRegistration(context, intent.getBundleExtra(KEY_EXTRAS));
			} else if (intent.getStringExtra("action").equals(ACTION_RECEIVE)) {
				handleMessage(context, intent.getBundleExtra(KEY_EXTRAS));
			}
		}
		return START_STICKY;
	}

	/**
	 * Handles messages received from our server, stored as extras in the
	 * intent. (The names of these extras should be defined in the common
	 * library.)
	 */
	private void handleMessage(final Context context, Bundle extras) {
		String type = extras.getString(TYPE);
		if (TYPE_PROVIDING_LOCATION.equals(type)) {
			// use the info for something
			Log.d(PhonarApplication.TAG,
					extras.getString(LocationHandler.KEY_LATITUDE));
		} else if (TYPE_REQUESTING_LOCATION.equals(type)){
			// Ask user if they want to share with this person and share if so.
			
			final String number = extras.getString(
					LocationHandler.KEY_ORIGINATOR);
			
			LocationManager lm = (LocationManager) context.getSystemService(
					Context.LOCATION_SERVICE);
			lm.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 2000L, 0.01F,
					new LocationHandler(context, number));
			
			/* Sends a notification. */
			//TODO: make notification trigger location sharing
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager 
				= (NotificationManager) getSystemService(ns);
			
			int icon = R.drawable.phonar_app_icon_19;
			CharSequence tickerText = number + " wants to Phonar you!";
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, tickerText, when);
			
			Context appContext = getApplicationContext();
			CharSequence contentTitle = number + " wants to Phonar you!";
			CharSequence contentText = "Accept by touching here";
			Intent notificationIntent = new Intent(this, Phonar.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			notificationIntent.putExtra(LocationHandler.KEY_ORIGINATOR, number);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

			notification.setLatestEventInfo(appContext, contentTitle, contentText, contentIntent);

			final int id = 1;

			mNotificationManager.notify(id, notification);
			
			Log.d(PhonarApplication.TAG,
					"extracted number in type_request_location: " + number);
		} else {
			Log.e(PhonarApplication.TAG, "handleMessage was passed an intent"
					+ " of unknown type.");
		}
	}

	/**
	 * Handles the registration of this app for this device. Should handle
	 * errors, unregistered, and new registrations. Be ready to re-register
	 * when Google refreshes the registration ID (happens periodically).
	 */
	private void handleRegistration(final Context context, Bundle extras) {
		String registration =
				extras.getString(GOOGLE_REGISTRATION_ID);
		if (extras.getString(GOOGLE_REGISTRATION_ERROR) != null) {
			// Registration failed -- should try again later.
			// TODO: tell use to try again later.
		} else if (extras.getString(GOOGLE_REGISTRATION_UNREGISTERED)
				!= null) {
			// Unregistered; new messages will now be rejected.
		} else if (registration != null) {
			new AsyncTask<String, Void, Void>() {
				@Override
				protected Void doInBackground(String... registration) {
					try {
						String url = PhonarApplication.REGISTRATION_URL
							+ LocationHandler.KEY_ORIGINATOR
							+ "=" + getNumber(context)
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

	@Override
	public IBinder onBind(Intent intent) {
		// We don't allow binding to this service.
		return null;
	}
}
