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
				GeoObj o = new GeoObj(40.344215, -74.654521, 0);        // <-------- GPS location, altitude
				o.setComp(objectFactory.newCircle(Color.red()));
				world.add(o);
			}
			
		});
	}
	
}
