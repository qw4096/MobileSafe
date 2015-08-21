package com.itgold.mobilesafe.view;

import com.itgold.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressDesView extends LinearLayout {

	private TextView mTvTitle;
	private TextView mTvLeft;
	private TextView mTvRight;
	private ProgressBar mPbProgress;

	public ProgressDesView(Context context) {
		this(context, null);
	}

	public ProgressDesView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 类和xml绑定
		View.inflate(context, R.layout.view_progress_des, this);

		// 初始化view
		mTvTitle = (TextView) findViewById(R.id.view_pdv_tv_title);
		mTvLeft = (TextView) findViewById(R.id.view_pdv_tv_left);
		mTvRight = (TextView) findViewById(R.id.view_pdv_tv_right);
		mPbProgress = (ProgressBar) findViewById(R.id.view_pdv_pb_progress);
	}

	public void setDesTitle(String title) {
		mTvTitle.setText(title);
	}

	public void setDesLeft(String title) {
		mTvLeft.setText(title);
	}

	public void setDesRight(String title) {
		mTvRight.setText(title);
	}

	public void setDesProgress(int progress) {
		mPbProgress.setProgress(progress);
	}
}
