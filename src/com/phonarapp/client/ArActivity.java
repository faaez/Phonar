package com.phonarapp.client;

import java.util.ArrayList;
import java.util.List;

import org.openintents.intents.AbstractWikitudeARIntent;
import org.openintents.intents.WikitudeARIntent;
import org.openintents.intents.WikitudePOI;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
		startArView();
	}

	/**
	 * Start running augmented reality view
	 */
	private void startArView() {
		WikitudeARIntent intent = prepareIntent();
		try {
			intent.startIntent(this);
		} catch (ActivityNotFoundException e) {
			AbstractWikitudeARIntent.handleWikitudeNotFound(this);
		}
	}

	/**
	 * Prepares wikitude AR intent.
	 */
	private WikitudeARIntent prepareIntent() {
		WikitudeARIntent intent = new WikitudeARIntent(this.getApplication(),
				API_KEY, DEVELOPER_NAME);
		addPois(intent);
		return intent;
	}

	/**
	 * Add POIs to intent.
	 */
	private void addPois(WikitudeARIntent intent) {
		WikitudePOI poi1 = new WikitudePOI(35.683333, -139.766667, 36, "Clem Wright", "");
		poi1.setDetailAction(ArActivity.CALLBACK_INTENT);
		WikitudePOI poi2 = new WikitudePOI(37.7793, -122.4192, 14, "Jorge Lugo",
                "");
		poi2.setDetailAction(ArActivity.CALLBACK_INTENT);
        WikitudePOI poi3 = new WikitudePOI(38, -123, 1, "Jeff Hodes",
                "");
        poi3.setDetailAction(ArActivity.CALLBACK_INTENT);
        WikitudePOI poi4 = new WikitudePOI(36, -125, 220, "Faaez Ul Haq",
                "");
        poi4.setDetailAction(ArActivity.CALLBACK_INTENT);
        List<WikitudePOI> pois = new ArrayList<WikitudePOI>();

        pois.add(poi1);
        pois.add(poi2);
        pois.add(poi3);
        pois.add(poi4);
        intent.addPOIs(pois);
        
        ((PhonarApplication)this.getApplication()).setPois(pois);

        //do something with application
	}

}
