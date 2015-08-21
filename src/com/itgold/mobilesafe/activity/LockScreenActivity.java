package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.bean.AppInfo;
import com.itgold.mobilesafe.engine.AppInfoProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LockScreenActivity extends Activity {
	public static final String EXTRA_PACKAGE_NAME = "packageName";
	private EditText mEtPwd;
	private TextView mTvName;
	private ImageView mIvIcon;
	private String mPackageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);

		// 初始化view
		mEtPwd = (EditText) findViewById(R.id.ls_et_pwd);
		mIvIcon = (ImageView) findViewById(R.id.ls_iv_icon);
		mTvName = (TextView) findViewById(R.id.ls_tv_name);

		// 显示对应的应用名称和图标

		mPackageName = getIntent().getStringExtra(EXTRA_PACKAGE_NAME);
		AppInfo info = AppInfoProvider.getAppInfo(this, mPackageName);

		mIvIcon.setImageDrawable(info.icon);
		mTvName.setText(info.name);
	}

	public void clickOk(View view) {
		// 校验密码
		String pwd = mEtPwd.getText().toString().trim();

		if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		if ("123".equals(pwd)) {
			// 通知电子狗放行
			Intent intent = new Intent();
			intent.setAction("com.itgold.free");
			intent.putExtra(EXTRA_PACKAGE_NAME, mPackageName);
			sendBroadcast(intent);

			finish();
		}
	}

	@Override
	public void onBackPressed() {

		// 回到桌面
		// <intent-filter>
		// <action android:name="android.intent.action.MAIN" />
		// <category android:name="android.intent.category.HOME" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <category android:name="android.intent.category.MONKEY"/>
		// </intent-filter>

		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);

		finish();
	}
}
