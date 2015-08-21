package com.itgold.mobilesafe.db;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntivirusDao {

	/**
	 * 判断是否是病毒
	 * 
	 * @param context
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(Context context, String md5) {
		String path = new File(context.getFilesDir(), "antivirus.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		String sql = "select count(1) from datable where md5=?";
		Cursor cursor = db.rawQuery(sql, new String[] { md5 });
		int count = 0;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();

		return count > 0;
	}

	public static void add(Context context, String md5) {
		// 4a515b37a9591cfa7d708e3790e60423
		String path = new File(context.getFilesDir(), "antivirus.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);

		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("type", 6);
		values.put("name", "Android.Adware.AirAD.a");
		values.put("desc", "恶意后台扣费,病毒木马程序");
		db.insert("datable", null, values);
		db.close();
	}
}
