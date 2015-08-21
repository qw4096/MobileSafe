package com.itgold.mobilesafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import com.itgold.mobilesafe.R;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import org.acra.*;
import org.acra.annotation.*;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import com.itgold.mobilesafe.utils.Logger;

@ReportsCrashes(formUri = "http://188.188.2.100:8080/CrashWeb/CrashServlet", 
mode = ReportingInteractionMode.DIALOG,
resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
resDialogText = R.string.crash_dialog_text,
resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
resDialogEmailPrompt = R.string.crash_user_email_label, // optional. When defined, adds a user email text entry with this text resource as label. The email address will be populated from SharedPreferences and will be provided as an ACRA field if configured.
resDialogOkToast = R.string.crash_dialog_ok_toast) // optional. displays a Toast message when the user accepts to send a report.)
public class BaseApplication extends Application {

	private static final String TAG = "BaseApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		// 程序入口
		Logger.d(TAG, "应用程序启动");
		//
		// // 监控全局 初始化
		// Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
		//
		// // int a = 1;
		// // int b = 0;
		// // int c = a / b;

		// The following line triggers the initialization of ACRA
		ACRA.init(this);
//		ACRA.getErrorReporter().setReportSender(new ReportSender() {
//			
//			@Override
//			public void send(Context arg0, CrashReportData data)
//					throws ReportSenderException {
//				// TODO Auto-generated method stub
//				
//				
//			}
//		});
		
		

	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	final class CrashHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			Logger.d(TAG, "捕获异常");

			StringWriter sw = new StringWriter();
			PrintWriter err = new PrintWriter(sw);
			ex.printStackTrace(err);

			String result = sw.toString();

			try {
				FileOutputStream os = new FileOutputStream(new File(
						Environment.getExternalStorageDirectory(), "error.log"));

				os.write(result.getBytes());
				os.close();

				android.os.Process.killProcess(android.os.Process.myPid());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
