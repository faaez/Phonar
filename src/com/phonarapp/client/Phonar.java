package com.phonarapp.client;

import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
	private HashMap<String, Person> people;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Context context = this;
		
		this.people = getPeopleForDebugging();

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
								+ "phonarserver?target=3474704757"
								+ "&number=3474704757";
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
        
        Button mapButton = (Button) findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mapIntent = new Intent(context, PhonarMapActivity.class);
                Phonar.this.startActivity(mapIntent);
            }
        });
	}
	
	public static HashMap<String, Person> getPeopleForDebugging() {
		HashMap<String, Person> people = new HashMap<String, Person>();
		Person person = new Person("12", "Clem", 37.7793, -122.4192, 36);
		people.put("12", person);
		person = new Person("34", "Jorge", 36.683333, -128.766667, 36);
		people.put("34", person);
		person = new Person("56", "Jeff", 37.683333, -137.766667, 36);
		people.put("56", person);
		person = new Person("78", "Faaez", 38.683333, -146.766667, 36);
		people.put("78", person);
		
		return people;
	}

	public void setPeople(HashMap<String, Person> people) {
		this.people = people;
	}

	public HashMap<String, Person> getPeople() {
		return people;
	}
}