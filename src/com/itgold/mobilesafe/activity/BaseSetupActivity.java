package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建实例
		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// e1: MotionEvent,事件的数据载体，down
				// e2: move
				// velocityX: x方向速率

				float x1 = e1.getRawX();
				float x2 = e2.getRawX();

				float y1 = e1.getRawY();
				float y2 = e2.getRawY();

				if (Math.abs(y1 - y2) > Math.abs(x1 - x2)) {
					// y轴运动
					return false;
				}

				// 从右往左 x1 > x2
				if (x1 > x2 + 50) {
					// 如果从右往左滑动，进入下一个页面
					// Toast.makeText(BaseSetupActivity.this, "手势进入下一个页面",
					// Toast.LENGTH_SHORT).show();
					doNext();

					// 处理当前的touch
					return true;
				}

				if (x1 + 50 < x2) {
					// 如果从右往左滑动，进入下一个页面
					// Toast.makeText(BaseSetupActivity.this, "手势进入上一个页面",
					// Toast.LENGTH_SHORT).show();

					doPre();

					// 处理当前的touch
					return true;
				}

				//
				return false;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void clickPre(View view) {
		doPre();
	}

	public void clickNext(View view) {
		doNext();
	}

	private void doPre() {
		if (performPre()) {
			return;
		}

		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);

		finish();
	}

	private void doNext() {
		// // ### 页面跳转--- 结果不同
		// Intent intent = new Intent(this, LostSetup3Activity.class);
		// startActivity(intent);
		// // ###
		if (performNext()) {
			return;
		}

		// ### 动画操作-- 相同
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);

		// ### 相同
		finish();
	}

	/**
	 * 让孩子去执行上一步的操作
	 * 
	 * @return 如果返回true 就不继续向下执行
	 */
	protected abstract boolean performPre();

	/**
	 * 让孩子去执行下一步的操作
	 * 
	 * @return 如果返回true 就不继续向下执行
	 */
	protected abstract boolean performNext();
}
