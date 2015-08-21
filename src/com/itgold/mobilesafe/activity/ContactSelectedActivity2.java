package com.itgold.mobilesafe.activity;

import java.util.List;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.bean.ContactInfo;
import com.itgold.mobilesafe.utils.ContactUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContactSelectedActivity2 extends Activity implements
		OnItemClickListener {

	public static final String KEY_NUMBER = "number";

	private ListView mListView;
	private ProgressBar mProgressBar;

	private Cursor mCursor;

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
				// mDatas = ContactUtils
				// .getAllPhone(ContactSelectedActivity2.this);

				mCursor = ContactUtils
						.getAllPhoneCursor(ContactSelectedActivity2.this);

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
						// mListView.setAdapter(new ContactAdapter());// adapter
						// // --->
						// // List数据

						mListView.setAdapter(new ContactAdapter(
								ContactSelectedActivity2.this, mCursor));
					}
				});
			}
		}).start();

		// 设置点击事件
		mListView.setOnItemClickListener(this);
	}

	private class ContactAdapter extends CursorAdapter {

		public ContactAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// 创建View
			return View.inflate(ContactSelectedActivity2.this,
					R.layout.item_contact, null);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// 绑定数据
			// view ： 显示的view

			ImageView ivIcon = (ImageView) view
					.findViewById(R.id.item_contact_iv_icon);
			TextView tvName = (TextView) view
					.findViewById(R.id.item_contact_tv_name);
			TextView tvNumber = (TextView) view
					.findViewById(R.id.item_contact_tv_number);

			// 设置数据
			// cursor : 数据

			ContactInfo info = ContactUtils.getContactInfo(cursor);

			tvName.setText(info.name);
			tvNumber.setText(info.number);

			Bitmap bitmap = ContactUtils.getContactIcon(
					ContactSelectedActivity2.this, info.contactId);
			if (bitmap != null) {
				ivIcon.setImageBitmap(bitmap);
			} else {
				ivIcon.setImageResource(R.drawable.ic_contact);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		mCursor.moveToPosition(position);
		ContactInfo info = ContactUtils.getContactInfo(mCursor);

		Intent data = new Intent();
		data.putExtra(KEY_NUMBER, info.number);
		setResult(Activity.RESULT_OK, data);

		finish();
	}
}
