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
import android.widget.ImageView;
import android.widget.Toast;

public class ARTest extends Activity  {

	public Bitmap getPhoto(String phoneNumber) {
		Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Uri photoUri = null;
		ContentResolver cr = this.getContentResolver();
		Cursor contact = cr.query(phoneUri,
				new String[] { ContactsContract.Contacts._ID }, null, null, null);

		if (contact.moveToFirst()) {
			long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
			photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);

		}
		else {
			Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
			return defaultPhoto;
		}
		if (photoUri != null) {
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
			if (input != null) {
				return BitmapFactory.decodeStream(input);
			}
		} 
		else {
			Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
			return defaultPhoto;
		}
		Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
		return defaultPhoto;
	}

	/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String phoneNumber = "+19134858847";
		
		Bitmap photo = getFacebookPhoto(phoneNumber);
		ImageView iv = new ImageView(getApplicationContext());
		iv.setImageBitmap(photo);
		setContentView(iv);			
		
	}
	*/ 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		system.ArActivity.startWithSetup(com.phonarapp.client.ARTest.this, new DefaultARSetup() {

			@Override
			public void addObjectsTo(GL1Renderer renderer, World world,
					GLFactory objectFactory) {
				GeoObj o = new GeoObj(40.350207,-74.652733, 0);        // <-------- GPS location, altitude

				String phoneNumber = "6092162135"; // Rik's photo. Shield your eyes - could be ugly. 
				
				Bitmap photo = getPhoto(phoneNumber);
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageBitmap(photo);
				ARTest.this.setContentView(iv);
							
				//o.setComp(objectFactory.newArrow());			
				//MeshComponent shape = objectFactory.newTextured2dShape(photo, "LOL");
				MeshComponent shape = objectFactory.newTexturedSquare("LOL2", photo, 1.2F);
				//shape.setScale(new Vec(10, 10, 10));
				//shape.setPosition(new Vec(0, 0, 0));
				shape.setScale(new Vec(10, 10, 10));
				o.setComp(shape);
				world.add(o);
			}

		});
	}
	
}
