package com.itgold.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import com.itgold.mobilesafe.bean.BlackInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackDao {
	private BlacklistDBHelper mHelper;

	public BlackDao(Context context) {
		mHelper = new BlacklistDBHelper(context);
	}

	/**
	 * 添加号码到数据库
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	public boolean add(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackListDB.BlackList.COLUMN_NUMBER, number);
		values.put(BlackListDB.BlackList.COLUMN_TYPE, type);

		long insert = db.insert(BlackListDB.BlackList.TABLE_NAME, null, values);

		db.close();

		return insert != -1;
	}

	/**
	 * 删除号码
	 * 
	 * @param number
	 * @return
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		String whereClause = BlackListDB.BlackList.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };
		int delete = db.delete(BlackListDB.BlackList.TABLE_NAME, whereClause,
				whereArgs);

		db.close();

		return delete != 0;
	}

	/**
	 * 更新号码拦截 的类型
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	public boolean update(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackListDB.BlackList.COLUMN_TYPE, type);
		String whereClause = BlackListDB.BlackList.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };
		int update = db.update(BlackListDB.BlackList.TABLE_NAME, values,
				whereClause, whereArgs);

		db.close();
		return update != 0;
	}

	/**
	 * 查找黑名单类型
	 * 
	 * @param number
	 * @return
	 */
	public int findType(String number) {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String sql = "select " + BlackListDB.BlackList.COLUMN_TYPE + " from "
				+ BlackListDB.BlackList.TABLE_NAME + " where "
				+ BlackListDB.BlackList.COLUMN_NUMBER + "=?";

		String[] selectionArgs = new String[] { number };

		Cursor cursor = db.rawQuery(sql, selectionArgs);

		int type = -1;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				type = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();
		return type;
	}

	/**
	 * 查询所有的数据
	 * 
	 * @return
	 */
	public List<BlackInfo> findAll() {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String sql = "select " + BlackListDB.BlackList.COLUMN_NUMBER + ","
				+ BlackListDB.BlackList.COLUMN_TYPE + " from "
				+ BlackListDB.BlackList.TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, null);

		List<BlackInfo> list = new ArrayList<BlackInfo>();

		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackInfo info = new BlackInfo();
				info.number = number;
				info.type = type;
				list.add(info);
			}
			cursor.close();
		}

		db.close();
		return list;
	}

	/**
	 * 查询部分数据
	 * 
	 * @param perPageSize
	 *            :要查询的数据的条数
	 * @param index
	 *            :从什么位置开始查询
	 * @return
	 */
	public List<BlackInfo> findPart(int perPageSize, int index) {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		// limit:页面每页要显示的条数
		// offset:从数据库取数据的时候，从什么位置开始取
		String sql = "select " + BlackListDB.BlackList.COLUMN_NUMBER + ","
				+ BlackListDB.BlackList.COLUMN_TYPE + " from "
				+ BlackListDB.BlackList.TABLE_NAME + " limit " + perPageSize
				+ " offset " + index;

		Cursor cursor = db.rawQuery(sql, null);

		List<BlackInfo> list = new ArrayList<BlackInfo>();

		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackInfo info = new BlackInfo();
				info.number = number;
				info.type = type;
				list.add(info);
			}
			cursor.close();
		}

		db.close();
		return list;
	}
}
