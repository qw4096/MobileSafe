package com.itgold.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.itgold.mobilesafe.bean.AppInfo;
import com.itgold.mobilesafe.utils.Logger;

public class AppInfoProvider {

	private static final String TAG = "AppInfoProvider";

	public static List<AppInfo> getAllApps(Context context) {
		PackageManager pm = context.getPackageManager();

		List<PackageInfo> packages = pm.getInstalledPackages(0);
		List<AppInfo> list = new ArrayList<AppInfo>();

		for (PackageInfo pack : packages) {
			AppInfo info = new AppInfo();

			info.packageName = pack.packageName;

			ApplicationInfo applicationInfo = pack.applicationInfo;
			info.name = applicationInfo.loadLabel(pm).toString();// 获得应用程序的名称
			info.icon = applicationInfo.loadIcon(pm);
			// applicationInfo.sourceDir//-->data/app/xxx.apk或者system/app/xxx.apk
			// applicationInfo.dataDir//-->data/data/包名/

			String sourceDir = applicationInfo.sourceDir;
			info.size = new File(sourceDir).length();
			//

			int flags = applicationInfo.flags;
			// 是否是系统程序 FLAG_SYSTEM
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// FLAG_SYSTEM :系统程序
				info.isSystem = true;
			} else {
				info.isSystem = false;
			}

			// 安装位置
			// info.isInstallSD;
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				// 外部存储
				info.isInstallSD = true;
			} else {
				info.isInstallSD = false;
			}

			Logger.d(TAG, "" + info.packageName);
			Logger.d(TAG, "" + info.name);
			Logger.d(TAG, "" + info.size);
			Logger.d(TAG, "系统：" + info.isSystem);
			Logger.d(TAG, "----------------------");

			list.add(info);
		}
		return list;
	}

	public static AppInfo getAppInfo(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();

		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(
					packageName, 0);

			AppInfo info = new AppInfo();
			info.packageName = packageName;
			info.name = applicationInfo.loadLabel(pm).toString();// 获得应用程序的名称
			info.icon = applicationInfo.loadIcon(pm);
			// applicationInfo.sourceDir//-->data/app/xxx.apk或者system/app/xxx.apk
			// applicationInfo.dataDir//-->data/data/包名/

			String sourceDir = applicationInfo.sourceDir;
			info.size = new File(sourceDir).length();
			//

			int flags = applicationInfo.flags;
			// 是否是系统程序 FLAG_SYSTEM
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// FLAG_SYSTEM :系统程序
				info.isSystem = true;
			} else {
				info.isSystem = false;
			}

			// 安装位置
			// info.isInstallSD;
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				// 外部存储
				info.isInstallSD = true;
			} else {
				info.isInstallSD = false;
			}

			return info;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
