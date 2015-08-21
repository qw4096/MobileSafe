package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.db.CommonNumberDao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity {

	private ExpandableListView mListView;

	private int mCurrentOpenPosition = -1;// 没有打开的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);

		mListView = (ExpandableListView) findViewById(R.id.cn_listview);

		mListView.setAdapter(new CommonNumberAdapter());

		// 设置group点击事件
		mListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// 如果当前没有打开打开当前
				if (mCurrentOpenPosition == -1) {
					// 一个都没有打开
					// 打开自己
					mListView.expandGroup(groupPosition);
					mCurrentOpenPosition = groupPosition;// 标记自己已经打开

					// 选中groupPosition
					mListView.setSelectedGroup(groupPosition);
				} else {
					// 有打开的情况
					// 如果点击的是打开的
					if (mCurrentOpenPosition == groupPosition) {
						// 关闭打开的
						mListView.collapseGroup(groupPosition);
						mCurrentOpenPosition = -1;
					} else {
						// 如果点击的不是打开的
						// 把之前打开的关闭
						mListView.collapseGroup(mCurrentOpenPosition);
						// 打开当前
						mListView.expandGroup(groupPosition);
						// 选中groupPosition
						mListView.setSelectedGroup(groupPosition);
						// 标记当前为打开的
						mCurrentOpenPosition = groupPosition;
					}
				}

				return true;
			}
		});

		mListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				String[] text = CommonNumberDao.getChildText(
						getApplicationContext(), groupPosition, childPosition);
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + text[1]));
				startActivity(intent);

				return true;
			}
		});
		
		mListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				
			}
		});
		
		mListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				// TODO Auto-generated method stub
				
			}
		});

	}

	private class TmpAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
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
			// TODO Auto-generated method stub
			return null;
		}

	}

	private class CommonNumberAdapter extends BaseExpandableListAdapter {

		// 分组的个数
		@Override
		public int getGroupCount() {
			return CommonNumberDao.getGroupCount(getApplicationContext());
		}

		// 第groupPosition有几个孩子
		@Override
		public int getChildrenCount(int groupPosition) {
			return CommonNumberDao.getChildCount(getApplicationContext(),
					groupPosition);
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TextView tv = new TextView(getApplicationContext());
			// tv.setText("" + groupPosition);
			// return tv;

			TextView tv = null;
			if (convertView == null) {
				convertView = new TextView(getApplicationContext());

				tv = (TextView) convertView;

				tv.setBackgroundColor(Color.parseColor("#33000000"));
				tv.setTextSize(18);
				tv.setPadding(10, 10, 10, 10);
				tv.setTextColor(Color.BLACK);
			} else {
				tv = (TextView) convertView;
			}

			// 给textView设置数据
			tv.setText(CommonNumberDao.getGroupText(getApplicationContext(),
					groupPosition));

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				convertView = new TextView(getApplicationContext());

				tv = (TextView) convertView;

				tv.setTextSize(16);
				tv.setPadding(8, 8, 8, 8);
				tv.setTextColor(Color.BLACK);
			} else {
				tv = (TextView) convertView;
			}

			String[] texts = CommonNumberDao.getChildText(
					getApplicationContext(), groupPosition, childPosition);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < texts.length; i++) {
				sb.append(texts[i]);
				if (i != (texts.length - 1)) {
					sb.append("\n");
				}
			}

			// 给textView设置数据
			tv.setText(sb.toString());

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

}
