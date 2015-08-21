package com.itgold.mobilesafe.receiver;

import java.util.List;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus.NmeaListener;
import android.text.format.Formatter;

import com.itgold.mobilesafe.bean.ProcessInfo;
import com.itgold.mobilesafe.engine.ProcessProvider;
import com.itgold.mobilesafe.utils.Logger;

import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

	private static final String TAG = "ProtectedService";

	@Override
	public void onReceive(Context context, Intent intent) {

		Logger.d(TAG, "执行任务");

		// 清理内存
		int count = 0;
		long memory = 0;
		List<ProcessInfo> list = ProcessProvider
				.getAllRunningProcesses(context);
		for (ProcessInfo info : list) {

			if (!info.isForeground) {
				count++;
				memory += info.memory;
				ProcessProvider.killProcess(context, info.packageName);
			}
		}

		if (count > 0) {
			Toast.makeText(
					context,
					"清理了" + count + "进程，节约"
							+ Formatter.formatFileSize(context, memory) + "内存",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "没有可清理的进程", Toast.LENGTH_SHORT).show();
		}
	}
}
