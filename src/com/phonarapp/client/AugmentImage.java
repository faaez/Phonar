package com.phonarapp.client;

import geo.GeoObj;
import gl.GL1Renderer;
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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.ImageView;

import commands.Command;

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
				Bitmap[] toShow = new Bitmap[people.size()];
				MeshComponent[] shape = new MeshComponent[people.size()];
				MeshComponent[] bg = new MeshComponent[people.size()];

				int i = 0;
				// add all objects

				for (Person p:people) {
					/*
					o[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					photo[i] = getPhoto(p.getPhoneNumber());
					shape[i] = objectFactory.newTexturedSquare("LOL"+i, photo[i], 1.0F);
					shape[i].setScale(new Vec(10, 10, 10));
					shape[i].addAnimation(new AnimationFaceToCamera(camera, 0.5f));
					o[i].setComp(shape[i]);
					world.add(o[i]);
					
					// background
					o2[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					Bitmap bitmap = Bitmap.createBitmap(photo[i].getWidth(), photo[i].getHeight(), Bitmap.Config.RGB_565);
					bitmap.eraseColor(Color.WHITE);
					bitmap.eraseColor(gl.Color.white().toIntARGB());
					bg[i] = objectFactory.newTexturedSquare("Lolzies"+i, bitmap, 1.2F);
					bg[i].setScale(new Vec(10, 10, 10));
					bg[i].addAnimation(new AnimationFaceToCamera(camera, 0.5f));
					o2[i].setComp(bg[i]);
					world.add(o2[i]);
					*/
					
					// this is ugly, but it's 2:30 am and the demo is tomorrow.
					
					// add person
					o[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					photo[i] = getPhoto(p.getPhoneNumber());
					int new_height = (int)(1+(1.4*photo[i].getHeight()));
					int new_width = 2 + photo[i].getWidth();
					int[] pixels = new int[new_height*new_width];
					int j = 0, k = 0;
					for (j = 0; j < new_width; j++) { // top border
						pixels[j] = Color.WHITE; 
					}
					for (j = 0; j < new_height; j++) { // left and right borders
						pixels[j*new_width] = Color.WHITE;
						pixels[(j*new_width)+new_width-1] = Color.WHITE;
					}
					for (j = 0; j < photo[i].getHeight(); j++) { // image
						for (k = 0; k < photo[i].getWidth(); k++) { 
							pixels[((j+1)*new_width)+k+1] = photo[i].getPixel(k, j);
						}
					}
					for (j = photo[i].getHeight()+1; j < new_height; j++) { // bottom border
						for (k = 0; k < new_width; k++) {
							pixels[(j*new_width)+k] = Color.WHITE;
						}
					}
					Bitmap temp = Bitmap.createBitmap(pixels, new_width, new_height, Bitmap.Config.RGB_565);
					toShow[i] = Bitmap.createBitmap(new_width, new_height, Bitmap.Config.RGB_565);
					// add name to image
					Canvas canvas = new Canvas(toShow[i]);
					Paint paint = new Paint();
					paint.setColor(Color.BLACK);
					float scale = getApplicationContext().getResources().getDisplayMetrics().density;
					paint.setTextSize(14*scale);
					paint.setFlags(Paint.ANTI_ALIAS_FLAG);
					Rect bounds = new Rect();
					paint.getTextBounds(p.getName(), 0, p.getName().length(), bounds);
					int x = (temp.getWidth() - bounds.width())/2;
					int y = 300*(temp.getHeight() - bounds.height())/400;
					canvas.drawBitmap(temp, 0, 0, null);
					canvas.drawText(p.getName(), x*scale, y*scale, paint);
					
					
					// add to world
					shape[i] = objectFactory.newTexturedSquare("LOL"+i, toShow[i], 1.0F);
					shape[i].setScale(new Vec(20, 20, 20));
					shape[i].addAnimation(new AnimationFaceToCamera(camera, 0.5f));
					o[i].setComp(shape[i]);
					final String phone_final = p.getPhoneNumber();
					o[i].setOnClickCommand(new Command() {

						@Override
						public boolean execute() {
							Intent i = new Intent(); 
							i.setAction(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT); 
							i.setData(Uri.fromParts("tel", phone_final, null));
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							getApplicationContext().startActivity(i);
							return true;
						}
						
					});
					world.add(o[i]);
										
					/*
					o2[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					o2[i].setComp(objectFactory.newArrow());
					world.add(o2[i]);
					*/
					/*
					// text
					o3[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude()-10);
					Log.d("NAME", p.getName());
					Obj text = objectFactory.newTextObject("Hello", new Vec(0, 0, 0), getApplicationContext(), camera);
					text.setColor(gl.Color.blackTransparent());
					o3[i].setComp(text);
					world.add(o3[i]);
					*/
					i++;
					
				}
			}

		});
	}
}