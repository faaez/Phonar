package com.phonarapp.client;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.os.Bundle;

/**
 * This activity displays an augmented reality view, plotting locations
 * on the screen when viewing through the camera.
 */
public class ArActivity extends Activity {
	/** wikitude api key */
	private static final String API_KEY =
			"269fdec1-154d-4b7f-84f6-77ffc33abf00";
	/** wikitude info */
	private static final String DEVELOPER_NAME = "Faaez Ul Haq";
	/** the callback-intent after pressing any buttons */
    private static final String CALLBACK_INTENT =
    		"wikitudeapi.mycallbackactivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arview);
//		startArView();
	}

	/**
	 * Start running augmented reality view
	 */
//	private void startArView() {
//		WikitudeARIntent intent = prepareIntent();
//		try {
//			intent.startIntent(this);
//		} catch (ActivityNotFoundException e) {
//			AbstractWikitudeARIntent.handleWikitudeNotFound(this);
//		}
//	}

	/**
	 * Prepares wikitude AR intent.
	 */
//	private WikitudeARIntent prepareIntent() {
//		WikitudeARIntent intent = new WikitudeARIntent(this.getApplication(),
//				API_KEY, DEVELOPER_NAME);
//		addPois(intent);
//		return intent;
//	}

	/**
	 * Add POIs to intent.
	 */
//	private void addPois(WikitudeARIntent intent) {
//		List<WikitudePOI> pois = new ArrayList<WikitudePOI>();
//		for (Person person : Phonar.getPeopleForDebugging().values()) {
//			WikitudePOI poi = new WikitudePOI(person.getLatitude(), person.getLongitude(), person.getAltitude(), person.getName(), person.getPhoneNumber());
//			poi.setDetailAction(ArActivity.CALLBACK_INTENT);
//			pois.add(poi);
//		}
//		
//        intent.addPOIs(pois);
//        
//        ((PhonarApplication)this.getApplication()).setPois(pois);
//
//        //do something with application
//	}

}
