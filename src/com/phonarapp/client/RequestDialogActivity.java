package com.phonarapp.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class RequestDialogActivity extends Activity {

    /** True for debugging; false to engage GPS */
	private boolean GIVE_FAKE_LOCATION = false;

	private static final int CONFIRM_GIVING = 1;

	private String requesterNumber;

	private final OnClickListener confirmationListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			Log.w("BOOM", "location about to be shared");

			LocationManager lm = (LocationManager) getApplicationContext().getSystemService(
					Context.LOCATION_SERVICE);

			LocationHandler handler = new LocationHandler(getApplicationContext(), requesterNumber);
			if (GIVE_FAKE_LOCATION) {
				Location loc = new Location("");
				loc.setLatitude(100);
				loc.setLongitude(200);
				loc.setAltitude(200);
				handler.onLocationChanged(loc);
			} else {
				lm.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 2000L, 0.01F,
						new LocationHandler(getApplicationContext(), requesterNumber));
			}
			RequestDialogActivity.this.finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requesterNumber = getIntent().getExtras().getString(LocationHandler.KEY_ORIGINATOR);
		showDialog(CONFIRM_GIVING);
	}	

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case CONFIRM_GIVING:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton("Yes", confirmationListener)
			.setNegativeButton("No", null)
			.setTitle("Share your location with " + requesterNumber + "?");
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;

	}
}
