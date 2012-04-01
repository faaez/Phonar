package com.phonarapp.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

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
	
	private NotificationManager notificationManager;
	private String number;
	private Context context;
	
	
	/** OnClickListener for comfirming location sharing */
	private final OnClickListener mShareLocationListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			LocationManager lm = (LocationManager) context.getSystemService(
					Context.LOCATION_SERVICE);
			LocationHandler lh = new LocationHandler(context, number);
			lh.onLastKnownLocation();
			lm.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 2000L, 0.01F,
					lh);
			lm.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2000L, 0.01F,
					lh);
		}
	};
	
	private final OnClickListener mEmptyListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
		}
	};

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
		String ns = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(ns);
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
		this.context = context;
		if (TYPE_PROVIDING_LOCATION.equals(type)) {
			double latitude = Double.parseDouble(extras.getString(LocationHandler.KEY_LATITUDE));
			double longitude = Double.parseDouble(extras.getString(LocationHandler.KEY_LONGITUDE));
			String target = extras.getString(LocationHandler.KEY_TARGET);
			
			((PhonarApplication)getApplication()).addPerson(latitude, longitude, target);
			
			int icon = R.drawable.notification23;
			CharSequence tickerText = target + " has accepted your request";
			long when = System.currentTimeMillis();
			
			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
			
			Context appContext = getApplicationContext();
			CharSequence contentTitle = target + " is sharing location";
			CharSequence contentText = "Touch here to view";
			Intent notificationIntent = new Intent(this, PhonarMapActivity.class);
			notificationIntent.putExtra(LocationHandler.KEY_LATITUDE, latitude);
			notificationIntent.putExtra(LocationHandler.KEY_LONGITUDE, longitude);

			notificationIntent.putExtra(LocationHandler.KEY_TARGET, target);

			
						
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

			notification.setLatestEventInfo(appContext, contentTitle, contentText, contentIntent);

			final int id = 1;

			notificationManager.notify(id, notification);
			
			
		} else if (TYPE_REQUESTING_LOCATION.equals(type)){
			// Ask user if they want to share with this person and share if so.
			
			number = extras.getString(
					LocationHandler.KEY_ORIGINATOR);
			
			/* Sends a notification. */
			//TODO: make notification trigger location sharing

			//HashMap for converting a number into a person's name
			ArrayList<Person> people = ((PhonarApplication)getApplication()).getPeople();
			String name = number;
			/*for (Person p : people) {
				if (p.getPhoneNumber().equals(number)) {
					name = p.getName();
					break;
				}
			}*/
			
			CharSequence tickerText;
			tickerText = name + " wants to Phonar you!";
			
			int icon = R.drawable.notification23;
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;  
			
			Context appContext = getApplicationContext();
			CharSequence contentTitle = name + " wants to Phonar you!";
			CharSequence contentText = "Accept by touching here";
			Intent notificationIntent = new Intent(this, RequestDialogActivity.class);
						
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			notificationIntent.putExtra(LocationHandler.KEY_ORIGINATOR, number);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

			notification.setLatestEventInfo(appContext, contentTitle, contentText, contentIntent);

			final int id = 1;

			notificationManager.notify(id, notification);

			try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setPositiveButton("Yes", mShareLocationListener)
			.setNegativeButton("No", mEmptyListener)
			.setTitle("Share your location with " + name + " ?");
			builder.create().show();
			} catch (BadTokenException e) {}
	
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
						HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
						Log.d("registration request status:", response.getStatusLine().toString());
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
