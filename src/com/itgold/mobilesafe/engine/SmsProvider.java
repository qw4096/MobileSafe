package com.itgold.mobilesafe.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Xml;

public class SmsProvider {

	// 设置总数量 , 进度， 成功，失败
	// 接口: 标准--》 设置总数量 , 进度， 成功，失败 --？
	/**
	 * 短信的备份
	 */
	public static void smsBackup(final Context context,
			final OnSmsListener listener) {

		// 2.数据-->从哪里来，到哪里去-->数据格式
		// 从哪里来
		// 到哪里去--->SD-->数据格式
		// json,xml

		new AsyncTask<Void, Integer, Boolean>() {

			private final static int TYPE_SIZE = 0;
			private final static int TYPE_PROGRESS = 1;

			@Override
			protected Boolean doInBackground(Void... params) {

				// 格式
				/**
				 * <list> <sms> <address>13511111</address>
				 * <date>13511111</date> <type>1</type> <body>saldfjal</body>
				 * </sms> <sms></sms> </list>
				 */
				// 序列化 xml
				XmlSerializer serializer = Xml.newSerializer();
				FileOutputStream os = null;
				try {
					os = new FileOutputStream(new File(
							Environment.getExternalStorageDirectory(),
							"smsbackup.xml"));
					// 指定输出流
					serializer.setOutput(os, "utf-8");
					// 开始文档
					serializer.startDocument("utf-8", true);
					// 开始根节点
					serializer.startTag(null, "list");

					ContentResolver cr = context.getContentResolver();
					Uri uri = Uri.parse("content://sms/");
					String[] projection = new String[] { "address", "date",
							"type", "body" };
					Cursor cursor = cr.query(uri, projection, null, null, null);
					if (cursor != null) {
						int count = cursor.getCount();
						// 设置总数量
						// diaLogger.setMax(count);
						publishProgress(TYPE_SIZE, count);

						int progress = 0;
						while (cursor.moveToNext()) {
							// 开始sms节点
							serializer.startTag(null, "sms");

							// address
							serializer.startTag(null, "address");
							String address = cursor.getString(0);
							if (TextUtils.isEmpty(address)) {
								address = "";
							}
							serializer.text(address);
							serializer.endTag(null, "address");

							// date
							serializer.startTag(null, "date");
							long date = cursor.getLong(1);
							serializer.text(date + "");
							serializer.endTag(null, "date");

							// type
							serializer.startTag(null, "type");
							int type = cursor.getInt(2);
							serializer.text(type + "");
							serializer.endTag(null, "type");

							// body
							serializer.startTag(null, "body");
							String body = cursor.getString(3);
							if (TextUtils.isEmpty(body)) {
								body = "";
							}
							serializer.text(body);
							serializer.endTag(null, "body");

							// 结束sms节点
							serializer.endTag(null, "sms");

							Thread.sleep(300);

							// 进度
							progress++;
							// diaLogger.setProgress(progress);
							// mTvProgress.setText("进度:" + progress);
							publishProgress(TYPE_PROGRESS, progress);
						}
						cursor.close();
					}

					// 结束根节点
					serializer.endTag(null, "list");
					// 结束文档
					serializer.endDocument();

					// 成功 --> 提示
					// diaLogger.dismiss();
					// mTvMax.setText("成功");

					return true;
				} catch (Exception e) {
					e.printStackTrace();

					// 失败 -- 提示
					// diaLogger.dismiss();
					// mTvMax.setText("失败");
					return false;
				} finally {
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
							e.printStackTrace();

							if (listener != null) {
								listener.onFailed();
							}
						}
						os = null;
					}
				}
			}

			protected void onProgressUpdate(Integer[] values) {
				if (values[0] == TYPE_SIZE) {
					// mTvMax.setText("总数:" + values[1]);

					if (listener != null) {
						listener.onMax(values[1]);
					}
				} else {
					// mTvProgress.setText("进度:" + values[1]);

					if (listener != null) {
						listener.onProgress(values[1]);
					}
				}
			};

			protected void onPostExecute(Boolean result) {
				if (result) {
					// mTvMax.setText("成功");

					if (listener != null) {
						listener.onSucess();
					}
				} else {
					// mTvMax.setText("失败");

					if (listener != null) {
						listener.onFailed();
					}
				}
			};
		}.execute();
	}

	public interface OnSmsListener {
		// 接口: 标准--》 设置总数量 , 进度， 成功，失败 --？
		void onMax(int max);

		void onProgress(int progress);

		void onSucess();

		void onFailed();
	}

	public static void smsRestore(final Context context,
			final OnSmsListener listener) {
		// 读取 xml文件 --》 反序列化对象-->插入到sms数据库

		// 总数， 进度，成功，失败

		new AsyncTask<Void, Integer, Boolean>() {
			private final static int TYPE_SIZE = 0;
			private final static int TYPE_PROGRESS = 1;

			@Override
			protected Boolean doInBackground(Void... params) {

				XmlPullParser parser = Xml.newPullParser();
				FileInputStream is = null;
				try {
					is = new FileInputStream(new File(
							Environment.getExternalStorageDirectory(),
							"smsbackup.xml"));
					parser.setInput(is, "utf-8");

					List<SmsInfo> list = null;
					SmsInfo info = null;
					int type = parser.getEventType();
					do {
						String name = parser.getName();
						switch (type) {
						case XmlPullParser.START_DOCUMENT:
							list = new ArrayList<SmsProvider.SmsInfo>();
							break;
						case XmlPullParser.START_TAG:
							if ("sms".equals(name)) {
								info = new SmsInfo();
							} else if ("address".equals(name)) {
								info.address = parser.nextText();
							} else if ("date".equals(name)) {
								info.date = Long.valueOf(parser.nextText());
							} else if ("type".equals(name)) {
								info.type = Integer.valueOf(parser.nextText());
							} else if ("body".equals(name)) {
								info.body = parser.nextText();
							}
							break;
						case XmlPullParser.END_TAG:
							if ("sms".equals(name)) {
								list.add(info);
							}
							break;
						default:
							break;
						}

						// 改变事件
						type = parser.next();
					} while (type != XmlPullParser.END_DOCUMENT);

					// 总数
					// if (listener != null) {
					// listener.onMax(list.size());
					// }

					publishProgress(TYPE_SIZE, list.size());

					// list数据
					// 插入数据
					ContentResolver cr = context.getContentResolver();

					Uri url = Uri.parse("content://sms/");
					for (int i = 0; i < list.size(); i++) {
						SmsInfo sms = list.get(i);

						ContentValues values = new ContentValues();
						values.put("address", sms.address);
						values.put("date", sms.date);
						values.put("type", sms.type);
						values.put("body", sms.body);
						cr.insert(url, values);

						// 进度
						// if (listener != null) {
						// listener.onProgress(i);
						// }

						publishProgress(TYPE_PROGRESS, i);
					}

					// 成功
					// if (listener != null) {
					// listener.onSucess();
					// }

					return true;
				} catch (Exception e) {
					e.printStackTrace();
					// 失败
					// if (listener != null) {
					// listener.onFailed();
					// }
					return false;
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();

							// 失败
							if (listener != null) {
								listener.onFailed();
							}
						}
						is = null;
					}
				}
			}

			protected void onProgressUpdate(Integer[] values) {
				if (values[0] == TYPE_SIZE) {
					// mTvMax.setText("总数:" + values[1]);

					if (listener != null) {
						listener.onMax(values[1]);
					}
				} else {
					// mTvProgress.setText("进度:" + values[1]);

					if (listener != null) {
						listener.onProgress(values[1]);
					}
				}
			};

			protected void onPostExecute(Boolean result) {
				if (result) {
					// mTvMax.setText("成功");

					if (listener != null) {
						listener.onSucess();
					}
				} else {
					// mTvMax.setText("失败");

					if (listener != null) {
						listener.onFailed();
					}
				}
			};
		}.execute();
	}

	private static class SmsInfo {
		String address;
		long date;
		int type;
		String body;
	}
}
