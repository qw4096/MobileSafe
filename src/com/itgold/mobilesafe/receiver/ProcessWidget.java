package com.itgold.mobilesafe.receiver;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

import com.itgold.mobilesafe.service.UpdateWidgetService;
import com.itgold.mobilesafe.utils.Logger;

import android.widget.RemoteViews;

public class ProcessWidget extends AppWidgetProvider {

	private static final String TAG = "ProcessWidget";

	// onEnable ---> onUpdate(反复) ----> onDeleted ---> onDisable

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);

		Logger.d(TAG, "onEnabled");
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);

		Logger.d(TAG, "onDisabled");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Logger.d(TAG, "onDeleted");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Logger.d(TAG, "onUpdate");

		// onUpdate--->
		//
		// ComponentName provider = new ComponentName(context,
		// ProcessWidget.class);
		// RemoteViews views = null;
		// appWidgetManager.updateAppWidget(provider, views);

		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);

	}

	// @Override
	// public void onAppWidgetOptionsChanged(Context context,
	// AppWidgetManager appWidgetManager, int appWidgetId,
	// Bundle newOptions) {
	// super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
	// newOptions);
	// }
}
