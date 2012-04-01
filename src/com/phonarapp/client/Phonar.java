package com.phonarapp.client;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.PeopleColumns;
import android.provider.Contacts.PhonesColumns;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Phonar extends Activity {

	private static final int CONTACT_PICKER_RESULT = 1001;  

	private final static String TAG = "PhonarMain";

	/** Prompted only on first run to allow user to input phone number */
	private EditText mUserNumberEditText;
	/** Temporary solution until we integrate with contacts */
	private EditText mTargetNumber;

	/** OnClickListener for dialog asking user to input their number */
	private final OnClickListener mSaveClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			// must be positive button; nothing else yet. also no error check
			setUserNumber(standardizePhoneNumber(
					mUserNumberEditText.getText().toString()));
			Intent registrationIntent = new Intent(
					"com.google.android.c2dm.intent.REGISTER");
					registrationIntent.putExtra("app", PendingIntent
							.getBroadcast(getApplicationContext(), 0, new Intent(), 0));
					registrationIntent.putExtra("sender",
					"jeffreyhodesphonarapp@gmail.com");
					startService(registrationIntent);
		}
	};

	/** OnClickListener for dialog asking user who they want to bronar */
	private final OnClickListener mBronarClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			startPhonarRequest(mTargetNumber.getText().toString());
		}
	};

	private void startPhonarRequest(String targetNumberString) {
		Log.d("location request:", "starting");
		final String url = PhonarApplication.LOCATION_REQUEST_URL
			+ LocationHandler.KEY_ORIGINATOR + "="
			+ MessageService.getNumber(Phonar.this)
			+ "&" + LocationHandler.KEY_TARGET + "="
			+ targetNumberString;
		Thread thread = new Thread(new Runnable(){
			  @Override
			  public void run(){
				  try { 
					  new DefaultHttpClient().execute(new HttpGet(url));
					  Log.d("phonar", "sent the location up");
				  } catch (Exception e) {
					  Log.e(PhonarApplication.TAG, "Network exception: " + e);
				  }
			  }
		});
		thread.run();
	}

	// Dialog codes
	private static final int DIALOG_ENTER_USER_NUMBER_ID = 0;
	private static final int DIALOG_ENTER_EXTERNAL_NUMBER_ID = 1;

	/** key to SharedPreferences for the number this device's number */
	public static final String KEY_USER_NUMBER = "userNumber";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Context context = this;

		Button registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent registrationIntent = new Intent(
				"com.google.android.c2dm.intent.REGISTER");
				registrationIntent.putExtra("app", PendingIntent
						.getBroadcast(context, 0, new Intent(), 0));
				registrationIntent.putExtra("sender",
				"jeffreyhodesphonarapp@gmail.com");
				startService(registrationIntent);
			}
		});

		Button unregisterButton = (Button) findViewById(R.id.unregister_button);
		unregisterButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent unregIntent =
					new Intent("com.google.android.c2dm.intent.UNREGISTER");
				unregIntent.putExtra("app", PendingIntent
						.getBroadcast(context, 0, new Intent(), 0));
				startService(unregIntent);
			}
		});

		Button phonarButton = (Button) findViewById(R.id.phonar_button);
		phonarButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_ENTER_EXTERNAL_NUMBER_ID);
			}
		});

		Button arButton = (Button) findViewById(R.id.ar_button);
		arButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent arIntent = new Intent(context, AugmentImage.class);
				Phonar.this.startActivity(arIntent);
			}
		});

		Button testArButton = (Button) findViewById(R.id.test_ar);
		testArButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent arIntent = new Intent(context, AugmentImage.class); //AugmentImage.class);
				Phonar.this.startActivity(arIntent);
			}
		});
		
		Button contactsButton = (Button) findViewById(R.id.contacts_button);
		contactsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doLaunchContactPicker();
			}
		});

		// ugly check. don't judge me. I'm tired :(
		if (getSharedPreferences("default", Context.MODE_PRIVATE)
				.getString(KEY_USER_NUMBER, null) == null) {
			showDialog(DIALOG_ENTER_USER_NUMBER_ID);
		}

		Button mapButton = (Button) findViewById(R.id.map_button);
		mapButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent mapIntent = new Intent(context, PhonarMapActivity.class);
				Phonar.this.startActivity(mapIntent);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_ENTER_USER_NUMBER_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			mUserNumberEditText = new EditText(this);
			mUserNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
			builder.setPositiveButton("SAVE!", mSaveClickListener)
			.setTitle("Enter your 10-digit phone number")
			.setView(mUserNumberEditText);
			dialog = builder.create();
			break;
		case DIALOG_ENTER_EXTERNAL_NUMBER_ID:
			AlertDialog.Builder externalBuilder = new AlertDialog.Builder(this);
			mTargetNumber = new EditText(this);
			mTargetNumber.setInputType(InputType.TYPE_CLASS_PHONE);
			externalBuilder.setPositiveButton("BRONAR!", mBronarClickListener)
			.setView(mTargetNumber);
			dialog = externalBuilder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * Given a 10-digit number (no hyphens, etc), sets the number of the owner
	 * of this app.
	 */
	public void setUserNumber(String number) {
		getSharedPreferences("default", Context.MODE_PRIVATE).edit()
		.putString(KEY_USER_NUMBER, number).commit();
	}

	public static ArrayList<Person> getPeopleForDebugging() {
		ArrayList<Person> people = new ArrayList<Person>();
		Person person = new Person("2095597960", "Hamza", 40.350191,-74.651252, 30);
		people.add(person);
		person = new Person("6097516257", "Faaez", 40.350191,-74.652615, 30);
		people.add(person);
		person = new Person("6096136481", "Zeerak", 40.349798,-74.653505, 30);
		people.add(person);
		return people;
	}

	public void doLaunchContactPicker() {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,  
	            Contacts.CONTENT_URI);  
	    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);  
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		String phoneNumber;
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (CONTACT_PICKER_RESULT) :
			if (resultCode == Activity.RESULT_OK) {
				Uri resultUri = data.getData();
				String id = resultUri.getLastPathSegment();  
				Cursor cursor = getContentResolver().query(  
				        Phone.CONTENT_URI, null,  
				        Phone.CONTACT_ID + "=?",  
				        new String[]{id}, null);  
				if (cursor.moveToFirst()) {  
					int phoneIdx = cursor.getColumnIndex(Phone.DATA);  
					phoneNumber = cursor.getString(phoneIdx);  
					Log.i(TAG, "PHONE:" + standardizePhoneNumber(phoneNumber));
					startPhonarRequest(standardizePhoneNumber(phoneNumber));
				}

			}
		break;
		}
	}

	public String standardizePhoneNumber(String number) {
		StringBuilder builder = new StringBuilder();
		for (Character c : number.toCharArray()) {
			if (Character.isDigit(c)) {
				builder.append(c);
			}
		}
		String result = builder.toString();
		if (result.length() != 10) {
			if (result.length() == 11 && result.charAt(0) == '1') {
				return result.substring(1);
			} else if (result.length() == 7) {
				return String.format("%s%s",
						MessageService.getNumber(this).substring(0, 3), result);
			} else {
				return "bad number";
			}
		} else {
			return result;
		}
	}

}
