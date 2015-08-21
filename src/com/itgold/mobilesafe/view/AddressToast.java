package com.itgold.mobilesafe.view;

import com.itgold.mobilesafe.R;

import android.content.Context;
import android.graphics.PixelFormat;

import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.Logger;
import com.itgold.mobilesafe.utils.PreferenceUtils;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressToast implements OnTouchListener {
	private static final String TAG = "AddressToast";
	private Context mContext;
	private WindowManager mWM;

	private WindowManager.LayoutParams mParams;
	private View mView;// 显示的view
	private float startX;
	private float startY;

	public AddressToast(Context context) {
		this.mContext = context;

		// 窗体管理者
		mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

		// 准备 LayoutParams
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		// | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mParams.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		// params.setTitle("Toast");
	}

	public void show(String address) {
		hide();

		// 准备显示的view
		// mView = new TextView(mContext);
		// ((TextView) mView).setText(address);

		mView = View.inflate(mContext, R.layout.toast_address, null);

		int style = PreferenceUtils.getInt(mContext, Constants.ADDRESS_STYLE,
				R.drawable.toast_address_normal);
		mView.setBackgroundResource(style);

		TextView tv = (TextView) mView.findViewById(R.id.toast_tv_address);
		tv.setText(address);

		// 设置touch的监听
		mView.setOnTouchListener(this);

		mWM.addView(mView, mParams);

	}

	public void hide() {
		if (mView != null) {
			// note: checking parent() just to make sure the view has
			// been added... i have seen cases where we get here when
			// the view isn't yet added, so let's try not to crash.
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}

			mView = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Logger.d(TAG, "按下");

			startX = event.getRawX();
			startY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			Logger.d(TAG, "移动");
			float newX = event.getRawX();
			float newY = event.getRawY();

			float diffX = newX - startX;
			float diffY = newY - startY;

			mParams.x += (int) (diffX + 0.5f);// 四舍五入
			mParams.y += (int) (diffY + 0.5f);

			if (mView != null) {
				mWM.updateViewLayout(mView, mParams);
			}
			startX = newX;
			startY = newY;
			break;
		case MotionEvent.ACTION_UP:
			Logger.d(TAG, "抬起");
			break;
		default:
			break;
		}

		return true;
	}

}
