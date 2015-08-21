package com.itgold.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.bean.AppInfo;
import com.itgold.mobilesafe.db.AppLockDao;
import com.itgold.mobilesafe.engine.AppInfoProvider;
import com.itgold.mobilesafe.view.SegementControlView;
import com.itgold.mobilesafe.view.SegementControlView.OnSelectedListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppLockActivity extends Activity {

	private SegementControlView mSegementControlView;
	private TextView mTvTip;

	private ListView mLvUnlock;
	private ListView mLvlock;

	private List<AppInfo> mUnlockDatas;
	private List<AppInfo> mLockDatas;

	private LinearLayout mLloading;
	private AppLockDao mDao;
	private AppLockAdapter mUnlockAdapter;
	private AppLockAdapter mLockAdapter;

	private boolean isAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);

		mDao = new AppLockDao(this);

		// 初始化view
		mSegementControlView = (SegementControlView) findViewById(R.id.al_scv);
		mTvTip = (TextView) findViewById(R.id.al_tv_tip);
		mLvUnlock = (ListView) findViewById(R.id.al_lv_unlock);
		mLvlock = (ListView) findViewById(R.id.al_lv_lock);
		mLloading = (LinearLayout) findViewById(R.id.css_loading);

		mSegementControlView.setOnSelectedListener(new OnSelectedListener() {

			@Override
			public void onSelected(boolean isLeftSelected) {

				if (isLeftSelected) {
					mTvTip.setText("未加锁");
					mLvUnlock.setVisibility(View.VISIBLE);
					mLvlock.setVisibility(View.GONE);
				} else {
					mTvTip.setText("已加锁");
					mLvUnlock.setVisibility(View.GONE);
					mLvlock.setVisibility(View.VISIBLE);
				}
			}
		});

		// List数据的加载
		new AsyncTask<Void, Void, Void>() {

			protected void onPreExecute() {
				// 进度条显示
				mLloading.setVisibility(View.VISIBLE);
			};

			@Override
			protected Void doInBackground(Void... params) {
				// 耗时操作
				List<AppInfo> allApps = AppInfoProvider
						.getAllApps(getApplicationContext());

				mUnlockDatas = new ArrayList<AppInfo>();
				mLockDatas = new ArrayList<AppInfo>();

				for (AppInfo info : allApps) {
					if (mDao.findLock(info.packageName)) {
						mLockDatas.add(info);
					} else {
						mUnlockDatas.add(info);
					}
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				// 进度条隐藏
				mLloading.setVisibility(View.GONE);
				mTvTip.setText("未加锁(" + mUnlockDatas.size() + ")");

				// 给listView设置数据

				mUnlockAdapter = new AppLockAdapter(false);
				mLvUnlock.setAdapter(mUnlockAdapter);// adapter --->
				// list<类型>

				mLockAdapter = new AppLockAdapter(true);
				mLvlock.setAdapter(mLockAdapter);
			};
		}.execute();

	}

	private class AppLockAdapter extends BaseAdapter {
		private boolean mLock;

		public AppLockAdapter(boolean lock) {
			this.mLock = lock;
		}

		@Override
		public int getCount() {

			if (mLock) {
				if (mLockDatas != null) {
					mTvTip.setText("已加锁(" + mLockDatas.size() + ")");
					return mLockDatas.size();
				} else {
					mTvTip.setText("已加锁(" + 0 + ")");
				}
			} else {
				if (mUnlockDatas != null) {
					mTvTip.setText("未加锁(" + mUnlockDatas.size() + ")");
					return mUnlockDatas.size();
				} else {
					mTvTip.setText("未加锁(" + 0 + ")");
				}
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mLock) {
				if (mLockDatas != null) {
					return mLockDatas.get(position);
				}
			} else {
				if (mUnlockDatas != null) {
					return mUnlockDatas.get(position);
				}
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				// 没有复用
				// 1.加载view
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_app_lock, null);
				// 2.初始化holder
				holder = new ViewHolder();
				// 3.设置标记
				convertView.setTag(holder);
				// 4.初始化holder中view
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_al_iv_icon);
				holder.ivLock = (ImageView) convertView
						.findViewById(R.id.item_al_iv_lock);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_al_tv_name);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 给view设置数据
			AppInfo info = null;
			if (mLock) {
				info = mLockDatas.get(position);
			} else {
				info = mUnlockDatas.get(position);
			}

			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);

			// 设置样式
			if (mLock) {
				// 显示解锁
				holder.ivLock.setImageResource(R.drawable.btn_unlock_selector);
			} else {
				// 显示加锁
				holder.ivLock.setImageResource(R.drawable.btn_lock_selector);
			}

			final AppInfo app = info;
			final View view = convertView;
			holder.ivLock.setTag(app);
			holder.ivLock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (isAnimation) {
						return;
					}

					if (mLock) {
						// 解锁

						TranslateAnimation ta = new TranslateAnimation(
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, -1,
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 0);
						ta.setDuration(200);
						ta.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								isAnimation = true;
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {

								// 从数据库中移除
								// if (mDao.findLock(app.packageName)) {

								AppInfo app = (AppInfo) ((ViewHolder) view
										.getTag()).ivLock.getTag();

								if (mDao.delete(app.packageName)) {
									// 已加锁的集合 ---》 未加锁的集合
									mLockDatas.remove(app);
									mUnlockDatas.add(app);

									mUnlockAdapter.notifyDataSetChanged();
									mLockAdapter.notifyDataSetChanged();

								} else {
									Toast.makeText(getApplicationContext(),
											"解锁失败", Toast.LENGTH_SHORT).show();
								}
								isAnimation = false;
								// }
							}
						});
						view.startAnimation(ta);
					} else {
						// 加锁
						// 未加锁的集合 ---》 已加锁的集合
						// 位移动画--》从左往右
						TranslateAnimation ta = new TranslateAnimation(
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 1,
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 0);
						ta.setDuration(200);
						ta.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								isAnimation = true;
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {

								AppInfo app = (AppInfo) ((ViewHolder) view
										.getTag()).ivLock.getTag();
								// if (!mDao.findLock(app.packageName)) {
								if (mDao.add(app.packageName)) {
									mUnlockDatas.remove(app);
									mLockDatas.add(app);

									mUnlockAdapter.notifyDataSetChanged();
									mLockAdapter.notifyDataSetChanged();
								} else {
									Toast.makeText(getApplicationContext(),
											"加锁失败", Toast.LENGTH_SHORT).show();
								}
								isAnimation = false;
								// }
							}
						});
						view.startAnimation(ta);

					}
				}
			});

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		ImageView ivLock;
		TextView tvName;
	}
}
