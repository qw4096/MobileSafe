package com.itgold.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itgold.mobilesafe.utils.Logger;

public class BlacklistDBHelper extends SQLiteOpenHelper {

	private static final String TAG = "BlacklistDBHelper";

	public BlacklistDBHelper(Context context) {
		super(context, BlackListDB.DB_NAME, null, BlackListDB.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 初始化数据库
		// 创建表
		String sql = BlackListDB.BlackList.SQL_CREATE_TABLE;
		Logger.d(TAG, "" + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
