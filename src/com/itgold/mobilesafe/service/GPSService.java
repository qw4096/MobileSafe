package com.itgold.mobilesafe.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.itgold.mobilesafe.utils.Constants;
import com.itgold.mobilesafe.utils.Logger;
import com.itgold.mobilesafe.utils.PreferenceUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class GPSService extends Service {

	private static final String TAG = "GPSService";
	private LocationManager mLm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Logger.d(TAG, "gps服务开启");

		mLm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		long minTime = 5 * 1000;// 最短获取gps信息的时间
		float minDistance = 10;// 最短的移动距离

		mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
				minDistance, listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Logger.d(TAG, "gps服务关闭");

		mLm.removeUpdates(listener);

	}

	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 当GPS芯片连接状态变化时 的回调
		}

		@Override
		public void onProviderEnabled(String provider) {
			// 当GPS芯片可用时的回调
		}

		@Override
		public void onProviderDisabled(String provider) {
			// 当GPS芯片不可用时的回调

		}

		@Override
		public void onLocationChanged(Location location) {
			// 位置改变时的回调

			double latitude = location.getLatitude();// 纬度
			double longitude = location.getLongitude();// 经度
			float accuracy = location.getAccuracy();// 精确度
			double altitude = location.getAltitude();// 海拔
			float bearing = location.getBearing();// 手机轴向
			float speed = location.getSpeed();// 手机运动的速度

			Logger.d(TAG, "latitude : " + latitude);
			Logger.d(TAG, "longitude : " + longitude);

			// 经纬度--》地址

			// 访问网络获取地址
			// 1. url
			// 2. method
			// 3. 请求参数
			// 4. 请求头
			// 请求行： url method
			// 请求头: key - value
			// 请求消息的内容
			// 请求参数： method --》 get--》url

			loadLocation(longitude, latitude);
			// TODO: 发短信

		}
	};

	private void loadLocation(final double longitude, final double latitude) {
		// 接口地址：http://lbs.juhe.cn/api/getaddressbylngb
		// 支持格式：JSON/XML
		// 请求方式：GET
		// 请求示例：http://lbs.juhe.cn/api/getaddressbylngb?lngx=116.407431&lngy=39.914492
		// 请求参数：
		// 名称 类型 必填 说明
		// lngx String Y google地图经度 (如：119.9772857)
		// lngy String Y google地图纬度 (如：27.327578)
		// dtype String N 返回数据格式：json或xml,默认json

		String url = "http://lbs.juhe.cn/api/getaddressbylngb";

		HttpUtils utils = new HttpUtils();
		utils.configTimeout(30 * 1000);
		utils.configSoTimeout(30 * 1000);
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("lngx", "" + longitude);
		params.addQueryStringParameter("lngy", "" + latitude);
		params.addQueryStringParameter("dtype", "json");

		// 头
		// params.addHeader(name, value)

		utils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException e, String msg) {
				// TODO 失败
				e.printStackTrace();

				sendSms("longitude:" + longitude + "  latitude:" + latitude);

				stopSelf();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO 成功
				String json = responseInfo.result;
				Logger.d(TAG, "json : " + json);

				// 解析json
				try {
					JSONObject root = new JSONObject(json);

					JSONObject rowObject = root.getJSONObject("row");
					JSONObject resultObject = rowObject.getJSONObject("result");
					String address = resultObject
							.getString("formatted_address");

					Logger.d(TAG, "address : " + address);

					sendSms("success.....");

					sendSms(address + "  longitude:" + longitude
							+ "  latitude:" + latitude);

					stopSelf();

				} catch (JSONException e) {
					e.printStackTrace();

					sendSms("longitude:" + longitude + "  latitude:" + latitude);

					stopSelf();
				}
			}
		});
	}

	private void sendSms(String text) {
		SmsManager sm = SmsManager.getDefault();
		String address = PreferenceUtils.getString(this, Constants.SJFD_NUMBER);
		Logger.d(TAG, "address : " + address);
		sm.sendTextMessage(address, null, text, null, null);
	}
}
