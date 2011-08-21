package com.phonarapp.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Receives both registration information and messages from our server.
 * The real work is delegated to the MessageService so as to avoid generating
 * an ANR or unsafely spawning a new thread from a non-UI thread.
 */
public class C2DMReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		Intent serviceIntent = new Intent(context, MessageService.class);
		serviceIntent.putExtra(MessageService.KEY_EXTRAS, extras);
		serviceIntent.putExtra(MessageService.KEY_ACTION, intent.getAction());
		context.startService(serviceIntent);
	}
}
