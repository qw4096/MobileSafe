package com.itgold.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.itgold.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;

import com.itgold.mobilesafe.bean.CacheInfo;
import com.itgold.mobilesafe.utils.Logger;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CleanCacheActivity extends Activity implements OnClickListener {

	protected static final String TAG = "CleanCacheActivity";
	private List<CacheInfo> mDatas;
	private PackageManager mPm;

	private ImageView mIvScanIcon;// 扫描的图标
	private ImageView mIvScanLine;// 扫描的线
	private TextView mTvScanName;// 扫描的 应用名称
	private TextView mTvScanCache;// 扫描的 缓存
	private ProgressBar mPbProgress;// 扫描的进度

	private RelativeLayout mRlContainerScan;
	private RelativeLayout mRlContainerResult;
	private TextView mTvResult;

	private ListView mListView;
	private CleanCacheAdapter mAapter;

	private ScanTask mTask;

	private int mAppCacheCount;
	private long mTotalCacheSize;

	private Button mBtnClearAll;

	// private boolean isFinish;// 标记activity是否结束

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);

		// 初始化view
		mIvScanIcon = (ImageView) findViewById(R.id.cc_iv_scan_icon);
		mIvScanLine = (ImageView) findViewById(R.id.cc_iv_scan_line);
		mTvScanName = (TextView) findViewById(R.id.cc_tv_scan_name);
		mTvScanCache = (TextView) findViewById(R.id.cc_tv_scan_cache);
		mPbProgress = (ProgressBar) findViewById(R.id.cc_pb_scan_progress);
		mListView = (ListView) findViewById(R.id.cc_listview);

		mRlContainerScan = (RelativeLayout) findViewById(R.id.cc_scan_container);
		mRlContainerResult = (RelativeLayout) findViewById(R.id.cc_result_container);
		mTvResult = (TextView) findViewById(R.id.cc_tv_result);

		mBtnClearAll = (Button) findViewById(R.id.cc_btn_clearall);
		mBtnClearAll.setOnClickListener(this);

		mPm = getPackageManager();

		// 开始扫描
		startScan();
	}

	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// if (mTask != null) {
	// mTask.stop();
	// mTask = null;
	// }
	// }

	@Override
	protected void onPause() {
		super.onPause();
		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
	}

	public void startScan(View view) {
		// 开始扫描
		startScan();
	}

	private void startScan() {
		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}

		mTask = new ScanTask();
		mTask.execute();
	}

	private final class ScanTask extends AsyncTask<Void, CacheInfo, Void> {
		private int progress;
		private int max;
		private boolean isFinish;

		protected void onPreExecute() {

			// 开启前
			// 扫描线 扫描线在动
			TranslateAnimation animation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 1);
			animation.setDuration(1000);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setRepeatMode(Animation.REVERSE);
			mIvScanLine.startAnimation(animation);

			mRlContainerScan.setVisibility(View.VISIBLE);
			mRlContainerResult.setVisibility(View.GONE);

			// 清理数据
			mAppCacheCount = 0;
			mTotalCacheSize = 0;

			// 禁用一键清理
			mBtnClearAll.setEnabled(false);
		};

		@Override
		protected Void doInBackground(Void... params) {
			// 1.读取所有数据的信息
			mDatas = new ArrayList<CacheInfo>();
			List<PackageInfo> packages = mPm.getInstalledPackages(0);

			max = packages.size();

			for (PackageInfo pack : packages) {
				progress++;
				if (isFinish) {
					break;
				}

				// 设置监听
				try {
					Method method = mPm.getClass().getDeclaredMethod(
							"getPackageSizeInfo", String.class,
							IPackageStatsObserver.class);

					method.invoke(mPm, pack.packageName, mStatsObserver);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			return null;
		}

		// 更新进度
		public void updateProgress(CacheInfo... progress) {
			publishProgress(progress);
		}

		@Override
		protected void onProgressUpdate(CacheInfo... values) {
			super.onProgressUpdate(values);

			if (isFinish) {
				return;
			}

			// 2. 图标变化
			mIvScanIcon.setImageDrawable(values[0].icon);

			// 3. 应用名称
			mTvScanName.setText(values[0].name);
			// 4. 进度条
			mPbProgress.setMax(max);
			mPbProgress.setProgress(progress);
			// 5. 应用的缓存数据大小
			mTvScanCache.setText("缓存大小:"
					+ Formatter.formatFileSize(getApplicationContext(),
							values[0].cacheSize));

			// 更新时
			if (mDatas.size() == 1) {
				// 设置adapter

				mAapter = new CleanCacheAdapter();
				mListView.setAdapter(mAapter);
			} else {
				Logger.d(TAG, "xxxx : " + Thread.currentThread().getName());
				// adapter更新
				if (mAapter != null) {
					mAapter.notifyDataSetChanged();
				}
			}

			// 滚动到底部
			mListView.smoothScrollToPosition(mAapter.getCount());
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (isFinish) {
				return;
			}

			// listView滚动到顶部
			mListView.smoothScrollToPosition(0);

			mRlContainerScan.setVisibility(View.GONE);
			mRlContainerResult.setVisibility(View.VISIBLE);

			mTvResult.setText("总共有"
					+ mAppCacheCount
					+ "有缓存，共"
					+ Formatter.formatFileSize(getApplicationContext(),
							mTotalCacheSize));

			// 让一键清理可用
			mBtnClearAll.setEnabled(true);
		}

		public void stop() {
			isFinish = true;
		}

	}

	private class CleanCacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
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
						R.layout.item_cache_info, null);
				// 2.创建holder
				holder = new ViewHolder();
				// 3.设置标记
				convertView.setTag(holder);
				// 4。初始化view
				holder.ivClean = (ImageView) convertView
						.findViewById(R.id.item_cacheinfo_iv_clean);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_cacheinfo_iv_icon);
				holder.tvCache = (TextView) convertView
						.findViewById(R.id.item_cacheinfo_tv_cache);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_cacheinfo_tv_name);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final CacheInfo info = mDatas.get(position);
			// 设置数据
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvCache.setText("缓存大小:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.cacheSize));
			holder.ivClean.setVisibility(info.cacheSize > 0 ? View.VISIBLE
					: View.GONE);

			holder.ivClean.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// mPm.deleteApplicationCacheFiles(packageName,
					// mClearCacheObserver);
					// public abstract void deleteApplicationCacheFiles(String
					// packageName,
					// IPackageDataObserver observer);

					// try {
					// Method method = mPm.getClass().getDeclaredMethod(
					// "deleteApplicationCacheFiles", String.class,
					// IPackageDataObserver.class);
					//
					// method.invoke(mPm, info.packageName,
					// new ClearCacheObserver());
					//
					// } catch (Exception e) {
					// e.printStackTrace();
					// }

					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					intent.setData(Uri.parse("package:" + info.packageName));
					startActivity(intent);

				}
			});

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		ImageView ivClean;
		TextView tvName;
		TextView tvCache;
	}

	final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
		public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
			Logger.d(TAG, "线程 :" + Thread.currentThread().getName());

			long cacheSize = stats.cacheSize;
			String packageName = stats.packageName;

			CacheInfo info = new CacheInfo();

			ApplicationInfo applicationInfo;
			try {
				applicationInfo = mPm.getApplicationInfo(packageName, 0);
				info.icon = applicationInfo.loadIcon(mPm);
				info.name = applicationInfo.loadLabel(mPm).toString();
				info.cacheSize = cacheSize;
				info.packageName = packageName;

				if (info.cacheSize > 0) {
					mDatas.add(0, info);

					mAppCacheCount++;
					mTotalCacheSize += info.cacheSize;
				} else {
					mDatas.add(info);
				}

				// 推出进度的 信息
				mTask.updateProgress(info);

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			Logger.d(TAG, "package:" + packageName);
			Logger.d(TAG,
					"cacheSize:"
							+ Formatter.formatFileSize(CleanCacheActivity.this,
									cacheSize));
			Logger.d(TAG, "------------------------------");

		}
	};

	final class ClearCacheObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(final String packageName,
				final boolean succeeded) {
			// final Message msg = mHandler.obtainMessage(CLEAR_CACHE);
			// msg.arg1 = succeeded ? OP_SUCCESSFUL:OP_FAILED;
			// mHandler.sendMessage(msg);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "删除成功",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnClearAll) {
			// 清除所有缓存
			// public abstract void freeStorageAndNotify(long freeStorageSize,
			// IPackageDataObserver observer);

			if (mTotalCacheSize <= 0) {
				return;
			}

			try {
				Method method = mPm.getClass().getDeclaredMethod(
						"freeStorageAndNotify", long.class,
						IPackageDataObserver.class);

				method.invoke(mPm, Long.MAX_VALUE, new ClearCacheObserver());

				// 重新扫描
				startScan();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
