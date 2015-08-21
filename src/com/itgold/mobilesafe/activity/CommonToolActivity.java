package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.engine.SmsProvider;
import com.itgold.mobilesafe.engine.SmsProvider.OnSmsListener;
import com.itgold.mobilesafe.service.WatchDogService1;
import com.itgold.mobilesafe.service.WatchDogService2;
import com.itgold.mobilesafe.utils.ServiceStateUtils;
import com.itgold.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ShareCompat.IntentReader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CommonToolActivity extends Activity {

	private SettingItemView mSivNumberAddress;
	private SettingItemView mSivCommonNumber;

	private SettingItemView mSivSmsbackup;
	private SettingItemView mSivSmsrestore;

	private SettingItemView mSivWatchDog1;
	private SettingItemView mSivWatchDog2;

	// private TextView mTvProgress;
	// private TextView mTvMax;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_tool);

		mSivNumberAddress = (SettingItemView) findViewById(R.id.ct_siv_numberaddress);

		// 设置点击事件
		mSivNumberAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						NumberAddressQueryActivity.class);
				startActivity(intent);
			}
		});

		mSivCommonNumber = (SettingItemView) findViewById(R.id.ct_siv_commonnum);
		mSivCommonNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						CommonNumberActivity.class);
				startActivity(intent);
			}
		});

		// 短信备份
		mSivSmsbackup = (SettingItemView) findViewById(R.id.ct_siv_smsbackup);
		mSivSmsbackup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				smsBackup();
			}
		});

		// 短信还原
		mSivSmsrestore = (SettingItemView) findViewById(R.id.ct_siv_smsrestore);
		mSivSmsrestore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				smsRestore();
			}
		});

		// 设置电子狗的点击事件
		mSivWatchDog1 = (SettingItemView) findViewById(R.id.ct_siv_watchdog1);
		mSivWatchDog1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果服务开启，就关闭
				boolean runging = ServiceStateUtils.isRunging(
						getApplicationContext(), WatchDogService1.class);
				if (runging) {
					stopService(new Intent(getApplicationContext(),
							WatchDogService1.class));

					// UI改变
					mSivWatchDog1.setToggleOn(false);
				} else {
					startService(new Intent(getApplicationContext(),
							WatchDogService1.class));

					// UI改变
					mSivWatchDog1.setToggleOn(true);
				}
			}
		});

		mSivWatchDog2 = (SettingItemView) findViewById(R.id.ct_siv_watchdog2);
		mSivWatchDog2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// <action android:name="android.intent.action.MAIN" />
				// <action
				// android:name="android.settings.ACCESSIBILITY_SETTINGS" />
				// <!-- Wtf... this action is bogus! Can we remove it? -->
				// <action android:name="ACCESSIBILITY_FEEDBACK_SETTINGS" />
				// <category android:name="android.intent.category.DEFAULT" />
				// <category android:name="android.intent.category.VOICE_LAUNCH"
				// />

				// Intent intent = new Intent();
				// intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
				// intent.addCategory("android.intent.category.DEFAULT");
				// intent.addCategory("android.intent.category.VOICE_LAUNCH");
				// startActivity(intent);

				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivity(intent);

			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		// 服务状态回显
		mSivWatchDog1.setToggleOn(ServiceStateUtils.isRunging(
				getApplicationContext(), WatchDogService1.class));

		mSivWatchDog2.setToggleOn(ServiceStateUtils.isRunging(
				getApplicationContext(), WatchDogService2.class));
	}

	private void smsBackup() {

		// 1.ui
		// 弹出进度
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// mTvProgress = (TextView) findViewById(R.id.ct_tv_progress);
		// mTvMax = (TextView) findViewById(R.id.ct_tv_max);
		// 设置总数量 , 进度， 成功，失败

		SmsProvider.smsBackup(this, new OnSmsListener() {

			@Override
			public void onSucess() {
				Toast.makeText(getApplicationContext(), "备份成功",
						Toast.LENGTH_SHORT).show();

				dialog.dismiss();
			}

			@Override
			public void onProgress(int progress) {
				// ui操作
				// mTvProgress.setText("进度:" + progress);
				dialog.setProgress(progress);
			}

			@Override
			public void onMax(int max) {
				// UI操作
				// mTvMax.setText("最大数:" + max);
				dialog.setMax(max);
			}

			@Override
			public void onFailed() {
				Toast.makeText(getApplicationContext(), "备份失败",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});

	}

	private void smsRestore() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// 数据短信的还原
		SmsProvider.smsRestore(this, new OnSmsListener() {
			@Override
			public void onSucess() {
				Toast.makeText(getApplicationContext(), "还原成功",
						Toast.LENGTH_SHORT).show();

				dialog.dismiss();
			}

			@Override
			public void onProgress(int progress) {
				// ui操作
				// mTvProgress.setText("进度:" + progress);
				dialog.setProgress(progress);
			}

			@Override
			public void onMax(int max) {
				// UI操作
				// mTvMax.setText("最大数:" + max);
				dialog.setMax(max);
			}

			@Override
			public void onFailed() {
				Toast.makeText(getApplicationContext(), "还原失败",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
	}

	public void clickAppLock(View view) {
		Intent intent = new Intent(this, AppLockActivity.class);
		startActivity(intent);
	}
}
