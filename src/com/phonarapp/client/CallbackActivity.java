/**
 * REMOVE THIS?
 */

//package com.phonarapp.client;
//
//import java.util.List;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.util.Pair;
//
///**
// * basic callback-activity
// * 
// * @author Martin Lechner, Mobilizy GmbH., martin.lechner@mobilizy.com
// * 
// */
//public class CallbackActivity extends Activity {
//
//	private static final int POI_CLICKED_DIALOG = 1;
//	private static final int NOTHING_SELECTED_DIALOG = 2;
//	private int poiId;
//	private List<Pair<Double, Double>> pois;
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		this.setContentView(R.layout.callback_layout);
//		pois = ((PhonarApplication) this.getApplication()).getPois();
////		poiId = this.getIntent().getIntExtra(
////				WikitudeARIntentHelper.EXTRA_INDEX_SELECTED_POI, -1);
//
//		if (pois != null && poiId != -1) {
//			this.showDialog(CallbackActivity.POI_CLICKED_DIALOG);
//		} else {
//			this.showDialog(CallbackActivity.NOTHING_SELECTED_DIALOG);
//		}
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//		case CallbackActivity.POI_CLICKED_DIALOG:
//			String title = "";
//			if (poiId != -1 && pois != null) {
//				//title = pois.get(poiId);
//			}
//			return new AlertDialog.Builder(this).setMessage("My new Intent!")
//					.setTitle("Coming from Wikitude, " + title + " clicked")
//					.setPositiveButton("OK",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//								}
//							}).create();
//		case CallbackActivity.NOTHING_SELECTED_DIALOG:
//			return new AlertDialog.Builder(this).setMessage("My new Intent!")
//					.setTitle("Coming from Wikitude, nothing is selected.")
//					.setPositiveButton("OK",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//								}
//							}).create();
//		}
//		return null;
//	}
//
//}
