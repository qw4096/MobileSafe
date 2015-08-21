package com.itgold.mobilesafe.test;


import android.test.AndroidTestCase;

import com.itgold.mobilesafe.utils.Base64Utils;
import com.itgold.mobilesafe.utils.EncryptionUtils;
import com.itgold.mobilesafe.utils.Logger;
import com.itgold.mobilesafe.utils.MD5Utils;
import com.itgold.mobilesafe.utils.ShaUtils;

public class MD5Test extends AndroidTestCase {

	private static final String TAG = "MD5Utils";

	public void testMD5() {

		String text = "123456";
		Logger.d(TAG, "" + ShaUtils.encode(text));
	}

	public void testBase64() {
		String text = "123456";
		Logger.d(TAG, "编码:" + Base64Utils.encode(text));
		Logger.d(TAG, "解码:" + Base64Utils.decode(Base64Utils.encode(text)));
	}

	public void testEncry() {
		String text = "123456";
		int key = 88;
		String encode = EncryptionUtils.encode(text, key);
		Logger.d(TAG, "编码:" + EncryptionUtils.encode(text, key));
		Logger.d(TAG, "解码:" + EncryptionUtils.decode(encode, key));
	}
}
