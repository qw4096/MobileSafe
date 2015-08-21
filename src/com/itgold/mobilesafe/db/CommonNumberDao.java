package com.itgold.mobilesafe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {

	/**
	 * 查询group的数量
	 * 
	 * @param context
	 * @return
	 */
	public static int getGroupCount(Context context) {
		String path = new File(context.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		String sql = "select count(1) from classlist";
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}

			cursor.close();
		}
		db.close();

		return count;
	}

	/**
	 * 获得第groupPosition的孩子个数
	 * 
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static int getChildCount(Context context, int groupPosition) {
		String path = new File(context.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		String sql = "select count(1) from table" + (groupPosition + 1);
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}

			cursor.close();
		}
		db.close();

		return count;
	}

	/**
	 * 获得group对应的文本
	 * 
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static String getGroupText(Context context, int groupPosition) {
		String path = new File(context.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		String sql = "select name from classlist where idx=?";

		Cursor cursor = db.rawQuery(sql, new String[] { (groupPosition + 1)
				+ "" });
		String result = "";
		if (cursor != null) {
			if (cursor.moveToNext()) {
				result = cursor.getString(0);
			}

			cursor.close();
		}
		db.close();
		return result;
	}

	/**
	 * 查询对应孩子的数据
	 * 
	 * @param context
	 * @param groupPosition
	 * @param childPosition
	 * @return
	 */
	public static String[] getChildText(Context context, int groupPosition,
			int childPosition) {

		// select name,number from table1 where _id=2;

		String path = new File(context.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		String sql = "select name,number from table" + (groupPosition + 1)
				+ " where _id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { (childPosition + 1)
				+ "" });

		String name = "";
		String number = "";
		if (cursor != null) {
			if (cursor.moveToNext()) {
				name = cursor.getString(0);
				number = cursor.getString(1);
			}

			cursor.close();
		}
		db.close();
		return new String[] { name, number };
	}
}
