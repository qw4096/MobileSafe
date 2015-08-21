package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LostSetup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup1);

	}

	// public void clickNext(View view) {
	//
	// Intent intent = new Intent(this, LostSetup2Activity.class);
	// startActivity(intent);
	//
	// // 设置动画
	// // enterAnim: anim 资源文件，进入的activity的动画
	// // exitAnim: anim 资源文件, 出去的activity的动画
	// overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
	//
	// finish();
	// }

	@Override
	protected boolean performNext() {
		Intent intent = new Intent(this, LostSetup2Activity.class);
		startActivity(intent);

		return false;
	}

	@Override
	protected boolean performPre() {
		return true;
	}

}
