package com.phonarapp.client;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PhonarItemizedMapOverlay extends ItemizedOverlay {

	private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
	public PhonarItemizedMapOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapview){
        if (event.getAction()==1){
        	Intent arIntent = new Intent(context, ArActivity.class);
			context.startActivity(arIntent);
        }
        return false;
    }

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	/**
	 * Add overlay to the list
	 */
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
}
