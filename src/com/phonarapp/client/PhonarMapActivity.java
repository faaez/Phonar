package com.phonarapp.client;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class PhonarMapActivity extends MapActivity {
	LinearLayout linearLayout;
	MapView mapView;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void onCreate(Bundle savedInstance) {
		setContentView(R.layout.maplayout);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
	}
	
}
