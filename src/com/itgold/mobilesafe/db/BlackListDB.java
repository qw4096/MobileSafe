package com.itgold.mobilesafe.db;

public interface BlackListDB {

	String DB_NAME = "black.db";
	int VERSION = 1;

	public interface BlackList {
		String TABLE_NAME = "black_list";

		String COLUMN_ID = "_id";
		String COLUMN_NUMBER = "number";
		String COLUMN_TYPE = "_type";

		String SQL_CREATE_TABLE = "create table " + TABLE_NAME + "("
				+ COLUMN_ID + " integer primary key autoincrement,"
				+ COLUMN_NUMBER + " text unique," + COLUMN_TYPE + " integer"
				+ ")";
	}

}
