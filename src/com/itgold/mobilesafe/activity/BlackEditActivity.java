package com.itgold.mobilesafe.activity;

import com.itgold.mobilesafe.R;

import com.itgold.mobilesafe.bean.BlackInfo;
import com.itgold.mobilesafe.db.BlackDao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class BlackEditActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	public final static String ACTION_ADD = "add";
	public final static String ACTION_UPDATE = "update";

	public final static String EXTRA_NUMBER = "number";
	public final static String EXTRA_TYPE = "type";
	public static final String EXTRA_POSITION = "position";

	private TextView mTvTitle;
	private Button mBtnOk;
	private Button mBtnCancel;
	private EditText mEtNumber;
	private RadioGroup mRgType;

	private int mCheckedId = -1;// 选中的id
	private boolean isUpdate;
	private int mPosition = -1;

	private BlackDao mDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_edit);

		mDao = new BlackDao(this);

		// 初始化view
		mTvTitle = (TextView) findViewById(R.id.be_tv_title);
		mBtnOk = (Button) findViewById(R.id.be_btn_ok);
		mBtnCancel = (Button) findViewById(R.id.be_btn_cancel);
		mEtNumber = (EditText) findViewById(R.id.be_et_number);
		mRgType = (RadioGroup) findViewById(R.id.be_rg_type);

		// 设置事件
		mBtnOk.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mRgType.setOnCheckedChangeListener(this);

		// 判断是 更新还是添加
		Intent intent = getIntent();
		String action = intent.getAction();

		if (ACTION_UPDATE.equals(action)) {
			isUpdate = true;
			mPosition = intent.getIntExtra(EXTRA_POSITION, -1);
			// 更新
			// UI
			// title
			mTvTitle.setText("更新黑名单");
			// btn
			mBtnOk.setText("更新");

			// et
			mEtNumber.setEnabled(false);

			// 数据
			// 输入框--》需要更新的号码
			String number = intent.getStringExtra(EXTRA_NUMBER);
			mEtNumber.setText(number);
			// 单选框--》当前的模式
			int type = intent.getIntExtra(EXTRA_TYPE, -1);

			switch (type) {
			case BlackInfo.TYPE_CALL:
				mCheckedId = R.id.be_rb_call;
				break;
			case BlackInfo.TYPE_SMS:
				mCheckedId = R.id.be_rb_sms;
				break;
			case BlackInfo.TYPE_ALL:
				mCheckedId = R.id.be_rb_all;
				break;
			default:
				break;
			}
			mRgType.check(mCheckedId);// radioButton的id
		} else {
			// 添加
		}

	}

	@Override
	public void onClick(View v) {
		if (v == mBtnOk) {
			performOk();
		} else if (v == mBtnCancel) {
			performCancel();
		}
	}

	private void performCancel() {
		finish();
	}

	private void performOk() {
		// 校验
		// 输入框
		String number = mEtNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
			mEtNumber.requestFocus();
			return;
		}

		// 单选框
		if (mCheckedId == -1) {
			Toast.makeText(this, "类型不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		int type = -1;
		switch (mCheckedId) {
		case R.id.be_rb_call:// 0
			type = BlackInfo.TYPE_CALL;
			break;
		case R.id.be_rb_sms:// 1
			type = BlackInfo.TYPE_SMS;
			break;
		case R.id.be_rb_all:// 2
			type = BlackInfo.TYPE_ALL;
			break;
		}

		if (isUpdate) {
			// 更新
			boolean update = mDao.update(number, type);
			if (update) {
				Intent data = new Intent();
				data.putExtra(EXTRA_POSITION, mPosition);
				data.putExtra(EXTRA_TYPE, type);
				setResult(Activity.RESULT_OK, data);

				Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
			}

		} else {
			// 数据库操作
			boolean add = mDao.add(number, type);
			if (add) {
				// 把数据返回
				Intent data = new Intent();
				data.putExtra(EXTRA_NUMBER, number);
				data.putExtra(EXTRA_TYPE, type);
				setResult(Activity.RESULT_OK, data);

				Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
			}

		}

		// 结束自己
		finish();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		this.mCheckedId = checkedId;
	}
}
