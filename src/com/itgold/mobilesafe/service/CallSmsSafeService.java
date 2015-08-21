package com.itgold.mobilesafe.service;

import java.lang.reflect.Method;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itgold.mobilesafe.bean.BlackInfo;
import com.itgold.mobilesafe.db.BlackDao;
import com.itgold.mobilesafe.utils.Logger;

public class CallSmsSafeService extends Service {

	private static final String TAG = "CallSmsSafeService";
	private TelephonyManager mTm;
	private BlackDao mDao;

	private PhoneStateListener mListener = new PhoneStateListener() {
		public void onCallStateChanged(int state, final String incomingNumber) {
			// state：电话的状态
			// * @see TelephonyManager#CALL_STATE_IDLE:闲置状态
			// * @see TelephonyManager#CALL_STATE_RINGING:响铃状态
			// * @see TelephonyManager#CALL_STATE_OFFHOOK:摘机--》接听状态

			// incomingNumber：拨入的电话号码

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:

				break;
			case TelephonyManager.CALL_STATE_RINGING:
				// 响铃状态--> 判断是否是黑名单---》挂掉电话
				int type = mDao.findType(incomingNumber);
				if (type == BlackInfo.TYPE_ALL || type == BlackInfo.TYPE_CALL) {
					// 需要拦截
					// 挂掉电话
					// Context.TELEPHONY_SERVICE
					// ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
					try {
						Class<?> clazz = Class
								.forName("android.os.ServiceManager");
						Method method = clazz.getDeclaredMethod("getService",
								String.class);
						IBinder binder = (IBinder) method.invoke(null,
								Context.TELEPHONY_SERVICE);
						ITelephony telephony = ITelephony.Stub
								.asInterface(binder);
						telephony.endCall();

						// Thread.sleep(200);

						// 删除通话记录
						final ContentResolver cr = getContentResolver();
						final Uri url = Uri.parse("content://call_log/calls");

						cr.registerContentObserver(url, true,
								new ContentObserver(new Handler()) {

									public void onChange(boolean selfChange) {

										String where = "number=?";
										String[] selectionArgs = new String[] { incomingNumber };
										cr.delete(url, where, selectionArgs);
									};
								});

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			default:
				break;
			}
		}
	};

	private BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			//
			Bundle extras = intent.getExtras();
			Object[] objs = (Object[]) extras.get("pdus");

			for (Object obj : objs) {

				SmsMessage msg = SmsMessage.createFromPdu((byte[]) obj);
				String address = msg.getOriginatingAddress();

				// 查询
				int type = mDao.findType(address);
				if (type == BlackInfo.TYPE_SMS || type == BlackInfo.TYPE_ALL) {
					abortBroadcast();
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.d(TAG, "开启拦截服务");

		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mDao = new BlackDao(this);

		// 1.拦截电话
		mTm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

		// 2.拦截短信
		// 注册短信接收者
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(mSmsReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.d(TAG, "关闭拦截服务");

		// 注销监听
		mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);

		// 注销
		unregisterReceiver(mSmsReceiver);
	}
}
