package com.itgold.mobilesafe.service;

import com.itgold.mobilesafe.R;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;

import com.itgold.mobilesafe.engine.ProcessProvider;
import com.itgold.mobilesafe.receiver.ProcessWidget;
import com.itgold.mobilesafe.utils.Logger;

import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private static final String TAG = "UpdateWidgetService";
	private AppWidgetManager mAwm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Logger.d(TAG, "更新widget服务开启");

		mAwm = AppWidgetManager.getInstance(this);

		// 开启更新widget
		startUpdate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Logger.d(TAG, "更新widget服务关闭");
	}

	private void startUpdate() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					ComponentName provider = new ComponentName(
							UpdateWidgetService.this, ProcessWidget.class);

					// 远程的view
					RemoteViews localRemoteViews = new RemoteViews(
							getPackageName(), R.layout.process_widget);

					// 进程数
					localRemoteViews.setTextViewText(
							R.id.process_count,
							"正在运行的进程:"
									+ ProcessProvider
											.getRunningProcessCount(UpdateWidgetService.this)
									+ "个");

					// 内存
					localRemoteViews.setTextViewText(
							R.id.process_memory,
							"可用内存:"
									+ Formatter
											.formatFileSize(
													UpdateWidgetService.this,
													ProcessProvider
															.getFreeMemory(UpdateWidgetService.this)));

					Intent intent = new Intent();
					intent.setAction("xxxx.receiver");

					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							UpdateWidgetService.this, 100, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					// 设置点击事件
					localRemoteViews.setOnClickPendingIntent(R.id.btn_clear,
							pendingIntent);

					// 清理内存

					mAwm.updateAppWidget(provider, localRemoteViews);

					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}
}
