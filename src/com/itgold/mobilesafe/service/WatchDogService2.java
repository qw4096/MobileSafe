package com.itgold.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.itgold.mobilesafe.activity.LockScreenActivity;
import com.itgold.mobilesafe.db.AppLockDao;
import com.itgold.mobilesafe.utils.Logger;

import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

public class WatchDogService2 extends AccessibilityService {

	private static final String TAG = "WatchDogService2";

	private List<String> mFreeList = new ArrayList<String>();
	private List<String> mLockList;// 所有上锁的应用

	private AppLockDao mDao;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				// 清空
				mFreeList.clear();
			} else if (action.equals("com.itgold.free")) {
				String packageName = intent
						.getStringExtra(LockScreenActivity.EXTRA_PACKAGE_NAME);
				mFreeList.add(packageName);
			}
		}
	};

	private ContentObserver mObserver = new ContentObserver(new Handler()) {

		public void onChange(boolean selfChange) {
			mLockList = mDao.findAll();
		};
	};

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		// 服务连接开启

		Logger.d(TAG, "服务连接 : ");

		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		setServiceInfo(info);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mDao = new AppLockDao(this);
		mLockList = mDao.findAll();

		// 注册广播接收
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.itgold.free");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

		// 添加数据库监听
		ContentResolver cr = getContentResolver();
		// content://a/b/c
		// content://a
		// false:
		cr.registerContentObserver(
				Uri.parse("content://com.itgold.db.applock"), true, mObserver);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Logger.d(TAG, "停止电子狗1服务");

		unregisterReceiver(mReceiver);
		getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		int eventType = event.getEventType();
		Logger.d(TAG, "eventType : " + eventType);

		if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			// window改变的事件
			// 获得改变后的包名
			String packageName = event.getPackageName().toString();

			Logger.d(TAG, "packageName : " + packageName);

			if (mFreeList.contains(packageName)) {
				return;
			}

			// 如果包名存在 上锁的数据库，弹出自己的activity拦截页面

			if (mLockList.contains(packageName)) {
				// 需要上锁
				Intent intent = new Intent(WatchDogService2.this,
						LockScreenActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(LockScreenActivity.EXTRA_PACKAGE_NAME,
						packageName);
				startActivity(intent);
			}
		}

	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

}
