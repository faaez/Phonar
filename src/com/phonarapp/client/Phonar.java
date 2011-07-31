package com.phonarapp.client;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Phonar extends Activity {
	/** debugging tag used throughout the application */
	public static final String TAG = "phonar";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// setup C2DM
		final Context context = this;
		Button registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent registrationIntent = new Intent(
						"com.google.android.c2dm.intent.REGISTER");
				registrationIntent.putExtra("app", PendingIntent
						.getBroadcast(context, 0, new Intent(), 0));
				registrationIntent.putExtra("sender",
						"jeffreyhodes@gmail.com");
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
	}
}