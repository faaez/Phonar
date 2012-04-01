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
import java.util.HashMap;

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
	private HashMap<String, Person> people;

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
		
		this.people = ((PhonarApplication)getApplication()).getPeople();
		
		if (people.isEmpty()) {
			people = Phonar.getPeopleForDebugging();
		}

		system.ArActivity.startWithSetup(this, new DefaultARSetup() {

			@Override
			public void addObjectsTo(GL1Renderer renderer, World world,
					GLFactory objectFactory) {
				if (people.isEmpty()) return; // no objects to add
				
				GeoObj[] o = new GeoObj[people.size()];
				Bitmap[] photo = new Bitmap[people.size()];
				MeshComponent[] shape = new MeshComponent[people.size()];
				
				int i = 0;
				// add all objects
				for (Person p:people.values()) {
					o[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					photo[i] = getPhoto(p.getPhoneNumber());
					shape[i] = objectFactory.newTexturedSquare("LOL"+i, photo[i], 1.0F);
					shape[i].setScale(new Vec(10, 10, 10));
					o[i].setComp(shape[i]);
					world.add(o[i]);
					i++;
				}
			}

		});
	}
}