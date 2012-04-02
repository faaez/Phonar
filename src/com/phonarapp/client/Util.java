package com.phonarapp.client;

import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

/** utility functions */
public class Util {
	/**
	 * Get contact name from a phone number
	 * @param phoneNumber phone number of contact whose name is required
	 * @param resolver content resolver (use getContentResolver())
	 * @return the contact name or the phone number if no name found
	 */
	public static String getNameByPhoneNumber(String phoneNumber, ContentResolver resolver) {
		String name = "";
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = resolver.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		if (cursor.moveToFirst()) {  
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}
		if (name == null) return phoneNumber;
		return name;
	}

	/**
	 * Standardize a phone number 
	 * @param number phone number to standardize
	 * @param context context of application (use getApplicationContext() or ClassName.this)
	 * @return the phone number in standard form
	 */
	 
	public static String standardizePhoneNumber(String number, Context context) {
		StringBuilder builder = new StringBuilder();
		for (Character c : number.toCharArray()) {
			if (Character.isDigit(c)) {
				builder.append(c);
			}
		}
		String result = builder.toString();
		if (result.length() != 10) {
			if (result.length() == 11 && result.charAt(0) == '1') {
				return result.substring(1);
			} else if (result.length() == 7) {
				return String.format("%s%s",
						getNumber(context).substring(0, 3), result);
			} else {
				return "bad number";
			}
		} else {
			return result;
		}
	}

	/**
	 * Returns this device's number as entered by the user
	 * @param context: context of application (use getApplicationContext() or ClassName.this)
	 * @return phone number entered by user
	 */
	public static String getNumber(Context context) {
		return context.getSharedPreferences("default", Context.MODE_PRIVATE)
				.getString(Phonar.KEY_USER_NUMBER, null);
	}

	/**
	 * Get contact photo from phone number
	 * @param phoneNumber: phone number of contact
	 * @param resolver: content resolver (use getContentResolver())
	 * @param resources: application resources (use getResources())
	 * @return a Bitmap photo of contact, or default image if no image found
	 */
	public static Bitmap getPhotoByPhoneNumber(String phoneNumber, ContentResolver resolver, Resources resources) {
		Bitmap defaultPhoto = BitmapFactory.decodeResource(resources, android.R.drawable.ic_menu_report_image);
		if (phoneNumber == null) return defaultPhoto;

		Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Uri photoUri = null;
		Cursor contact = resolver.query(phoneUri,
				new String[] { ContactsContract.Contacts._ID }, null, null, null);

		if (contact.moveToFirst()) {
			long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
			photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);

		}
		else return defaultPhoto;
		if (photoUri != null) {
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, photoUri);
			if (input != null) {
				return BitmapFactory.decodeStream(input);
			}
		} 
		else return defaultPhoto;
		return defaultPhoto;
	}

}
