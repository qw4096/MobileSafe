package com.itgold.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.itgold.mobilesafe.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;

import com.itgold.mobilesafe.bean.AppInfo;
import com.itgold.mobilesafe.engine.AppInfoProvider;
import com.itgold.mobilesafe.utils.Logger;
import com.itgold.mobilesafe.view.ProgressDesView;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends Activity {

	public static final String TAG = "AppManagerActivity";
	private ProgressDesView mPdvRom;
	private ProgressDesView mPdvSD;
	private ListView mListView;
	private LinearLayout mLlLoding;
	private TextView mTvHeader;

	private List<AppInfo> mDatas;
	private List<AppInfo> mSystemDatas;
	private List<AppInfo> mUserDatas;
	private AppAdapter mAdapter;

	private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG, "接收到卸载广播");
			String dataString = intent.getDataString();
			Logger.d(TAG, "卸载了:" + dataString);
			String packageName = dataString.replace("package:", "");

			// UI更新
			ListIterator<AppInfo> iterator = mUserDatas.listIterator();
			while (iterator.hasNext()) {
				AppInfo next = iterator.next();
				if (next.packageName.equals(packageName)) {
					// 移除
					iterator.remove();
					break;
				}
			}

			// adapter更新
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		// 初始化view
		mPdvRom = (ProgressDesView) findViewById(R.id.am_pdv_rom);
		mPdvSD = (ProgressDesView) findViewById(R.id.am_pdv_sd);
		mListView = (ListView) findViewById(R.id.am_listview);
		mLlLoding = (LinearLayout) findViewById(R.id.css_loading);
		mTvHeader = (TextView) findViewById(R.id.am_tv_header);

		// 注册package安装和卸载的广播
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mPackageReceiver, filter);

		// 设置数据
		// 1.内部存储设置数据
		File dataDirectory = Environment.getDataDirectory();
		long romFreeSpace = dataDirectory.getFreeSpace();// 剩余空间
		long romTotalSpace = dataDirectory.getTotalSpace();// 总间
		long romUsedSpace = romTotalSpace - romFreeSpace;// 已经使用的
		// left：已经使用多少

		mPdvRom.setDesTitle("内存:");
		mPdvRom.setDesLeft(Formatter.formatFileSize(this, romUsedSpace) + "已用");
		mPdvRom.setDesRight(Formatter.formatFileSize(this, romFreeSpace) + "可用");
		int romProgress = (int) (romUsedSpace * 100f / romTotalSpace + 0.5f);
		mPdvRom.setDesProgress(romProgress);

		// 2.给sd卡部分设置数据
		File sdDirctory = Environment.getExternalStorageDirectory();
		long sdFreeSpace = sdDirctory.getFreeSpace();// 剩余空间
		long sdTotalSpace = sdDirctory.getTotalSpace();// 总间
		long sdUsedSpace = sdTotalSpace - sdFreeSpace;// 已经使用的

		mPdvSD.setDesTitle("SD卡:");
		mPdvSD.setDesLeft(Formatter.formatFileSize(this, sdUsedSpace) + "已用");
		mPdvSD.setDesRight(Formatter.formatFileSize(this, sdFreeSpace) + "可用");
		int sdProgress = (int) (sdUsedSpace * 100f / sdTotalSpace + 0.5f);
		mPdvSD.setDesProgress(sdProgress);

		// 数据加载
		mLlLoding.setVisibility(View.VISIBLE);
		mTvHeader.setVisibility(View.GONE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 3. 加载数据--耗时
				mDatas = AppInfoProvider.getAllApps(getApplicationContext());
				mSystemDatas = new ArrayList<AppInfo>();
				mUserDatas = new ArrayList<AppInfo>();

				for (AppInfo info : mDatas) {
					if (info.isSystem) {
						// 系统程序
						mSystemDatas.add(info);
					} else {
						mUserDatas.add(info);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mLlLoding.setVisibility(View.GONE);
						mTvHeader.setVisibility(View.VISIBLE);

						// 给头布局设置数据
						mTvHeader.setText("用户程序(" + mUserDatas.size() + "个)");

						// 4. 给listView设置数据
						mAdapter = new AppAdapter();
						mListView.setAdapter(mAdapter);// apdapter -->
						// List<类型>--->UI
					}
				});
			}
		}).start();

		// 监听listView的滑动事件
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 滑动的过程中

				if (mUserDatas == null || mSystemDatas == null) {
					return;
				}

				int userSize = mUserDatas.size();

				// 如果 某个头是第一个可见,就显示为对应的头
				if (firstVisibleItem >= 0 && firstVisibleItem <= userSize) {
					// 第一个可见-->用户程序部分
					mTvHeader.setText("用户程序(" + mUserDatas.size() + "个)");
				} else if (firstVisibleItem >= userSize + 1) {
					mTvHeader.setText("系统程序(" + mSystemDatas.size() + "个)");
				}

			}
		});

		// 设置ListView的item的点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			private PopupWindow mWindow;
			private View contentView;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// 弹出层
				// 如果点击的是 头条目，不做响应
				if (position == 0) {
					// 点击的是用户程序的头
					return;
				}
				int userSize = mUserDatas.size();
				if (position == userSize + 1) {
					// 点击的是系统程序的头
					return;
				}

				// 获取点击的数据
				AppInfo info = null;
				if (position > 0 && position < userSize + 1) {
					// 用户程序
					info = mUserDatas.get(position - 1);
				} else {
					// 系统程序
					info = mSystemDatas.get(position - userSize - 2);
				}

				// 展示层
				// TextView contentView = new
				// TextView(getApplicationContext());// 展示的内容的view
				// contentView.setText("弹出的层");
				// contentView.setPadding(8, 8, 8, 8);
				// int[] colors = new int[] { Color.RED, Color.BLUE, Color.GREEN
				// };
				// Random rdm = new Random();
				// contentView.setBackgroundColor(colors[rdm
				// .nextInt(colors.length)]);

				// 初始化
				if (mWindow == null) {
					contentView = View.inflate(getApplicationContext(),
							R.layout.popup_item_app, null);

					// 展示view的宽度和高度
					int width = LayoutParams.WRAP_CONTENT;
					int height = LayoutParams.WRAP_CONTENT;

					mWindow = new PopupWindow(contentView, width, height);

					// 焦点
					mWindow.setFocusable(true);

					// 设置其他位置可触摸
					mWindow.setOutsideTouchable(true);
					mWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));

					// 设置动画
					mWindow.setAnimationStyle(R.style.PopAnimation);
				}

				final AppInfo app = info;
				contentView.findViewById(R.id.pop_ll_uninstall)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 卸载

								// <action
								// android:name="android.intent.action.DELETE"
								// />
								// <category
								// android:name="android.intent.category.DEFAULT"
								// />
								// <data android:scheme="package" />

								Intent intent = new Intent();
								intent.setAction("android.intent.action.DELETE");
								intent.addCategory("android.intent.category.DEFAULT");
								intent.setData(Uri.parse("package:"
										+ app.packageName));
								startActivity(intent);

								// popup隐藏
								mWindow.dismiss();

							}
						});

				contentView.findViewById(R.id.pop_ll_share).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 分享
								// <action
								// android:name="android.intent.action.SEND" />
								// <category
								// android:name="android.intent.category.DEFAULT"
								// />
								// <data android:mimeType="text/plain" />

								// Intent intent = new Intent();
								// intent.setAction("android.intent.action.SEND");
								// intent.addCategory("android.intent.category.DEFAULT");
								// intent.setDataAndType(
								// Uri.parse(""),
								// "text/plain");
								// startActivity(intent);

								Intent smsIntent = new Intent(
										android.content.Intent.ACTION_VIEW);
								smsIntent.setType("vnd.android-dir/mms-sms");
								// smsIntent.putExtra("address", "phoneNumber");
								smsIntent.putExtra("sms_body", "分享某某软件");
								startActivity(smsIntent);

								// popup隐藏
								mWindow.dismiss();
							}
						});

				contentView.findViewById(R.id.pop_ll_open).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 打开
								PackageManager pm = getPackageManager();
								Intent intent = pm
										.getLaunchIntentForPackage(app.packageName);

								if (intent != null) {
									startActivity(intent);
								}

								// popup隐藏
								mWindow.dismiss();
							}
						});
				contentView.findViewById(R.id.pop_ll_info).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 信息详情

								/*
								 * <action android:name=
								 * "android.settings.APPLICATION_DETAILS_SETTINGS"
								 * /> <category
								 * android:name="android.intent.category.DEFAULT"
								 * /> <data android:scheme="package" />
								 */

								Intent intent = new Intent();
								intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
								intent.addCategory("android.intent.category.DEFAULT");
								intent.setData(Uri.parse("package:"
										+ app.packageName));
								startActivity(intent);

								// popup隐藏
								mWindow.dismiss();
							}
						});

				// 显示
				// 在anchor控件的下方显示
				// window.showAsDropDown(view);
				mWindow.showAsDropDown(view, 60, -view.getHeight());
				// window.showAtLocation(view, Gravity.LEFT | Gravity.TOP, 0,
				// 0);

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mPackageReceiver);
	}

	private class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// if (mDatas != null) {
			// return mDatas.size();
			// }
			int systemCount = 0;
			if (mSystemDatas != null) {
				systemCount = mSystemDatas.size();
				systemCount += 1;
			}

			int userCount = 0;
			if (mUserDatas != null) {
				userCount = mUserDatas.size();
				userCount += 1;
			}
			return systemCount + userCount;
		}

		@Override
		public Object getItem(int position) {
			// if (mDatas != null) {
			// return mDatas.get(position);
			// }
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int userSize = mUserDatas.size();

			// 用户部分
			if (position == 0) {
				// 显示用户部分的头
				TextView tv = new TextView(getApplicationContext());

				tv.setPadding(4, 4, 4, 4);
				tv.setBackgroundColor(Color.parseColor("#ffcccccc"));
				tv.setTextColor(Color.BLACK);

				tv.setText("用户程序(" + userSize + "个)");

				return tv;
			}

			// 系统程序的头部分
			int systemSize = mSystemDatas.size();
			if (position == userSize + 1) {
				TextView tv = new TextView(getApplicationContext());

				tv.setPadding(4, 4, 4, 4);
				tv.setBackgroundColor(Color.parseColor("#ffcccccc"));
				tv.setTextColor(Color.BLACK);

				tv.setText("系统程序(" + systemSize + "个)");

				return tv;
			}

			ViewHolder holder = null;
			if (convertView == null || (convertView instanceof TextView)) {// --->TextView
				// 没有复用
				// 1.初始化view
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_app_info, null);
				// 2. 初始化holder
				holder = new ViewHolder();
				// 3. 设置标记
				convertView.setTag(holder);
				// 4. 初始化holder的view
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_appinfo_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_appinfo_tv_name);
				holder.tvInstallPath = (TextView) convertView
						.findViewById(R.id.item_appinfo_tv_install);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.item_appinfo_tv_size);
			} else {
				// 有复用
				holder = (ViewHolder) convertView.getTag();
			}

			AppInfo info = null;
			if (position < userSize + 1) {
				// 显示的是用户程序部分
				info = mUserDatas.get(position - 1);

				Logger.d(TAG, "用户部分：" + (position - 1));
			} else {
				// 显示系统程序部分
				info = mSystemDatas.get(position - userSize - 2);

				Logger.d(TAG, "系统部分：" + (position - userSize - 2));
			}

			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvInstallPath.setText(info.isInstallSD ? "SD卡安装" : "手机内存");
			holder.tvName.setText(info.name);
			holder.tvSize.setText(Formatter.formatFileSize(
					getApplicationContext(), info.size));

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvInstallPath;
		TextView tvSize;
	}
}
