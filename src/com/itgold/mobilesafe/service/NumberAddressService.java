package com.itgold.mobilesafe.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.itgold.mobilesafe.db.AddressDao;
import com.itgold.mobilesafe.utils.Logger;
import com.itgold.mobilesafe.view.AddressToast;

public class NumberAddressService extends Service {

	private static final String TAG = "NumberAddressService";
	private TelephonyManager mTm;
	// private WindowManager mWM;

	// private TextView mView;
	private AddressToast mCallInToast;
	private AddressToast mCallOutToast;

	// 产品经理定义：只显示 拨入
	private boolean showIn = false;// 当前没有显示拨入号码

	private BroadcastReceiver mCallOutReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

			String address = AddressDao.findAddress(NumberAddressService.this,
					number);

			if (showIn) {
				return;
			}
			mCallOutToast.show(address);
		}
	};

	private PhoneStateListener mCallInListener = new PhoneStateListener() {

		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				// 闲置
				// 隐藏 自己的toast

				// if (mView != null) {
				// // note: checking parent() just to make sure the view has
				// // been added... i have seen cases where we get here when
				// // the view isn't yet added, so let's try not to crash.
				// if (mView.getParent() != null) {
				// mWM.removeView(mView);
				// }
				//
				// mView = null;
				// }

				mCallInToast.hide();
				mCallOutToast.hide();

				showIn = false;
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				// 显示归属地
				String address = AddressDao.findAddress(
						NumberAddressService.this, incomingNumber);

				// // Toast toast = Toast.makeText(NumberAddressService.this,
				// // address, Toast.LENGTH_LONG);
				// // toast.show();
				//
				// // 准备显示的view
				// mView = new TextView(getApplicationContext());
				// mView.setText(address);
				//
				// // 准备 LayoutParams
				// WindowManager.LayoutParams params = new
				// WindowManager.LayoutParams();
				// params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				// params.width = WindowManager.LayoutParams.WRAP_CONTENT;
				// params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				// | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
				// params.format = PixelFormat.TRANSLUCENT;
				// // params.windowAnimations =
				// // com.android.internal.R.style.Animation_Toast;
				// params.type = WindowManager.LayoutParams.TYPE_TOAST;
				// // params.setTitle("Toast");
				//
				// mWM.addView(mView, params);

				mCallInToast.show(address);
				showIn = true;
				mCallOutToast.hide();
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			default:
				break;
			}

		};

	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Logger.d(TAG, "归属地服务开启");

		// 准备WindowManager
		// mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		// 新建toast
		mCallInToast = new AddressToast(this);
		mCallOutToast = new AddressToast(this);

		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// 1.拨入号码的归属地
		mTm.listen(mCallInListener, PhoneStateListener.LISTEN_CALL_STATE);

		// 2.拨出号码的归属地
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mCallOutReceiver, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Logger.d(TAG, "归属地服务关闭");

		// 注销
		mTm.listen(mCallInListener, PhoneStateListener.LISTEN_NONE);
	}

}
