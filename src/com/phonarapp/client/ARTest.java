package com.phonarapp.client;

import android.app.Activity;
import android.os.Bundle;

import system.ArActivity;
import system.DefaultARSetup;
import worldData.World;
import geo.GeoObj;
import gl.Color;
import gl.GL1Renderer;
import gl.GLFactory;

public class ARTest extends Activity  {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArActivity.startWithSetup(ARTest.this, new DefaultARSetup() {
			
			@Override
			public void addObjectsTo(GL1Renderer renderer, World world,
					GLFactory objectFactory) {
				for (Person person : ((PhonarApplication)getApplication()).getPeople().values()) {
					GeoObj poi = new GeoObj(person.getLatitude(), person.getLongitude(), person.getAltitude());
					poi.setComp(objectFactory.newDiamond(Color.red()));
					world.add(poi);
				}
			}
		});
	}
	
}
