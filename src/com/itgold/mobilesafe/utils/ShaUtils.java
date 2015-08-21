package com.itgold.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.itgold.mobilesafe.utils.Logger;

public class ShaUtils {
	private static final String TAG = "MD5Utils";

	public static String encode(String text) {

		try {
			MessageDigest digest = MessageDigest.getInstance("sha-1");
			byte[] buffer = digest.digest(text.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : buffer) {
				int a = b & 0xff;
				// Logger.d(TAG, "" + a);

				String hex = Integer.toHexString(a);
				Logger.d(TAG, "" + hex);

				if (hex.length() == 1) {
					hex = 0 + hex;
				}

				sb.append(hex);
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		encode("123456");
	}
}
