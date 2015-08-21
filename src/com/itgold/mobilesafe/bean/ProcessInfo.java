package com.itgold.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class ProcessInfo {

	public Drawable icon;// 应用的图标
	public String name;// 应用名称
	public String packageName;// 应用名称
	public long memory;// 占用的内存
	public boolean isSystem;// 是否是系统进程
	public boolean isForeground;//是否是前台进程

	public boolean checked;// 标记是否选中

}
