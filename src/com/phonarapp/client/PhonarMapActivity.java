package com.phonarapp.client;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class PhonarMapActivity extends MapActivity {
	LinearLayout linearLayout;
	MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable;
	PhonarItemizedMapOverlay itemizedOverlay;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		setContentView(R.layout.maplayout);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		addPeopleToMap();
	}
	
	private void addPeopleToMap() {
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		itemizedOverlay = new PhonarItemizedMapOverlay(drawable);
		
		//Person person = Phonar.getPeopleForDebugging().get("34");
		for (Person person : Phonar.getPeopleForDebugging().values()) {
			GeoPoint point = new GeoPoint((int)(person.getLatitude() * 1E6), (int)(person.getLongitude() * 1E6));
			OverlayItem overlayitem = new OverlayItem(point, person.getName(), person.getPhoneNumber());
		itemizedOverlay.addOverlay(overlayitem);
		}
		mapOverlays.add(itemizedOverlay);
		//mapView.invalidate();
	}
	
}
