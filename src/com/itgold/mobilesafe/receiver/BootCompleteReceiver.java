package com.itgold.mobilesafe.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.itgold.mobilesafe.service.ProtectedService;
import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.Logger;
import com.itgold.mobilesafe.utils.PreferenceUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.d(TAG, "接收到开机");

		boolean flag = PreferenceUtils.getBoolean(context,
				Constants.SJFD_PROTECTING);

		// 如果没有开启防盗保护
		if (!flag) {
			return;
		}

		// 检测sim卡是否一致
		// 取得当前sim卡
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String currentSim = tm.getSimSerialNumber();
		String localSim = PreferenceUtils
				.getString(context, Constants.SJFD_SIM) + "xxx";

		if (!currentSim.equals(localSim)) {
			Logger.d(TAG, "手机可能被盗!");

			// 发送短信给安全号码
			SmsManager sm = SmsManager.getDefault();
			String number = PreferenceUtils.getString(context,
					Constants.SJFD_NUMBER);
			sm.sendTextMessage(number, null, "shouji diu le....XXXXX", null,
					null);
		}

		// 开启服务
		context.startService(new Intent(context, ProtectedService.class));
	}

}
