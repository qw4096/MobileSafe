package com.itgold.mobilesafe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.itgold.mobilesafe.bean.ContactInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactUtils {
	
	public static List<ContactInfo> getAllPhone(Context context) {

		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,// 用户名
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

		Cursor cursor = resolver.query(uri, projection, null, null, null);

		List<ContactInfo> list = new ArrayList<ContactInfo>();

		if (cursor != null) {
			while (cursor.moveToNext()) {
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				long contactId = cursor.getLong(2);

				ContactInfo info = new ContactInfo();
				info.name = name;
				info.number = number;
				info.contactId = contactId;

				list.add(info);
			}
			cursor.close();
		}
		return list;
	}

	public static Bitmap getContactIcon(Context context, long contactId) {

		// content://contacts/101
		ContentResolver cr = context.getContentResolver();
		Uri contactUri = Uri.withAppendedPath(
				ContactsContract.Contacts.CONTENT_URI, contactId + "");
		InputStream is = null;
		try {
			is = ContactsContract.Contacts.openContactPhotoInputStream(cr,
					contactUri);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
		}
	}

	public static Cursor getAllPhoneCursor(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,// 用户名
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

		return resolver.query(uri, projection, null, null, null);
	}

	public static ContactInfo getContactInfo(Cursor cursor) {

		String name = cursor.getString(1);
		String number = cursor.getString(2);
		long contactId = cursor.getLong(3);

		ContactInfo info = new ContactInfo();
		info.name = name;
		info.number = number;
		info.contactId = contactId;

		return info;
	}

}
