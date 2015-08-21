package com.itgold.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.itgold.mobilesafe.R;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.itgold.mobilesafe.activity.SplashActivity;
import com.itgold.mobilesafe.utils.Logger;

import android.widget.RemoteViews;

public class ProtectedService extends Service {

	private static final String TAG = "ProtectedService";

	private static final int ID = 100;

	private Timer timer;

	private ScheduledFuture<?> future;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Logger.d(TAG, "前台进程保护服务开启");

		// 将服务做成前台进程
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "黄金卫士保护您的安全";
		notification.contentView = new RemoteViews(getPackageName(),
				R.layout.notification_proctected);

		Intent intent = new Intent(this, SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.contentIntent = PendingIntent.getActivity(this, 100,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		startForeground(ID, notification);

		// 开启定时器

		// timer = new Timer();
		// // timer.schedule(task, when);//在某个日期执行一次
		// // timer.schedule(task, delay);//当前时间延时delay毫秒后执行
		// timer.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// Logger.d(TAG, "执行任务....");
		//
		// }
		// }, 0, 5000);

		// 获得计划task池子

		// ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
		//
		// future = pool.scheduleAtFixedRate(new Runnable() {
		//
		// @Override
		// public void run() {
		// Logger.d(TAG, "执行任务....");
		// }
		// }, 0, 3000, TimeUnit.MILLISECONDS);

		// AlarmManager am = (AlarmManager)
		// getSystemService(Context.ALARM_SERVICE);
		// Intent intent = new Intent();
		// intent.setAction("xxxx.receiver");
		// PendingIntent operation = PendingIntent.getBroadcast(this, 100,
		// intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// am.setRepeating(AlarmManager.RTC, 0, 3000, operation);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Logger.d(TAG, "前台进程保护服务关闭");

		// timer.cancel();

		// future.cancel(true);
	}
}
