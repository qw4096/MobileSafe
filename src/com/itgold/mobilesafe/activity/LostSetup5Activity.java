package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.PreferenceUtils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class LostSetup5Activity extends BaseSetupActivity {
	private CheckBox mCb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup5);

		mCb = (CheckBox) findViewById(R.id.setup5_cb);

		// 初始化状态
		boolean flag = PreferenceUtils.getBoolean(this,
				Constants.SJFD_PROTECTING);
		mCb.setChecked(flag);

		mCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 标记是否有开启防盗功能
				PreferenceUtils.putBoolean(LostSetup5Activity.this,
						Constants.SJFD_PROTECTING, isChecked);
			}
		});

	}

	// public void clickPre(View view) {
	//
	// Intent intent = new Intent(this, LostSetup4Activity.class);
	// startActivity(intent);
	//
	// overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
	//
	// finish();
	// }

	// public void clickNext(View view) {
	//
	// // Intent intent = new Intent(this, LostSetup5Activity.class);
	// // startActivity(intent);
	//
	// overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
	//
	// }

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, LostSetup4Activity.class);
		startActivity(intent);

		return false;
	}

	@Override
	protected boolean performNext() {
		// TODO Auto-generated method stub

		// 校验是否勾选 开启
		if (!mCb.isChecked()) {
			Toast.makeText(this, "要开启防盗功能，必须勾选", Toast.LENGTH_SHORT).show();
			return true;
		}

		// 标记已经设置过
		PreferenceUtils.putBoolean(this, Constants.SJFD_SETUP, true);

		// 往结果页面跳转
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);

		return false;
	}
}
