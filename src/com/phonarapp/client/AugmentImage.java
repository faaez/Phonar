package com.phonarapp.client;

import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.animations.AnimationFaceToCamera;
import gl.scenegraph.MeshComponent;

import java.io.InputStream;
import java.util.ArrayList;

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
	private ArrayList<Person> people;

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

		if (people == null) { 
			people = Phonar.getPeopleForDebugging();
		}
		else if (people.isEmpty()) {
			people = Phonar.getPeopleForDebugging();
		}

		system.ArActivity.startWithSetup(this, new DefaultARSetup() {

			@Override
			public void addObjectsTo(GL1Renderer renderer, World world,
					GLFactory objectFactory) {
				if (people.isEmpty()) return; // no objects to add

				GeoObj[] o = new GeoObj[people.size()];   // image geo object
				GeoObj[] o2 = new GeoObj[people.size()];  // background geo object
				GeoObj[] o3 = new GeoObj[people.size()];  // name geo object
				Bitmap[] photo = new Bitmap[people.size()];
				MeshComponent[] shape = new MeshComponent[people.size()];
				MeshComponent[] bg = new MeshComponent[people.size()];

				int i = 0;
				// add all objects

				for (Person p:people) {
					o[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					photo[i] = getPhoto(p.getPhoneNumber());
					shape[i] = objectFactory.newTexturedSquare("LOL"+i, photo[i], 1.0F);
					shape[i].setScale(new Vec(10, 10, 10));
					shape[i].addAnimation(new AnimationFaceToCamera(camera, 0.5f));
					o[i].setComp(shape[i]);
					world.add(o[i]);
					
					
					
					// background
					/*
					o2[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					Bitmap bitmap = Bitmap.createBitmap(photo[i].getWidth(), photo[i].getHeight(), Bitmap.Config.RGB_565);
					bitmap.eraseColor(Color.WHITE);
					bg[i] = objectFactory.newTexturedSquare("Lolzies"+i, bitmap, 1.2F);
					bg[i].setScale(new Vec(10, 10, 10));
					o2[i].setComp(bg[i]);
					world.add(o2[i]);
					*/
					
					o2[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					o2[i].setComp(objectFactory.newArrow());
					world.add(o2[i]);
					
					// text
					GLCamera glcam = new GLCamera();
					o3[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					o3[i].setComp(objectFactory.newTextObject(p.getName(), new Vec(0, 0, 0), getApplicationContext(), glcam));
					world.add(o3[i]);
					
					objectFactory.newSolarSystem(new Vec(0, 1, 0));
					
					i++;
					
				}
			}

		});
	}
}