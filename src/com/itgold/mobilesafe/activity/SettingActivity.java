package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.service.CallSmsSafeService;
import com.itgold.mobilesafe.service.NumberAddressService;
import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.PreferenceUtils;
import com.itgold.mobilesafe.utils.ServiceStateUtils;
import com.itgold.mobilesafe.view.AddressDialog;
import com.itgold.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//设置页面
public class SettingActivity extends Activity {

	private SettingItemView mSivAutoUpdate;

	private SettingItemView mSivCallSmsSafe;

	private SettingItemView mSivNumberAddress;

	private SettingItemView mSivAddressStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_update);

		// 设置当前更新的状态
		boolean update = PreferenceUtils.getBoolean(this,
				Constants.AUTO_UPDATE, true);
		mSivAutoUpdate.setToggleOn(update);

		mSivAutoUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果是打开，就关闭，如果是关闭就打开
				// if (mSivAutoUpdate.isToggleOn()) {
				// mSivAutoUpdate.setToggleOn(false);
				// } else {
				// mSivAutoUpdate.setToggleOn(true);
				// }
				mSivAutoUpdate.toggle();

				// 存储状态信息
				boolean toggleOn = mSivAutoUpdate.isToggleOn();

				// SharedPreferences sp = getSharedPreferences("config",
				// Context.MODE_PRIVATE);
				// Editor edit = sp.edit();
				// edit.putBoolean("setting_update", toggleOn);
				// edit.commit();

				PreferenceUtils.putBoolean(SettingActivity.this,
						Constants.AUTO_UPDATE, toggleOn);
			}
		});

		mSivCallSmsSafe = (SettingItemView) findViewById(R.id.setting_siv_callsmssafe);

		// 点击操作
		mSivCallSmsSafe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 开启或者关闭服务
				// 如果是开启的那么就关闭
				if (ServiceStateUtils.isRunging(getApplicationContext(),
						CallSmsSafeService.class)) {
					// 运行的--》stop
					Intent intent = new Intent(getApplicationContext(),
							CallSmsSafeService.class);
					stopService(intent);

					// UI改变
					mSivCallSmsSafe.setToggleOn(false);
				} else {
					Intent intent = new Intent(getApplicationContext(),
							CallSmsSafeService.class);
					startService(intent);

					// UI改变
					mSivCallSmsSafe.setToggleOn(true);
				}
			}
		});

		mSivNumberAddress = (SettingItemView) findViewById(R.id.setting_siv_address);

		mSivNumberAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果服务开启，就关闭
				if (ServiceStateUtils.isRunging(getApplicationContext(),
						NumberAddressService.class)) {
					stopService(new Intent(getApplicationContext(),
							NumberAddressService.class));

					mSivNumberAddress.setToggleOn(false);
				} else {
					startService(new Intent(getApplicationContext(),
							NumberAddressService.class));

					mSivNumberAddress.setToggleOn(true);
				}
			}
		});

		// 归属地样式
		mSivAddressStyle = (SettingItemView) findViewById(R.id.setting_siv_address_style);

		mSivAddressStyle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 弹出Dialog
				final AddressDialog dialog = new AddressDialog(
						SettingActivity.this);
				dialog.setAdapter(new AddressAdapter());// -->list
				dialog.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						dialog.dismiss();

						PreferenceUtils.putInt(getApplicationContext(),
								Constants.ADDRESS_STYLE, icons[position]);

					}
				});

				dialog.show();
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();

		if (ServiceStateUtils.isRunging(getApplicationContext(),
				CallSmsSafeService.class)) {
			mSivCallSmsSafe.setToggleOn(true);
		} else {
			mSivCallSmsSafe.setToggleOn(false);
		}

		// 号码归属地服务的判断
		if (ServiceStateUtils.isRunging(getApplicationContext(),
				NumberAddressService.class)) {
			mSivNumberAddress.setToggleOn(true);
		} else {
			mSivNumberAddress.setToggleOn(false);
		}
	}

	private String[] titles = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
	private int[] icons = new int[] { R.drawable.toast_address_normal,
			R.drawable.toast_address_orange, R.drawable.toast_address_blue,
			R.drawable.toast_address_gray, R.drawable.toast_address_green };

	private class AddressAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				// 没有复用
				// 初始化converView
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_address, null);
				// 初始化holder
				holder = new ViewHolder();
				// 设置标记
				convertView.setTag(holder);
				// 初始化holder的view
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_addres_iv_icon);
				holder.ivSelected = (ImageView) convertView
						.findViewById(R.id.item_addres_iv_selected);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.item_addres_tv_title);
			} else {
				// 有复用
				holder = (ViewHolder) convertView.getTag();
			}

			int style = PreferenceUtils.getInt(getApplicationContext(),
					Constants.ADDRESS_STYLE, R.drawable.toast_address_normal);

			// 给view设置数据
			holder.ivIcon.setImageResource(icons[position]);
			if (style == icons[position]) {
				holder.ivSelected.setVisibility(View.VISIBLE);
			} else {
				holder.ivSelected.setVisibility(View.GONE);
			}

			holder.tvTitle.setText(titles[position]);

			return convertView;
		}
	}

	class ViewHolder {
		ImageView ivIcon;
		ImageView ivSelected;
		TextView tvTitle;

	}
}
