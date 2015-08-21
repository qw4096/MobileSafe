package com.itgold.mobilesafe.activity;

import java.util.List;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.bean.ContactInfo;
import com.itgold.mobilesafe.utils.ContactUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContactSelectedActivity extends Activity implements
		OnItemClickListener {

	public static final String KEY_NUMBER = "number";

	private ListView mListView;
	private ProgressBar mProgressBar;

	private List<ContactInfo> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_selected);

		mListView = (ListView) findViewById(R.id.cs_listview);
		mProgressBar = (ProgressBar) findViewById(R.id.cs_pb);
		
		// 子线程中执行
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 加载数据
				mDatas = ContactUtils.getAllPhone(ContactSelectedActivity.this);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				

				// 子线程中不可以做ui操作
				runOnUiThread(new Runnable() {
					public void run() {
						// 隐藏progressBar
						mProgressBar.setVisibility(View.GONE);
						
						// 给listview设置数据
						mListView.setAdapter(new ContactAdapter());// adapter
																	// --->
																	// List数据
					}
				});
			}
		}).start();

		// 设置点击事件
		mListView.setOnItemClickListener(this);
	}

	private class ContactAdapter extends BaseAdapter {

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
			// TextView tv = new TextView(ContactSelectedActivity.this);
			//
			// ContactInfo info = mDatas.get(position);
			// tv.setText(info.name + "===" + info.number);

			// View view = View.inflate(ContactSelectedActivity.this,
			// R.layout.item_contact, null);
			// ImageView ivIcon = (ImageView) view
			// .findViewById(R.id.item_contact_iv_icon);
			// TextView tvName = (TextView) view
			// .findViewById(R.id.item_contact_tv_name);
			// TextView tvNumber = (TextView) view
			// .findViewById(R.id.item_contact_tv_number);

			// if (convertView == null) {
			// convertView = View.inflate(ContactSelectedActivity.this,
			// R.layout.item_contact, null);
			// }
			//
			// ImageView ivIcon = (ImageView) convertView
			// .findViewById(R.id.item_contact_iv_icon);
			// TextView tvName = (TextView) convertView
			// .findViewById(R.id.item_contact_tv_name);
			// TextView tvNumber = (TextView) convertView
			// .findViewById(R.id.item_contact_tv_number);
			//
			// ContactInfo info = mDatas.get(position);
			// tvName.setText(info.name);
			// tvNumber.setText(info.number);
			//
			// Bitmap bitmap = ContactUtils.getContactIcon(
			// ContactSelectedActivity.this, info.contactId);
			// if (bitmap != null) {
			// ivIcon.setImageBitmap(bitmap);
			// } else {
			// ivIcon.setImageResource(R.drawable.ic_contact);
			// }

			ViewHolder holder = null;
			if (convertView == null) {
				// 说明没有复用
				// 1.加载view
				convertView = View.inflate(ContactSelectedActivity.this,
						R.layout.item_contact, null);
				// 2. 初始化holder
				holder = new ViewHolder();
				// 3. 初始化view
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_contact_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_contact_tv_name);
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.item_contact_tv_number);
				// 4. 设置标记
				convertView.setTag(holder);
			} else {
				// 有复用
				holder = (ViewHolder) convertView.getTag();
			}

			ContactInfo info = mDatas.get(position);
			holder.tvName.setText(info.name);
			holder.tvNumber.setText(info.number);
			Bitmap bitmap = ContactUtils.getContactIcon(
					ContactSelectedActivity.this, info.contactId);
			if (bitmap != null) {
				holder.ivIcon.setImageBitmap(bitmap);
			} else {
				holder.ivIcon.setImageResource(R.drawable.ic_contact);
			}

			return convertView;
		}
	}

	static class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvNumber;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		ContactInfo info = mDatas.get(position);
		Intent data = new Intent();
		data.putExtra(KEY_NUMBER, info.number);
		setResult(Activity.RESULT_OK, data);

		finish();
	}
}
