package com.itgold.mobilesafe.activity;

import java.util.List;

import com.itgold.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.itgold.mobilesafe.bean.BlackInfo;
import com.itgold.mobilesafe.db.BlackDao;
import com.itgold.mobilesafe.utils.Logger;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity implements
		OnItemClickListener {

	private static final String TAG = "CallSmsSafeActivity";
	protected static final int REQUEST_CODE_ADD = 100;
	private static final int REQUEST_CODE_UPDATE = 101;

	private static final int PAGE_SIZE = 10;

	private ImageView mIvAdd;
	private ListView mListView;
	private LinearLayout mLlLoading;
	private ImageView mIvEmpty;

	private List<BlackInfo> mDatas;

	private BlackDao mDao;
	private CallSmsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsms_safe);

		mDao = new BlackDao(this);

		// 初始化view
		mIvAdd = (ImageView) findViewById(R.id.css_iv_add);
		mListView = (ListView) findViewById(R.id.css_lv);
		mLlLoading = (LinearLayout) findViewById(R.id.css_loading);
		mIvEmpty = (ImageView) findViewById(R.id.css_iv_empty);

		mIvAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CallSmsSafeActivity.this,
						BlackEditActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ADD);
			}
		});

		// 给listView item设置点击事件
		mListView.setOnItemClickListener(this);

		startQuery();

		// 监听listView滑动
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 滑动状态改变时的回调
				// scrollState:当前的状态
				// SCROLL_STATE_IDLE : 闲置空闲状态
				// SCROLL_STATE_TOUCH_SCROLL: 触摸滚动状态
				// SCROLL_STATE_FLING: 惯性滑动状态

				// Logger.d(TAG, "state : " + scrollState);

				// 空闲 && 要看到最后一个
				// 当前可以看到的最后一个item的下标
				int lastVisiblePosition = mListView.getLastVisiblePosition();
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastVisiblePosition == (mDatas.size() - 1)) {

					// 加载数据
					mLlLoading.setVisibility(View.VISIBLE);
					new Thread() {
						public void run() {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							int index = mDatas.size();
							List<BlackInfo> list = mDao.findPart(PAGE_SIZE,
									index);

							if (list.size() == 0) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										mLlLoading.setVisibility(View.GONE);
										Toast.makeText(getApplicationContext(),
												"没有更多数据", Toast.LENGTH_SHORT)
												.show();
									}
								});
								return;
							}

							// 添加到当前的list中
							mDatas.addAll(list);

							runOnUiThread(new Runnable() {
								public void run() {
									mLlLoading.setVisibility(View.GONE);

									// UI更新
									mAdapter.notifyDataSetChanged();
								}
							});
						};
					}.start();

				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 正在滑动时的回调
				// firstVisibleItem:第一个可见的item的position
				// visibleItemCount:可见的数量
				// totalItemCount:--List

				Logger.d(TAG, "onScroll:" + firstVisibleItem + " = "
						+ visibleItemCount);
			}
		});
	}

	private void startQuery() {
		mLlLoading.setVisibility(View.VISIBLE);
		// 开线程去查询数据
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 接口访问--》net
				// 初始化List数据
				// mDatas = mDao.findAll();
				mDatas = mDao.findPart(PAGE_SIZE, 0);

				// 主线程中设置adapter

				runOnUiThread(new Runnable() {
					public void run() {
						mLlLoading.setVisibility(View.GONE);

						// 给listView设置数据
						mAdapter = new CallSmsAdapter();
						mListView.setAdapter(mAdapter);// --->adapter--->List<类型>---》显示

						// 设置空的view
						mListView.setEmptyView(mIvEmpty);

					}
				});
			}
		}.start();
	}

	private void startQuery(final int pageSize, final int offset) {
		mLlLoading.setVisibility(View.VISIBLE);
		// 开线程去查询数据
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 接口访问--》net
				// 初始化List数据
				// mDatas = mDao.findAll();
				final List<BlackInfo> part = mDao.findPart(pageSize, offset);

				// 主线程中设置adapter

				runOnUiThread(new Runnable() {
					public void run() {
						mLlLoading.setVisibility(View.GONE);

						if (part != null) {

							mDatas.addAll(part);
							mAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE_ADD) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 1.获得 添加的数据
				String number = data
						.getStringExtra(BlackEditActivity.EXTRA_NUMBER);
				int type = data.getIntExtra(BlackEditActivity.EXTRA_TYPE, -1);
				BlackInfo info = new BlackInfo();
				info.number = number;
				info.type = type;
				// 2.把数据添加到list中
				mDatas.add(info);
				// 3.adapter去更新
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		} else if (requestCode == REQUEST_CODE_UPDATE) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 1. type--类型
				int type = data.getIntExtra(BlackEditActivity.EXTRA_TYPE, -1);
				int poistion = data.getIntExtra(
						BlackEditActivity.EXTRA_POSITION, -1);

				// mDatas 某一条记录 的type
				mDatas.get(poistion).type = type;

				// 2. adapter更新
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	}

	private class CallSmsAdapter extends BaseAdapter {

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
			// TextView tv = new TextView(CallSmsSafeActivity.this);
			// BlackInfo info = mDatas.get(position);
			// tv.setText(info.number + "==" + info.type);
			// return tv;

			ViewHolder holder = null;
			if (convertView == null) {
				// 没有复用
				// 1. 加载布局
				convertView = View.inflate(CallSmsSafeActivity.this,
						R.layout.item_black, null);
				// 2. 初始化holder
				holder = new ViewHolder();
				// 3. 绑定holder，打标记
				convertView.setTag(holder);
				// 4. 初始化holder中的View
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.item_black_tv_number);
				holder.tvType = (TextView) convertView
						.findViewById(R.id.item_black_tv_type);
				holder.ivDelete = (ImageView) convertView
						.findViewById(R.id.item_black_iv_delete);
			} else {
				// 复用
				holder = (ViewHolder) convertView.getTag();
			}

			final BlackInfo info = mDatas.get(position);
			// 给view设置数据
			holder.tvNumber.setText(info.number);

			switch (info.type) {
			case BlackInfo.TYPE_CALL:
				holder.tvType.setText("电话拦截");
				break;
			case BlackInfo.TYPE_SMS:
				holder.tvType.setText("短信拦截");
				break;
			case BlackInfo.TYPE_ALL:
				holder.tvType.setText("电话+短信拦截");
				break;
			default:
				break;
			}

			// 设置删除点击事件
			holder.ivDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// 数据库删除
					boolean delete = mDao.delete(info.number);
					if (delete) {
						mDatas.remove(info);

						// ui更新
						mAdapter.notifyDataSetChanged();

						Toast.makeText(getApplicationContext(), "删除成功",
								Toast.LENGTH_SHORT).show();

						// if (mDatas.size() == 0) {
						// 查询
						// startQuery();
						// }

						startQuery(1, mDatas.size());

					} else {
						Toast.makeText(getApplicationContext(), "删除失败",
								Toast.LENGTH_SHORT).show();
					}

				}
			});

			return convertView;
		}
	}

	class ViewHolder {
		TextView tvNumber;
		TextView tvType;
		ImageView ivDelete;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		BlackInfo info = mDatas.get(position);
		// 跳转到更新界面
		Intent intent = new Intent(this, BlackEditActivity.class);
		intent.setAction(BlackEditActivity.ACTION_UPDATE);
		intent.putExtra(BlackEditActivity.EXTRA_NUMBER, info.number);
		intent.putExtra(BlackEditActivity.EXTRA_TYPE, info.type);
		intent.putExtra(BlackEditActivity.EXTRA_POSITION, position);
		startActivityForResult(intent, REQUEST_CODE_UPDATE);

	}

}
