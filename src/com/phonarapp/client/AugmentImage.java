/*
 * TO DO: 
 * 1. Add marker (Google Maps arrow)
 * 2. Add name
 */

package com.phonarapp.client;

import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;

import java.io.InputStream;

import system.DefaultARSetup;
import util.Vec;
import worldData.World;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.Toast;

public class AugmentImage extends Activity {
	private final double default_latitude[] = {40.350265, 40.350583};
	private final double default_longitude[] = {-74.652733, -74.651392};
	private final double default_altitude[] = {0, 0};
	private final String default_phoneNumber[] = {"9134858847", "6092162135"}; // Rik's phone number. Tell the ladies.
	private double latitude[];
	private double longitude[];
	private double altitude[];
	private String phoneNumber[];
	

	public Bitmap getPhoto(String phoneNumber) {
		Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
		if (phoneNumber == null) return defaultPhoto;
		
		Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Uri photoUri = null;
		ContentResolver cr = this.getContentResolver();
		Cursor contact = cr.query(phoneUri,
				new String[] { ContactsContract.Contacts._ID }, null, null, null);

		if (contact.moveToFirst()) {
			long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
			photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);

		}
		else return defaultPhoto;
		if (photoUri != null) {
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
			if (input != null) {
				return BitmapFactory.decodeStream(input);
			}
		} 
		else return defaultPhoto;
		return defaultPhoto;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Toast.makeText(getApplicationContext(), "NEW", Toast.LENGTH_SHORT).show();
		
		if (getIntent().getExtras() != null) {
			latitude = getIntent().getExtras().getDoubleArray("latitude");
			longitude = getIntent().getExtras().getDoubleArray("longitude");
			altitude = getIntent().getExtras().getDoubleArray("altitude");
			phoneNumber = getIntent().getExtras().getStringArray("phoneNumber");
		}
		else {
			latitude = default_latitude;
			longitude = default_longitude;
			altitude = default_altitude;
			phoneNumber = default_phoneNumber;
		}

		system.ArActivity.startWithSetup(this, new DefaultARSetup() {

			@Override
			public void addObjectsTo(GL1Renderer renderer, World world,
					GLFactory objectFactory) {
				if (latitude == null) return; // no objects to add
				
				GeoObj[] o = new GeoObj[latitude.length];
				Bitmap[] photo = new Bitmap[latitude.length];
				MeshComponent[] shape = new MeshComponent[latitude.length];
				
				// add all objects
				for (int i = 0; i < latitude.length; i++) {
					if (longitude.length < i || altitude.length < i || phoneNumber.length < i) break;
					
					o[i] = new GeoObj(latitude[i], longitude[i], altitude[i]);
					photo[i] = getPhoto(phoneNumber[i]);
					shape[i] = objectFactory.newTexturedSquare("LOL"+i, photo[i], 1.0F);
					shape[i].setScale(new Vec(10, 10, 10));
					o[i].setComp(shape[i]);
					world.add(o[i]);
				}
			}

		});
	}
}