package com.itgold.mobilesafe.view;

import com.itgold.mobilesafe.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AddressDialog extends Dialog {

	private ListView mListView;
	private ListAdapter mAdapter;
	private OnItemClickListener mListener;

	public AddressDialog(Context context) {
		super(context, R.style.AddressDialogTheme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_address);

		mListView = (ListView) findViewById(R.id.dialog_listview);
		// 设置adapter
		setAdapter(mAdapter);
		setOnItemClickListener(mListener);

		// 改变样式-->window的样式
		Window window = getWindow();
		LayoutParams params = window.getAttributes();
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;// 设置弹出位置
		window.setAttributes(params);
	}

	/**
	 * 设置adapter
	 * 
	 * @param adapter
	 */
	public void setAdapter(ListAdapter adapter) {
		this.mAdapter = adapter;
		if (mListView != null) {
			mListView.setAdapter(adapter);
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
		if (mListView != null) {
			mListView.setOnItemClickListener(listener);
		}
	}

}
