package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.PreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LostFindActivity extends Activity {

	private TextView mTvNumber;
	private ImageView mIvProtecting;
	private RelativeLayout mRlProtecting;
	private RelativeLayout mRlSetup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_find);

		mTvNumber = (TextView) findViewById(R.id.lf_tv_number);

		String number = PreferenceUtils.getString(this, Constants.SJFD_NUMBER);
		mTvNumber.setText(number);

		mIvProtecting = (ImageView) findViewById(R.id.lf_iv_protecting);
		boolean protecting = PreferenceUtils.getBoolean(this,
				Constants.SJFD_PROTECTING);
		// if (protecting) {
		// mIvProtecting.setImageResource(R.drawable.lock);
		// } else {
		// mIvProtecting.setImageResource(R.drawable.unlock);
		// }
		mIvProtecting.setImageResource(protecting ? R.drawable.lock
				: R.drawable.unlock);

		mRlProtecting = (RelativeLayout) findViewById(R.id.lf_rl_protecting);
		mRlProtecting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean protecting = PreferenceUtils.getBoolean(
						LostFindActivity.this, Constants.SJFD_PROTECTING);

				if (protecting) {
					// 如果是保护的，显示不保护
					mIvProtecting.setImageResource(R.drawable.unlock);

					PreferenceUtils.putBoolean(LostFindActivity.this,
							Constants.SJFD_PROTECTING, false);
				} else {
					mIvProtecting.setImageResource(R.drawable.lock);

					PreferenceUtils.putBoolean(LostFindActivity.this,
							Constants.SJFD_PROTECTING, true);
				}
			}
		});

		mRlSetup = (RelativeLayout) findViewById(R.id.lf_rl_setup);

		mRlSetup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LostFindActivity.this,
						LostSetup1Activity.class);
				startActivity(intent);

				overridePendingTransition(R.anim.next_enter, R.anim.next_exit);

				finish();
			}
		});
	}
}
