package com.phonarapp.client;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openintents.intents.WikitudePOI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Phonar extends Activity {
	//POIs
	private List<WikitudePOI> pois;

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

		Button pollButton = (Button) findViewById(R.id.poll_button);
		pollButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// request myself from the server
				new AsyncTask<Void, Void, Void>() {
					@Override
					public Void doInBackground(Void... v) {
						try {
							// TODO: this is magical. should be device number
							String url = "http://phonarapp.appspot.com/"
								+ "phonarserver?target=3474704757";
							HttpClient httpclient = new DefaultHttpClient();
						    httpclient.execute(new HttpGet(url));
						    Log.d(PhonarApplication.TAG, "done polling");
						} catch (Exception e) {
							Log.e(PhonarApplication.TAG, "Network exception: " + e);
						}
						return null;
					}
				}.execute();
			}
		});

		Button arButton = (Button) findViewById(R.id.ar_button);
        arButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent arIntent = new Intent(context, ArActivity.class);
                Phonar.this.startActivity(arIntent);
            }
        });
	}

	public void setPois(List<WikitudePOI> pois) {
		this.pois = pois;
	}

	public List<WikitudePOI> getPois() {
		return pois;

	}
}