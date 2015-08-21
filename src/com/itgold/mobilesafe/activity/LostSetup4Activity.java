package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.receiver.SafeAdimnReceiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LostSetup4Activity extends BaseSetupActivity {

	protected static final int REQUEST_CODE_ENABLE_ADMIN = 1010;
	private RelativeLayout mRlAdmin;
	private ImageView mIvAdmin;
	private DevicePolicyManager mDpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup4);

		mRlAdmin = (RelativeLayout) findViewById(R.id.setup4_rl_admin);
		mIvAdmin = (ImageView) findViewById(R.id.setup4_iv_admin);

		mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		final ComponentName who = new ComponentName(getApplicationContext(),
				SafeAdimnReceiver.class);

		// 设置初始化的状态
		if (mDpm.isAdminActive(who)) {
			// 激活
			// ui改变
			mIvAdmin.setImageResource(R.drawable.admin_activated);
		} else {
			// ui改变
			mIvAdmin.setImageResource(R.drawable.admin_inactivated);
		}

		// 设置点击事件
		mRlAdmin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (mDpm.isAdminActive(who)) {
					// 如果是激活的就取消激活
					mDpm.removeActiveAdmin(who);

					mDpm.resetPassword("", 0);

					// ui改变
					mIvAdmin.setImageResource(R.drawable.admin_inactivated);
				} else {
					// 需要激活
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"黄金卫士");
					startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {

			switch (resultCode) {
			case Activity.RESULT_OK:
				// 激活成功
				// ui改变
				mIvAdmin.setImageResource(R.drawable.admin_activated);
				break;
			default:
				break;
			}
		}
	}

	// public void clickPre(View view) {
	//
	// Intent intent = new Intent(this, LostSetup3Activity.class);
	// startActivity(intent);
	//
	// overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
	//
	// finish();
	// }

	// public void clickNext(View view) {
	//
	// Intent intent = new Intent(this, LostSetup5Activity.class);
	// startActivity(intent);
	//
	// overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
	//
	// finish();
	// }

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, LostSetup3Activity.class);
		startActivity(intent);

		return false;
	}

	@Override
	protected boolean performNext() {
		// 校验是否开启设备管理员
		ComponentName who = new ComponentName(getApplicationContext(),
				SafeAdimnReceiver.class);
		if (!mDpm.isAdminActive(who)) {
			Toast.makeText(getApplicationContext(), "要开启手机防盗必须设置设备管理员",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		Intent intent = new Intent(this, LostSetup5Activity.class);
		startActivity(intent);

		return false;
	}

}
