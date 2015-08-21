package com.itgold.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.itgold.mobilesafe.activity.LockScreenActivity;
import com.itgold.mobilesafe.db.AppLockDao;
import com.itgold.mobilesafe.utils.Logger;

public class WatchDogService1 extends Service {

	private static final String TAG = "WatchDogService1";
	private ActivityManager mAm;
	private AppLockDao mDao;

	private List<String> mFreeList = new ArrayList<String>();
	private boolean isRunging = false;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				// 停止耗时操作
				isRunging = false;

				// 清空
				mFreeList.clear();
			} else if (action.equals("com.itgold.free")) {
				String packageName = intent
						.getStringExtra(LockScreenActivity.EXTRA_PACKAGE_NAME);
				mFreeList.add(packageName);
			} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
				// 停止耗时操作
				startWatch();
			}

		}
	};

	private ContentObserver mObserver = new ContentObserver(new Handler()) {

		public void onChange(boolean selfChange) {
			mLockList = mDao.findAll();
		};
	};
	private List<String> mLockList;// 所有上锁的应用

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Logger.d(TAG, "开启电子狗1服务");

		// 轮询当前的任务栈
		mDao = new AppLockDao(this);
		mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		mLockList = mDao.findAll();

		// 注册广播接收
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.itgold.free");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, filter);

		// 添加数据库监听
		ContentResolver cr = getContentResolver();
		// content://a/b/c
		// content://a
		// false:
		cr.registerContentObserver(
				Uri.parse("content://com.itgold.db.applock"), true, mObserver);

		startWatch();
	}

	private void startWatch() {
		if (isRunging) {
			return;
		}
		isRunging = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRunging) {

					Logger.d(TAG, "watch......");
					// 实时获取
					List<RunningTaskInfo> tasks = mAm.getRunningTasks(1);
					RunningTaskInfo recentTask = tasks.get(0);// 当前显示的任务栈

					// 当前应用程序的包名
					String packageName = recentTask.topActivity
							.getPackageName();

					if (mFreeList.contains(packageName)) {
						continue;
					}

					// 如果包名存在 上锁的数据库，弹出自己的activity拦截页面

					if (mLockList.contains(packageName)) {
						// 需要上锁
						Intent intent = new Intent(WatchDogService1.this,
								LockScreenActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(LockScreenActivity.EXTRA_PACKAGE_NAME,
								packageName);
						startActivity(intent);
					}
					// if (mDao.findLock(packageName)) {
					// // 需要上锁
					// Intent intent = new Intent(WatchDogService1.this,
					// LockScreenActivity.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// intent.putExtra(LockScreenActivity.EXTRA_PACKAGE_NAME,
					// packageName);
					// startActivity(intent);
					// }

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Logger.d(TAG, "停止电子狗1服务");

		unregisterReceiver(mReceiver);
		getContentResolver().unregisterContentObserver(mObserver);
	}

}
