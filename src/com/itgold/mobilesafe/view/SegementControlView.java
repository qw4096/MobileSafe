package com.itgold.mobilesafe.view;

import com.itgold.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SegementControlView extends LinearLayout implements
		OnClickListener {
	private TextView mTvLeft;
	private TextView mTvRight;
	private boolean isLeftSelected = true;

	private OnSelectedListener mListener;

	public SegementControlView(Context context) {
		this(context, null);
	}

	public SegementControlView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 绑定xml
		View.inflate(context, R.layout.view_segement, this);
		mTvLeft = (TextView) findViewById(R.id.view_tv_left);
		mTvRight = (TextView) findViewById(R.id.view_tv_right);

		// 默认让左侧选中
		mTvLeft.setSelected(true);

		// 设置点击事件
		mTvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v == mTvLeft) {
			// 如果左侧是不选中的，点击选中左侧
			if (!isLeftSelected) {
				mTvRight.setSelected(false);
				mTvLeft.setSelected(true);

				isLeftSelected = true;

				if (mListener != null) {
					mListener.onSelected(true);
				}
			}
		} else if (v == mTvRight) {
			if (isLeftSelected) {
				// 如果右侧是不选中的情况，点击选中右侧
				mTvRight.setSelected(true);
				mTvLeft.setSelected(false);

				isLeftSelected = false;

				if (mListener != null) {
					mListener.onSelected(false);
				}
			}
		}
	}

	/**
	 * 设置监听器
	 * 
	 * @param listener
	 */
	public void setOnSelectedListener(OnSelectedListener listener) {
		this.mListener = listener;
	}

	/**
	 * 定义接口
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnSelectedListener {
		// 选中和没选中
		void onSelected(boolean isLeftSelected);
	}

}
