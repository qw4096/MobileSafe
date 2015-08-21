package com.itgold.mobilesafe.activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.itgold.mobilesafe.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;

import com.itgold.mobilesafe.bean.TrafficInfo;
import com.itgold.mobilesafe.utils.Logger;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TrafficActiviy extends Activity {

	private static final String TAG = "TrafficActiviy";
	private PackageManager mPm;

	private List<TrafficInfo> mDatas;

	private ListView mListView;
	private LinearLayout mLloading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_traffic);

		mListView = (ListView) findViewById(R.id.ta_listview);
		mLloading = (LinearLayout) findViewById(R.id.css_loading);

		// 读取proc/uid_stat/用户id/tcp_rcv 接收的
		// 读取proc/uid_stat/用户id/tcp_snd 发送的

		mPm = getPackageManager();

		new AsyncTask<Void, Void, Void>() {
			protected void onPreExecute() {
				mLloading.setVisibility(View.VISIBLE);

			};

			@Override
			protected Void doInBackground(Void... params) {
				List<PackageInfo> packages = mPm.getInstalledPackages(0);

				mDatas = new ArrayList<TrafficInfo>();
				for (PackageInfo pack : packages) {
					// 用户id
					int uid = pack.applicationInfo.uid;
					String packageName = pack.packageName;
					String name = pack.applicationInfo.loadLabel(mPm)
							.toString();
					Drawable icon = pack.applicationInfo.loadIcon(mPm);
					long rcv = getRcv(uid);
					long snd = getSnd(uid);

					if (rcv != 0 && snd != 0) {
						TrafficInfo info = new TrafficInfo();
						info.uid = uid;
						info.packageName = packageName;
						info.name = name;
						info.icon = icon;
						info.rcv = rcv;
						info.snd = snd;

						mDatas.add(info);
					}

					Logger.d(TAG, "uid : " + uid);
					Logger.d(TAG, "packageName : " + packageName);
					Logger.d(TAG, "name : " + name);
					Logger.d(TAG, "--------------------------------------------- ");
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				mLloading.setVisibility(View.GONE);

				// 给listView设置adapter
				mListView.setAdapter(new TrafficAdapter());

			};
		}.execute();

	}

	private final class TrafficAdapter extends BaseAdapter {

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
				// 加载view
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_traffic, null);
				// holder
				holder = new ViewHolder();
				// tag
				convertView.setTag(holder);
				// findview
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_traffic_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_name);
				holder.tvRcv = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_rcv);
				holder.tvSnd = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_snd);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置数据
			TrafficInfo info = mDatas.get(position);
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvRcv.setText("接收:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.rcv));
			holder.tvSnd.setText("发送:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.snd));

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvRcv;
		TextView tvSnd;
	}

	/**
	 * 获取接收的数据大小
	 * 
	 * @param uid
	 * @return
	 */
	private long getRcv(int uid) {
		// 读取proc/uid_stat/用户id/tcp_rcv 接收的
		String fileName = "/proc/uid_stat/" + uid + "/tcp_rcv";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String readLine = reader.readLine();

			return Long.valueOf(readLine);
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				reader = null;
			}
		}
		return 0;
	}

	private long getSnd(int uid) {
		// 读取proc/uid_stat/用户id/tcp_snd 发送的
		String fileName = "/proc/uid_stat/" + uid + "/tcp_snd";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String readLine = reader.readLine();

			return Long.valueOf(readLine);
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				reader = null;
			}
		}
		return 0;
	}
}
