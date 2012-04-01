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

//	private void addPeopleToMap() {
//		mapOverlays = mapView.getOverlays();
//		drawable = this.getResources().getDrawable(R.drawable.androidmarker);
//		itemizedOverlay = new PhonarItemizedMapOverlay(drawable, this.getBaseContext());
//
//		Bundle bundle = getIntent().getExtras();
//		double latitude = bundle.getDouble(LocationHandler.KEY_LATITUDE);
//		double longitude = bundle.getDouble(LocationHandler.KEY_LONGITUDE);
//		String target = bundle.getString(LocationHandler.KEY_TARGET);
//
//		GeoPoint point = new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
//		OverlayItem overlayItem = new OverlayItem(point, target, target);
//		itemizedOverlay.addOverlay(overlayItem);
//		mapOverlays.add(itemizedOverlay);
//		mapView.invalidate();
//	}
	
	private void addPeopleToMap() {
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		itemizedOverlay = new PhonarItemizedMapOverlay(drawable, this.getBaseContext());

		//Person person = Phonar.getPeopleForDebugging().get("34");
		for (Person person : ((PhonarApplication) getApplication()).getPeople()) {
			GeoPoint point = new GeoPoint((int)(person.getLatitude() * 1E6), (int)(person.getLongitude() * 1E6));
			OverlayItem overlayitem = new OverlayItem(point, person.getName(), person.getPhoneNumber());
		itemizedOverlay.addOverlay(overlayitem);
		}
		mapOverlays.add(itemizedOverlay);
		//mapView.invalidate();
	}

	

}
