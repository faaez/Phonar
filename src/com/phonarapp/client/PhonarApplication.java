package com.phonarapp.client;

import java.util.List;

import org.openintents.intents.WikitudePOI;

import android.app.Application;

public class PhonarApplication extends Application {
	private List<WikitudePOI> pois;
	/** debugging tag used throughout the application */
	public static final String TAG = "phonar";
	
	public void setPois(List<WikitudePOI> pois) {
		this.pois = pois;
	}

	public List<WikitudePOI> getPois() {
		return pois;
	}
}