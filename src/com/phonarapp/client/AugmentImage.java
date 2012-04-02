package com.phonarapp.client;

import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLFactory;
import gl.animations.AnimationFaceToCamera;
import gl.scenegraph.MeshComponent;

import java.util.ArrayList;

import system.DefaultARSetup;
import util.Vec;
import worldData.World;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import commands.Command;

/**
 * The actual UI class. Augments the contacts picture etc. onto the VR mode.
 */

public class AugmentImage extends Activity {
	private ArrayList<Person> people;
	Handler toastHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		toastHandler = new Handler();

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
				if (people.isEmpty()) { // no objects to add
					toastHandler.post(new DisplayToast("No people to phonar", getApplicationContext()));
					return; 
				}

				GeoObj[] o = new GeoObj[people.size()];   // image geo object
				Bitmap[] photo = new Bitmap[people.size()];
				Bitmap[] toShow = new Bitmap[people.size()];
				MeshComponent[] shape = new MeshComponent[people.size()];

				int i = 0;
				
				// add all people
				for (Person p:people) {					
					// this is ugly, but it's 2:30 am and the demo is tomorrow.
					// add person and white background
					o[i] = new GeoObj(p.getLatitude(), p.getLongitude(), p.getAltitude());
					photo[i] = Util.getPhotoByPhoneNumber(p.getPhoneNumber(), getContentResolver(), getResources());
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
					String name = p.getName();
					Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(p.getPhoneNumber()));
					Cursor cursor = getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
					if (cursor.moveToFirst()) {  
					    name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
					}
					name = name.split(" ")[0];
					paint.getTextBounds(name, 0, name.length(), bounds);
					int x = (temp.getWidth() - bounds.width())/2;
					int y = 300*(temp.getHeight() - bounds.height())/400;
					canvas.drawBitmap(temp, 0, 0, null);
					canvas.drawText(name, x*scale, y*scale, paint);
										
					// add to world
					shape[i] = objectFactory.newTexturedSquare("LOL"+i, toShow[i], 1.0F);
					shape[i].setScale(new Vec(20, 20, 20));
					shape[i].addAnimation(new AnimationFaceToCamera(camera, 0.5f));
					o[i].setComp(shape[i]);
					final String phone_final = p.getPhoneNumber();
					// make interactable (opens contact info on click)
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

					i++;
					
				}
			}

		});
	}
}