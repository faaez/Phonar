package com.phonarapp.client;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

public class Phonar extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // C2DM
        Intent registrationIntent =
        		new Intent("com.google.android.c2dm.intent.REGISTER");
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(
        		this, 0, new Intent(), 0)); // boilerplate
        registrationIntent.putExtra("sender", "jeffreyhodes@gmail.com");
        startService(registrationIntent);
    }
}