package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.PreferenceUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LostSetup2Activity extends BaseSetupActivity {
	private RelativeLayout mRlBind;
	private ImageView mIvLock;
	private TelephonyManager mTm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup2);

		mRlBind = (RelativeLayout) findViewById(R.id.setup2_rl_bind);
		mIvLock = (ImageView) findViewById(R.id.setup2_iv_lock);

		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// 初始化UI
		String sim = PreferenceUtils.getString(LostSetup2Activity.this,
				Constants.SJFD_SIM);
		if (TextUtils.isEmpty(sim)) {
			mIvLock.setImageResource(R.drawable.unlock);
		} else {
			mIvLock.setImageResource(R.drawable.lock);
		}

		mRlBind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sim = PreferenceUtils.getString(LostSetup2Activity.this,
						Constants.SJFD_SIM);
				if (TextUtils.isEmpty(sim)) {
					// 如果没有绑定了，绑定
					// 数据存储 当前sim卡串号

					sim = mTm.getSimSerialNumber();
					PreferenceUtils.putString(LostSetup2Activity.this,
							Constants.SJFD_SIM, sim);

					// UI
					mIvLock.setImageResource(R.drawable.lock);
				} else {
					// 如果已经绑定了，解除绑定
					// 数据存储 清除sim卡串号
					PreferenceUtils.putString(LostSetup2Activity.this,
							Constants.SJFD_SIM, null);
					// UI
					mIvLock.setImageResource(R.drawable.unlock);
				}

			}
		});

	}

	// 上一步点击===》 行为相同
	// public void clickPre(View view) {
	//
	// Intent intent = new Intent(this, LostSetup1Activity.class);
	// startActivity(intent);
	//
	// overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
	//
	// finish();
	// }

	// // 下一步点击===》 行为相同
	// public void clickNext(View view) {
	//
	// // ### 页面跳转--- 结果不同
	// Intent intent = new Intent(this, LostSetup3Activity.class);
	// startActivity(intent);
	// // ###
	//
	// // ### 动画操作-- 相同
	// overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
	//
	// // ### 相同
	// finish();
	// }

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, LostSetup1Activity.class);
		startActivity(intent);

		return false;
	}

	@Override
	protected boolean performNext() {
		// 检测用户是否 绑定了sim卡

		String sim = PreferenceUtils.getString(this, Constants.SJFD_SIM);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(this, "如果要开启手机防盗，必须绑定手机sim卡", Toast.LENGTH_SHORT)
					.show();
			return true;
		}

		Intent intent = new Intent(this, LostSetup3Activity.class);
		startActivity(intent);

		return false;
	}
}
