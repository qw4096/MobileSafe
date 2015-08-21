package com.itgold.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.bean.ProcessInfo;
import com.itgold.mobilesafe.engine.ProcessProvider;
import com.itgold.mobilesafe.service.AutoCleanService;
import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.PreferenceUtils;
import com.itgold.mobilesafe.utils.ServiceStateUtils;
import com.itgold.mobilesafe.view.ProgressDesView;
import com.itgold.mobilesafe.view.SettingItemView;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

public class ProcessManagerActivity extends Activity {

	private ProgressDesView mPdvProcess;
	private ProgressDesView mPdvMemory;
	private StickyListHeadersListView mListView;
	private LinearLayout mLloading;

	private ImageView mIvArrow1;
	private ImageView mIvArrow2;
	private SlidingDrawer mDrawer;

	private SettingItemView mSivShowSystem;
	private SettingItemView mSivAutoClean;
	private boolean showSystem = true;

	private List<ProcessInfo> mDatas;
	private List<ProcessInfo> mUserDatas;
	private List<ProcessInfo> mSystemDatas;

	private ProcessAdapter mAdapter;
	private int mRunningProcessCount;
	private int mTotalProcessCount;
	private long mFreeMemory;
	private long mTotalMemory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		// 初始化view
		mPdvProcess = (ProgressDesView) findViewById(R.id.pm_pdv_process);
		mPdvMemory = (ProgressDesView) findViewById(R.id.pm_pdv_memory);
		mListView = (StickyListHeadersListView) findViewById(R.id.pm_listview);
		mLloading = (LinearLayout) findViewById(R.id.css_loading);
		mIvArrow1 = (ImageView) findViewById(R.id.pm_drawer_arrow1);
		mIvArrow2 = (ImageView) findViewById(R.id.pm_drawer_arrow2);
		mDrawer = (SlidingDrawer) findViewById(R.id.pm_drawer);
		mSivShowSystem = (SettingItemView) findViewById(R.id.pm_siv_showsystem);
		mSivAutoClean = (SettingItemView) findViewById(R.id.pm_siv_autoclean);

		// 设置listView的item点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ProcessInfo info = mDatas.get(position);
				if (info.packageName.equals(getPackageName())) {
					return;
				}

				// 如果选中了，就取消
				info.checked = !info.checked;

				// UI更新
				mAdapter.notifyDataSetChanged();
			}
		});

		// 让箭头做动画up
		showUPAnimation();

		// 设置抽屉的监听
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				// 抽屉打开的回调
				showDownArrow();
			}
		});

		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				showUPAnimation();
			}
		});

		// 设置系统进程 开关
		showSystem = PreferenceUtils.getBoolean(getApplicationContext(),
				Constants.SHOW_SYSTEM_PROCESS, true);
		mSivShowSystem.setToggleOn(showSystem);

		// 设置显示系统进程的点击事件
		mSivShowSystem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean flag = PreferenceUtils.getBoolean(
						getApplicationContext(), Constants.SHOW_SYSTEM_PROCESS,
						true);
				// UI更新
				mSivShowSystem.setToggleOn(!flag);

				showSystem = !flag;
				mAdapter.notifyDataSetChanged();
				// 数据更新
				PreferenceUtils.putBoolean(getApplicationContext(),
						Constants.SHOW_SYSTEM_PROCESS, !flag);
			}
		});

		// 设置锁屏自动清理的点击事件
		mSivAutoClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果服务是开启就关闭
				boolean runging = ServiceStateUtils.isRunging(
						getApplicationContext(), AutoCleanService.class);
				if (runging) {
					stopService(new Intent(getApplicationContext(),
							AutoCleanService.class));
				} else {
					startService(new Intent(getApplicationContext(),
							AutoCleanService.class));
				}

				// UI操作
				mSivAutoClean.setToggleOn(!runging);
			}
		});
	}

	private void startQuery() {
		mLloading.setVisibility(View.VISIBLE);
		// 开线程加载
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 数据加载
				mDatas = ProcessProvider
						.getAllRunningProcesses(getApplicationContext());

				mSystemDatas = new ArrayList<ProcessInfo>();
				mUserDatas = new ArrayList<ProcessInfo>();
				for (ProcessInfo info : mDatas) {
					if (info.isSystem) {
						mSystemDatas.add(info);
					} else {
						mUserDatas.add(info);
					}
				}

				mDatas.clear();
				mDatas.addAll(mUserDatas);
				mDatas.addAll(mSystemDatas);

				// 主线程中设置adapter
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mLloading.setVisibility(View.GONE);

						// 给listView加载数据
						mAdapter = new ProcessAdapter();
						mListView.setAdapter(mAdapter);// adapter
														// -->list<类型>
						// --->Ui展示
					}
				});
			}
		}).start();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// 初始化服务状态
		boolean runging = ServiceStateUtils.isRunging(getApplicationContext(),
				AutoCleanService.class);
		mSivAutoClean.setToggleOn(runging);

		// 进程
		// 1. 正在运行的进程数，总的应用数
		mRunningProcessCount = ProcessProvider.getRunningProcessCount(this);
		mTotalProcessCount = ProcessProvider.getTotalProcessCount(this);
		initProcessUI();

		// 内存
		// 1. 已经使用的内存,剩余的内存
		mFreeMemory = ProcessProvider.getFreeMemory(this);
		mTotalMemory = ProcessProvider.getTotalMemory(this);
		initMemoryUI();

		startQuery();
	}

	private void showDownArrow() {
		// 清除动画
		mIvArrow1.clearAnimation();
		mIvArrow2.clearAnimation();

		mIvArrow1.setImageResource(R.drawable.drawer_arrow_down);
		mIvArrow2.setImageResource(R.drawable.drawer_arrow_down);
	}

	private void showUPAnimation() {
		mIvArrow1.setImageResource(R.drawable.drawer_arrow_up);
		mIvArrow2.setImageResource(R.drawable.drawer_arrow_up);

		AlphaAnimation alpha1 = new AlphaAnimation(0.2f, 1f);
		alpha1.setDuration(600);
		alpha1.setRepeatCount(AlphaAnimation.INFINITE);
		alpha1.setRepeatMode(AlphaAnimation.REVERSE);
		mIvArrow1.startAnimation(alpha1);

		AlphaAnimation alpha2 = new AlphaAnimation(1f, 0.2f);
		alpha2.setDuration(600);
		alpha2.setRepeatCount(AlphaAnimation.INFINITE);
		alpha2.setRepeatMode(AlphaAnimation.REVERSE);
		mIvArrow2.startAnimation(alpha2);
	}

	private void initMemoryUI() {
		long usedMemory = mTotalMemory - mFreeMemory;

		mPdvMemory.setDesTitle("内存:");
		mPdvMemory.setDesLeft("占用内存:"
				+ Formatter.formatFileSize(this, usedMemory));
		mPdvMemory.setDesRight("可用内存:"
				+ Formatter.formatFileSize(this, mFreeMemory));
		mPdvMemory
				.setDesProgress((int) (usedMemory * 100f / mTotalMemory + 0.5f));
	}

	private void initProcessUI() {

		mPdvProcess.setDesTitle("进程数:");
		mPdvProcess.setDesLeft("正在运行" + mRunningProcessCount + "个");
		mPdvProcess.setDesRight("可有进程" + mTotalProcessCount + "个");
		mPdvProcess.setDesProgress((int) (mRunningProcessCount * 100f
				/ mTotalProcessCount + 0.5f));
	}

	public void clickAll(View view) {
		if (mDatas == null) {
			return;
		}

		if (showSystem) {
			for (ProcessInfo info : mDatas) {
				if (info.packageName.equals(getPackageName())) {
					continue;
				}
				info.checked = true;
			}
		} else {
			for (ProcessInfo info : mUserDatas) {
				if (info.packageName.equals(getPackageName())) {
					continue;
				}
				info.checked = true;
			}
		}

		// UI更新
		mAdapter.notifyDataSetChanged();
	}

	public void clickReverse(View view) {
		if (mDatas == null) {
			return;
		}
		if (showSystem) {
			for (ProcessInfo info : mDatas) {
				if (info.packageName.equals(getPackageName())) {
					continue;
				}
				info.checked = !info.checked;
			}
		} else {
			for (ProcessInfo info : mUserDatas) {
				if (info.packageName.equals(getPackageName())) {
					continue;
				}
				info.checked = !info.checked;
			}
		}

		// UI更新
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 清理
	 * 
	 * @param view
	 */
	public void clickClean(View view) {
		// 找到所有选中的进程,杀死

		if (mDatas == null) {
			return;
		}

		// for (ProcessInfo info : mDatas) {
		// if (info.checked) {
		// // 选中的，杀死
		// ProcessProvider.killProcess(this, info.packageName);
		//
		// mDatas.remove(info);
		// }
		// }

		int processCount = 0;
		long freeMemory = 0;

		ListIterator<ProcessInfo> iterator = null;
		if (showSystem) {
			iterator = mDatas.listIterator();
		} else {
			iterator = mUserDatas.listIterator();
		}
		while (iterator.hasNext()) {
			ProcessInfo info = iterator.next();
			if (info.checked) {
				// 杀死
				ProcessProvider.killProcess(this, info.packageName);
				// 移除
				iterator.remove();

				if (showSystem) {
					mUserDatas.remove(info);
				} else {
					mDatas.remove(info);
				}

				processCount++;
				freeMemory += info.memory;
			}
		}

		if (processCount != 0) {
			// 清理的进程数，释放的内存
			Toast.makeText(
					this,
					"结束了" + processCount + "进程，释放了"
							+ Formatter.formatFileSize(this, freeMemory) + "内存",
					Toast.LENGTH_SHORT).show();

			// 进程
			mRunningProcessCount -= processCount;
			// 内存
			mFreeMemory += freeMemory;

			initMemoryUI();
			initProcessUI();
		}

		// UI更新
		mAdapter.notifyDataSetChanged();
	}

	private class ProcessAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			if (showSystem) {
				if (mDatas != null) {
					return mDatas.size();
				}
			} else {
				if (mUserDatas != null) {
					return mUserDatas.size();
				}
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (showSystem) {
				if (mDatas != null) {
					return mDatas.get(position);
				}
			} else {
				if (mUserDatas != null) {
					return mUserDatas.get(position);
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
				// 1. 加载view
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_process, null);
				// 2.初始化holder
				holder = new ViewHolder();
				// 3.设置标记
				convertView.setTag(holder);
				// 4.给holder找view
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_process_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_process_tv_name);
				holder.tvMemory = (TextView) convertView
						.findViewById(R.id.item_process_tv_memory);
				holder.cbChoice = (CheckBox) convertView
						.findViewById(R.id.item_process_cb_choice);
			} else {
				// 复用
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置数据
			ProcessInfo info = null;

			if (showSystem) {
				info = mDatas.get(position);
			} else {
				info = mUserDatas.get(position);
			}

			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvMemory.setText("占用内存:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.memory));

			// 设置是否选中
			holder.cbChoice.setChecked(info.checked);

			if (info.packageName.equals(getPackageName())) {
				holder.cbChoice.setVisibility(View.GONE);
			} else {
				holder.cbChoice.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				convertView = new TextView(getApplicationContext());
				tv = (TextView) convertView;

				tv.setPadding(4, 4, 4, 4);
				tv.setBackgroundColor(Color.parseColor("#33000000"));
				tv.setTextSize(15);
				tv.setTextColor(Color.BLACK);
			} else {
				tv = (TextView) convertView;
			}

			ProcessInfo info = null;
			if (showSystem) {
				info = mDatas.get(position);
			} else {
				info = mUserDatas.get(position);
			}
			boolean isSystem = info.isSystem;

			tv.setText(isSystem ? "系统进程(" + mSystemDatas.size() + "个)"
					: "用户进程(" + mUserDatas.size() + "个)");

			return convertView;
		}

		// 获取唯一的标记
		@Override
		public long getHeaderId(int position) {
			ProcessInfo info = null;
			if (showSystem) {
				info = mDatas.get(position);
			} else {
				info = mUserDatas.get(position);
			}

			return info.isSystem ? 0 : 1;
			// return position;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvMemory;
		CheckBox cbChoice;
	}

}
