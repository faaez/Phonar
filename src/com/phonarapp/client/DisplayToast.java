package com.phonarapp.client;

import android.content.Context;
import android.widget.Toast;

/**
 * Runnable to display toast from non-UI thread
 * Use by declaring a new handler and then running this, for example:
 * 
 * Handler toastHandler = new Handler();
 * Context context = getApplicationContext(); 
 * //Context context = ClassName.this;  // Alternately
 * toastHandler.post(new DisplayToast("TEXTTODISPLAY", context); 
 */
public class DisplayToast implements Runnable { 
	String text;
	Context context;

	public DisplayToast(String text, Context context){
		this.text = text;
		this.context = context;
	}

	public void run(){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
