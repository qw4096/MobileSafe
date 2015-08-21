package com.itgold.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDBHelper extends SQLiteOpenHelper {

	public AppLockDBHelper(Context context) {
		super(context, AppLockDB.DB, null, AppLockDB.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 给数据库建表
		db.execSQL(AppLockDB.AppLock.CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
