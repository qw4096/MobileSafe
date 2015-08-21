package com.itgold.mobilesafe.activity;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import com.itgold.mobilesafe.R;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.itgold.mobilesafe.service.ProtectedService;
import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.GZipUtils;
import com.itgold.mobilesafe.utils.Logger;
import com.itgold.mobilesafe.utils.PackageUtils;
import com.itgold.mobilesafe.utils.PreferenceUtils;
import com.itgold.mobilesafe.utils.ServiceStateUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";

	public static final int SHOW_ERROR = 110;

	public static final int SHOW_UPDATE_DIALOG = 120;

	protected static final int REQUEST_CODE_INSTALL = 100;

	private TextView mTvVersion;

	private String mDesc;// 版本更新的描述信息

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;

			switch (what) {
			case SHOW_ERROR:
				// 提示错误：
				Toast.makeText(getApplicationContext(), msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				// 进入主页
				load2Home();
				break;
			case SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			default:
				break;
			}

		}
	};

	public String mUrl;// 最新版本的url

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// 初始化view
		mTvVersion = (TextView) findViewById(R.id.splash_tv_version);

		// 显示版本号
		mTvVersion.setText(PackageUtils.getVersionName(this));

		// 检测是否更新
		boolean update = PreferenceUtils.getBoolean(this,
				Constants.AUTO_UPDATE, true);
		if (update) {
			Logger.d(TAG, "需要检测更新");
			checkVersionUpdate();
		} else {
			Logger.d(TAG, "不需要检测更新");
			load2Home();
		}

		// 拷贝解压号码归属地数据库
		copyAddressDB();

		// 拷贝常用号码
		copyCommonNumberDB();

		// 拷贝病毒数据库
		copyVirusDB();

		// 开启必要的服务
		if (!ServiceStateUtils.isRunging(this, ProtectedService.class)) {
			startService(new Intent(this, ProtectedService.class));
		}

		boolean flag = PreferenceUtils.getBoolean(this, Constants.HAS_SHORTCUT);
		if (!flag) {
			// 创建快捷图标
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.main_icon);

			Intent intent = new Intent();
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			// 指定名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黄金卫士");
			// 指定图标
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
			// 指定行为
			Intent clickIntent = new Intent(this, SplashActivity.class);
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);
			sendBroadcast(intent);

			// 设置
			PreferenceUtils.putBoolean(this, Constants.HAS_SHORTCUT, true);
		}

//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				String a = null;
//				a.equals("dsaffdsa");
//			}
//		}).start();
	}

	private void copyVirusDB() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File destFile = new File(getFilesDir(), "antivirus.db");
				if (!destFile.exists()) {
					Logger.d(TAG, "antivirus.db数据库不存在需要拷贝");
					// 拷贝
					AssetManager assets = getAssets();
					InputStream is = null;
					FileOutputStream os = null;
					try {
						is = assets.open("antivirus.db");
						os = new FileOutputStream(destFile);

						byte[] buffer = new byte[1024];
						int len = -1;
						while ((len = is.read(buffer)) != -1) {
							os.write(buffer, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						close(is);
						close(os);
					}
				} else {
					Logger.d(TAG, "antivirus.db数据库已经存在不需要拷贝");
				}
			}
		}).start();
	}

	private void copyCommonNumberDB() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				File destFile = new File(getFilesDir(), "commonnum.db");
				if (!destFile.exists()) {
					Logger.d(TAG, "commonnum数据库不存在需要拷贝");
					// 拷贝
					AssetManager assets = getAssets();
					InputStream is = null;
					FileOutputStream os = null;
					try {
						is = assets.open("commonnum.db");
						os = new FileOutputStream(destFile);

						byte[] buffer = new byte[1024];
						int len = -1;
						while ((len = is.read(buffer)) != -1) {
							os.write(buffer, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						close(is);
						close(os);
					}
				} else {
					Logger.d(TAG, "commonnum数据库已经存在不需要拷贝");
				}
			}
		}).start();
	}

	private void copyAddressDB() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File destFile = new File(getFilesDir(), "address.db");

				if (!destFile.exists()) {
					Logger.d(TAG, "数据库不存在需要拷贝");
					copyAndunzipAddressDB2();
				} else {
					Logger.d(TAG, "数据库已经存在不需要拷贝");
				}

			}

			private void copyAndunzipAddressDB2() {
				AssetManager assets = getAssets();
				InputStream is = null;
				FileOutputStream os = null;
				File destFile = new File(getFilesDir(), "address.db");
				try {
					is = assets.open("address.zip");
					os = new FileOutputStream(destFile);
					GZipUtils.unzip(is, os);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private void copyAndunzipAddressDB1() {
				// 1. 拷贝文件
				AssetManager assets = getAssets();
				File destFile = new File(getFilesDir(), "address.zip");

				InputStream is = null;
				FileOutputStream fos = null;
				try {
					is = assets.open("address.zip");
					fos = new FileOutputStream(destFile);

					byte[] buffer = new byte[1024];
					int len = -1;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					close(is);
					close(fos);
				}

				// 2. 解压文件
				File zipFile = new File(getFilesDir(), "address.zip");
				File targetFile = new File(getFilesDir(), "address.db");
				try {
					GZipUtils.unzip(zipFile, targetFile);

					// 删除zip文件
					zipFile.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private void close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			io = null;
		}
	}

	private void load2Home() {

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				Logger.d(TAG, "name : " + name);

				Intent intent = new Intent(SplashActivity.this,
						HomeActivity.class);
				startActivity(intent);

				finish();
			}
		}, 1200);
	}

	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 设置点击取消不可用
		builder.setCancelable(false);

		// 设置title
		builder.setTitle("版本更新提醒");
		// 设置文本
		builder.setMessage(mDesc);

		// button
		builder.setPositiveButton("立刻升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				// 去下载最新版本
				showProgressDialog();
			}
		});

		builder.setNegativeButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				load2Home();
			}
		});

		// 显示
		builder.show();
	}

	private void showProgressDialog() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.show();

		// 去下载新版本
		HttpUtils utils = new HttpUtils();
		String url = mUrl;// 下载的地址
		final String target = new File(
				Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".apk").getAbsolutePath();

		utils.download(url, target, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				// 成功时的回调
				Logger.d(TAG, "下载成功");

				dialog.dismiss();
				// 去安装:
				// 安装是系统行为

				// <intent-filter>
				// <action android:name="android.intent.action.VIEW" />
				// <category android:name="android.intent.category.DEFAULT" />
				// <data android:scheme="content" />
				// <data android:scheme="file" />
				// <data
				// android:mimeType="application/vnd.android.package-archive" />
				// </intent-filter>

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.parse("file:" + target),
						"application/vnd.android.package-archive");

				// Uri.fromFile(file)//-->file:路径
				startActivityForResult(intent, REQUEST_CODE_INSTALL);
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// total：下载的文件的总大小
				// current：当前下载到了什么位置
				dialog.setMax((int) total);
				dialog.setProgress((int) current);
			}

			@Override
			public void onFailure(HttpException e, String arg1) {

				e.printStackTrace();
				// 失败的回调
				Logger.d(TAG, "下载失败");

				dialog.dismiss();

				// 进入主页
				load2Home();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode:自己发送的请求代码
		// resultCode ：结果代码,你打开的Activity做的标记

		if (requestCode == REQUEST_CODE_INSTALL) {
			// 安装的程序返回的数据

			switch (resultCode) {
			case Activity.RESULT_OK:
				// 用户成功操作
				Logger.d(TAG, "用户成功安装");
				break;
			case Activity.RESULT_CANCELED:
				// 用户取消操作
				Logger.d(TAG, "用户取消安装");
				load2Home();
				break;
			default:
				break;
			}
		}
	}

	private void checkVersionUpdate() {
		// 1.去网络获取最新的版本信息
		new Thread(new CheckVersionTask()).start();
	}

	private class CheckVersionTask implements Runnable {

		@Override
		public void run() {
			// 服务器必须提供网络接口

			String uri = "http://188.188.2.100:8080/update.txt";

			// 获取网络访问的客户端
			AndroidHttpClient client = AndroidHttpClient.newInstance("gold",
					getApplicationContext());
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);// 设置访问网络的超时时间
			HttpConnectionParams.setSoTimeout(params, 5000);// 设置读取的超时时间

			// 创造get请求
			HttpGet get = new HttpGet(uri);

			try {
				// 获得response
				HttpResponse response = client.execute(get);

				// 获得状态码
				int statusCode = response.getStatusLine().getStatusCode();
				if (200 == statusCode) {
					// 访问成功
					String result = EntityUtils.toString(response.getEntity(),
							"utf-8");

					Logger.d(TAG, "访问结果:" + result);
					// {versionCode:2}

					// 获得本地的版本号
					int localCode = PackageUtils
							.getVersionCode(getApplicationContext());

					// 解析json
					JSONObject jsonObject = new JSONObject(result);

					int netCode = jsonObject.getInt("versionCode");

					// 比对
					if (netCode > localCode) {
						// 需要更新 ,显示更新的对话框
						Logger.d(TAG, "需要更新");

						mDesc = jsonObject.getString("desc");
						mUrl = jsonObject.getString("url");

						Message msg = Message.obtain();
						msg.what = SHOW_UPDATE_DIALOG;
						mHandler.sendMessage(msg);

					} else {
						// 不需要更新,进入主页
						Logger.d(TAG, "不需要更新");
						load2Home();
					}
				} else {
					// 访问失败
					Message msg = Message.obtain();
					msg.what = SHOW_ERROR;
					msg.obj = "code:130";
					mHandler.sendMessage(msg);
				}
			} catch (ClientProtocolException e) {

				// Message msg = mHandler.obtainMessage();
				// msg.sendToTarget();

				Message msg = Message.obtain();
				msg.what = SHOW_ERROR;
				msg.obj = "code:110";
				mHandler.sendMessage(msg);

			} catch (IOException e) {

				Message msg = Message.obtain();
				msg.what = SHOW_ERROR;
				msg.obj = "code:120";
				mHandler.sendMessage(msg);
			} catch (JSONException e) {

				Message msg = Message.obtain();
				msg.what = SHOW_ERROR;
				msg.obj = "code:119";
				mHandler.sendMessage(msg);
			} finally {
				if (client != null) {
					client.close();
					client = null;
				}
			}

		}
	}

}
