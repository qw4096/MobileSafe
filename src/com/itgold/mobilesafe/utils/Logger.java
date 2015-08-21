package com.itgold.mobilesafe.utils;

import android.util.Log;

public class Logger {

	public static final int LEVEL_V = 0;
	public static final int LEVEL_D = 1;
	public static final int LEVEL_I = 2;
	public static final int LEVEL_W = 3;
	public static final int LEVEL_E = 4;
	private static boolean isEnable = true;// 日志是否可见
	private static int LOG_LEVEL = LEVEL_V;// 日志级别

	public static void d(String tag, String msg) {
		if (!isEnable) {
			return;
		}

		if (LOG_LEVEL <= LEVEL_D) {
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (!isEnable) {
			return;
		}

		if (LOG_LEVEL <= LEVEL_E) {
			Log.d(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (!isEnable) {
			return;
		}

		if (LOG_LEVEL <= LEVEL_V) {
			Log.v(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (!isEnable) {
			return;
		}

		if (LOG_LEVEL <= LEVEL_I) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (!isEnable) {
			return;
		}

		if (LOG_LEVEL <= LEVEL_W) {
			Log.w(tag, msg);
		}
	}
}
