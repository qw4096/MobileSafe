package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.PreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LostSetup3Activity extends BaseSetupActivity {
	private static final int REQUEST_CODE_CONTACT = 100;
	private EditText mEtNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup3);

		mEtNumber = (EditText) findViewById(R.id.setup3_et_number);

		// 设置安全号码
		String number = PreferenceUtils.getString(this, Constants.SJFD_NUMBER);
		mEtNumber.setText(number);
		if (!TextUtils.isEmpty(number)) {
			mEtNumber.setSelection(number.length());
		}
	}

	public void clickContact(View view) {
		Intent intent = new Intent(this, ContactSelectedActivity2.class);
		startActivityForResult(intent, REQUEST_CODE_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE_CONTACT) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				String number = data
						.getStringExtra(ContactSelectedActivity.KEY_NUMBER);
				mEtNumber.setText(number);
				if (!TextUtils.isEmpty(number)) {
					mEtNumber.setSelection(number.length());
				}
				break;

			default:
				break;
			}
		}
	}

	// public void clickPre(View view) {
	//
	// Intent intent = new Intent(this, LostSetup2Activity.class);
	// startActivity(intent);
	//
	// overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
	//
	// finish();
	// }

	//
	// public void clickNext(View view) {
	//
	// Intent intent = new Intent(this, LostSetup4Activity.class);
	// startActivity(intent);
	//
	// overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
	//
	// finish();
	// }

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, LostSetup2Activity.class);
		startActivity(intent);

		return false;
	}

	@Override
	protected boolean performNext() {

		// 校验，安全号码是否为空
		String number = mEtNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "如果要开启手机防盗，必须设置安全号码", Toast.LENGTH_SHORT)
					.show();
			return true;
		}

		// 记录安全号码
		PreferenceUtils.putString(this, Constants.SJFD_NUMBER, number);

		Intent intent = new Intent(this, LostSetup4Activity.class);
		startActivity(intent);

		return false;
	}

}
