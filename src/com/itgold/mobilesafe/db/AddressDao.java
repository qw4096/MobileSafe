package com.itgold.mobilesafe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class AddressDao {

	public static String findAddress(Context context, String number) {
		String path = new File(context.getFilesDir(), "address.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// select cardtype from info where mobileprefix='1351234';

		// 13512345678
		// 1[34578]
		// ^1[34578]\d{9}$
		boolean isPhone = number.matches("^1[34578]\\d{9}$");
		String address = null;
		if (isPhone) {
			// 手机号码的查询
			String sql = "select cardtype from info where mobileprefix=?";

			String prefix = number.substring(0, 7);
			Cursor cursor = db.rawQuery(sql, new String[] { prefix });
			if (cursor != null) {
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
				}
				cursor.close();
			}
		} else {
			// 非手机
			int length = number.length();

			switch (length) {
			case 3:
				address = "紧急电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "服务号码";
				break;
			case 7:
			case 8:
				address = "本地座机";
				break;
			case 10:
			case 11:
			case 12:
				// 查询
				String prefix = number.substring(0, 3);

				String sql = "select city from info where area=?";
				Cursor cursor = db.rawQuery(sql, new String[] { prefix });
				if (cursor != null) {
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();
				}

				if (TextUtils.isEmpty(address)) {
					// 没有

					prefix = number.substring(0, 4);
					cursor = db.rawQuery(sql, new String[] { prefix });
					if (cursor != null) {
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}

				if (TextUtils.isEmpty(address)) {
					address = "未知";
				}

				break;
			default:
				address = "未知";
				break;
			}
			// 110,119,120，报警号码
			// 5556,虚拟机
			// 10086,10000,95599,服务号码
			// 7 本地座机
			// 8 本地座机
			// 075588888888 --> 外地 0108888888 0101234567
			// 10658984883828323882

		}

		db.close();
		return address;
	}
}
