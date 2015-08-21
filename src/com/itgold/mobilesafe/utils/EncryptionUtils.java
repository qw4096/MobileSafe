package com.itgold.mobilesafe.utils;

public class EncryptionUtils {

	public static String encode(String text, int key) {

		byte[] bytes = text.getBytes();

		for (int i = 0; i < bytes.length; i++) {
			// bytes[i] = xxx;
			bytes[i] ^= key;
		}
		return new String(bytes);
	}
	
	public static String decode(String text, int key) {

		byte[] bytes = text.getBytes();

		for (int i = 0; i < bytes.length; i++) {
			// bytes[i] = xxx;
			bytes[i] ^= key;
		}
		return new String(bytes);
	}
}
