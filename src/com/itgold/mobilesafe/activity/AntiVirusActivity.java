package com.itgold.mobilesafe.activity;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.itgold.mobilesafe.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.itgold.mobilesafe.bean.AntiVirusInfo;
import com.itgold.mobilesafe.db.AntivirusDao;
import com.itgold.mobilesafe.utils.Logger;
import com.itgold.mobilesafe.utils.MD5Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class AntiVirusActivity extends Activity implements OnClickListener {

	private static final String TAG = null;
	private PackageManager mPm;

	private List<AntiVirusInfo> mDatas;

	private TextView mTvPackageName;
	// private TextView mTvProgress;
	private ArcProgress mArcProgress;

	private ListView mListView;
	private AntiVirusAdapter mAdapter;

	private RelativeLayout mRlProgressContainer;
	private LinearLayout mLlResultContainer;
	private LinearLayout mLlAnimatorContainer;
	private TextView mTvResult;
	private Button mBtnScan;

	private ImageView mIvLeft;
	private ImageView mIvRight;

	private int mVirusTotal;// 记录总共有几个病毒
	private ScanTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);

		mPm = getPackageManager();

		// 初始化view
		mTvPackageName = (TextView) findViewById(R.id.aa_tv_packageName);
		// mTvProgress = (TextView) findViewById(R.id.aa_tv_progress);
		mArcProgress = (ArcProgress) findViewById(R.id.aa_arc_progress);
		mListView = (ListView) findViewById(R.id.aa_listview);
		mRlProgressContainer = (RelativeLayout) findViewById(R.id.aa_progress_container);
		mLlResultContainer = (LinearLayout) findViewById(R.id.aa_result_container);
		mLlAnimatorContainer = (LinearLayout) findViewById(R.id.aa_animator_container);
		mTvResult = (TextView) findViewById(R.id.aa_tv_result);
		mBtnScan = (Button) findViewById(R.id.aa_btn_scan);
		mBtnScan.setOnClickListener(this);

		mIvLeft = (ImageView) findViewById(R.id.aa_iv_left);
		mIvRight = (ImageView) findViewById(R.id.aa_iv_right);

		startScan();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
	}

	private void startScan() {
		// 开线程扫描包
		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}

		mTask = new ScanTask();
		mTask.execute();
	}

	private final class ScanTask extends AsyncTask<Void, AntiVirusInfo, Void> {
		private int progress;
		private int max;
		private boolean isFinish;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mRlProgressContainer.setVisibility(View.VISIBLE);
			mLlResultContainer.setVisibility(View.GONE);
			mLlAnimatorContainer.setVisibility(View.GONE);
			mBtnScan.setEnabled(false);
		}

		public void stop() {
			isFinish = true;
		}

		@Override
		protected Void doInBackground(Void... params) {

			List<PackageInfo> list = mPm
					.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

			// 设置最大值
			max = list.size();

			mDatas = new ArrayList<AntiVirusInfo>();
			for (PackageInfo pack : list) {
				progress++;

				// data/app/xxx.apk
				// system/app/xxx.apk
				String sourceDir = pack.applicationInfo.sourceDir;

				FileInputStream in;
				try {
					in = new FileInputStream(sourceDir);
					String md5 = MD5Utils.encode(in);
					String name = pack.applicationInfo.loadLabel(mPm)
							.toString();
					Drawable icon = pack.applicationInfo.loadIcon(mPm);
					boolean isVirus = AntivirusDao.isVirus(
							getApplicationContext(), md5);

					Logger.d(TAG, "name : " + name);
					Logger.d(TAG, "md5 : " + md5);
					Logger.d(TAG, "---------------------------");

					AntiVirusInfo info = new AntiVirusInfo();
					info.icon = icon;
					info.name = name;
					info.packageName = pack.packageName;
					info.isVirus = isVirus;

					// 添加到集合
					if (info.isVirus) {
						// 添加到第一个
						mDatas.add(0, info);

						mVirusTotal++;
					} else {
						mDatas.add(info);
					}

					// push进度
					publishProgress(info);

					if (isFinish) {
						break;
					}

					Thread.sleep(100);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(AntiVirusInfo... values) {
			if (isFinish) {
				return;
			}

			AntiVirusInfo info = values[0];

			// 改变扫描的包名和进度
			mTvPackageName.setText(info.packageName);
			int currentProgress = (int) (progress * 100f / max + 0.5f);
			// mTvProgress.setText(currentProgress + "%");
			mArcProgress.setProgress(currentProgress);

			if (progress == 1) {

				mAdapter = new AntiVirusAdapter();
				mListView.setAdapter(mAdapter);
			} else {
				// adapter更新
				mAdapter.notifyDataSetChanged();
			}

			mListView.smoothScrollToPosition(mAdapter.getCount());
		}

		@Override
		protected void onPostExecute(Void result) {
			if (isFinish) {
				return;
			}
			// 滚动到顶部
			mListView.smoothScrollToPosition(0);

			if (mVirusTotal > 0) {
				mTvResult.setText("您的手机很不安全");
				mTvResult.setTextColor(Color.RED);
			} else {
				mTvResult.setText("您的手机很安全");
				mTvResult.setTextColor(Color.WHITE);
			}

			// 结束时，做打开的动画
			// 1.获得 进度容器的图片
			mRlProgressContainer.setDrawingCacheEnabled(true);
			mRlProgressContainer
					.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
			Bitmap bitmap = mRlProgressContainer.getDrawingCache();// null

			// 给动画imageView设置图片,做动画
			mIvLeft.setImageBitmap(getLeftBitmap(bitmap));
			mIvRight.setImageBitmap(getRightBitmap(bitmap));

			// 显示结果，隐藏进度
			mRlProgressContainer.setVisibility(View.GONE);
			mLlResultContainer.setVisibility(View.VISIBLE);
			mLlAnimatorContainer.setVisibility(View.VISIBLE);
			mLlAnimatorContainer.bringToFront();

			// 显示动画
			showOpenAnimtor();
		}
	}

	private void showOpenAnimtor() {
		AnimatorSet set = new AnimatorSet();

		mLlAnimatorContainer.measure(0, 0);
		set.playTogether(
				ObjectAnimator.ofFloat(mIvLeft, "translationX", 0,
						-mIvLeft.getMeasuredWidth()),
				ObjectAnimator.ofFloat(mIvRight, "translationX", 0,
						mIvRight.getMeasuredWidth()),
				ObjectAnimator.ofFloat(mIvLeft, "alpha", 1, 0),
				ObjectAnimator.ofFloat(mIvRight, "alpha", 1, 0),
				ObjectAnimator.ofFloat(mLlResultContainer, "alpha", 0, 1));
		set.setDuration(3000);
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				mBtnScan.setEnabled(true);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		});
		set.start();
	}

	private Bitmap getLeftBitmap(Bitmap srcBitmap) {
		// 1. 准备画纸
		int width = (int) (srcBitmap.getWidth() / 2f + 0.5f);
		int height = srcBitmap.getHeight();
		Bitmap destBitmap = Bitmap.createBitmap(width, height,
				srcBitmap.getConfig());
		// 2. 准备画板，把画纸放上去
		Canvas canvas = new Canvas(destBitmap);
		// 3. 准备笔
		Paint paint = new Paint();
		// 4. 准备规则
		Matrix matrix = new Matrix();

		// 5. 绘制
		canvas.drawBitmap(srcBitmap, matrix, paint);

		return destBitmap;
	}

	private Bitmap getRightBitmap(Bitmap srcBitmap) {
		// 1. 准备画纸
		int width = (int) (srcBitmap.getWidth() / 2f + 0.5f);
		int height = srcBitmap.getHeight();
		Bitmap destBitmap = Bitmap.createBitmap(width, height,
				srcBitmap.getConfig());
		// 2. 准备画板，把画纸放上去
		Canvas canvas = new Canvas(destBitmap);
		// 3. 准备笔
		Paint paint = new Paint();
		// 4. 准备规则
		Matrix matrix = new Matrix();
		matrix.setTranslate(-width, 0);

		// 5. 绘制
		canvas.drawBitmap(srcBitmap, matrix, paint);

		return destBitmap;
	}

	private class AntiVirusAdapter extends BaseAdapter {

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
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_virus_info, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				// findView
				holder.ivClean = (ImageView) convertView
						.findViewById(R.id.item_virusinfo_iv_clean);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_virusinfo_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_virusinfo_tv_name);
				holder.tvVirus = (TextView) convertView
						.findViewById(R.id.item_virusinfo_tv_virus);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置数据
			AntiVirusInfo info = mDatas.get(position);
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvVirus.setText(info.isVirus ? "病毒" : "安全");
			holder.tvVirus.setTextColor(info.isVirus ? Color.RED : Color.GREEN);
			holder.ivClean.setVisibility(info.isVirus ? View.VISIBLE
					: View.GONE);

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvVirus;
		ImageView ivClean;
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnScan) {
			// startScan();
			mBtnScan.setEnabled(false);

			AnimatorSet set = new AnimatorSet();
			set.playTogether(
					ObjectAnimator.ofFloat(mIvLeft, "translationX",
							-mIvLeft.getWidth(), 0),
					ObjectAnimator.ofFloat(mIvRight, "translationX",
							mIvRight.getWidth(), 0),
					ObjectAnimator.ofFloat(mIvLeft, "alpha", 0, 1),
					ObjectAnimator.ofFloat(mIvRight, "alpha", 0, 1),
					ObjectAnimator.ofFloat(mLlResultContainer, "alpha", 1, 0));
			set.setDuration(3000);

			set.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					startScan();
				}

				@Override
				public void onAnimationCancel(Animator arg0) {
					// TODO Auto-generated method stub

				}
			});
			set.start();

		}
	}
}
