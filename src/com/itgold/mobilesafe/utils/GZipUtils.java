package com.itgold.mobilesafe.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

	/**
	 * zip压缩流
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void zip(InputStream is, OutputStream os) throws IOException {
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(os);

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = is.read(buffer)) != -1) {

				// 写为zip
				gos.write(buffer, 0, len);
			}
		} finally {
			close(is);
			close(gos);
		}
	}

	/**
	 * zip压缩
	 * 
	 * @param srcFile
	 *            ：要压缩的文件
	 * @param zipFile
	 *            ：压缩后文件存放的地址
	 * @throws IOException
	 */
	public static void zip(File srcFile, File zipFile) throws IOException {
		GZIPOutputStream gos = null;
		FileInputStream fis = null;
		try {
			gos = new GZIPOutputStream(new FileOutputStream(zipFile));
			fis = new FileInputStream(srcFile);

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {

				// 写为zip
				gos.write(buffer, 0, len);
			}
		} finally {
			close(fis);
			close(gos);
		}
	}

	/**
	 * 解压zip流
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void unzip(InputStream is, OutputStream os)
			throws IOException {
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(is);

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = gis.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
		} finally {
			close(gis);
			close(os);
		}
	}

	/**
	 * 解压
	 * 
	 * @param zipFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void unzip(File zipFile, File targetFile) throws IOException {

		GZIPInputStream gis = null;
		FileOutputStream fos = null;
		try {
			gis = new GZIPInputStream(new FileInputStream(zipFile));
			fos = new FileOutputStream(targetFile);

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = gis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
		} finally {
			close(gis);
			close(fos);
		}
	}

	private static void close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			io = null;
		}
	}
}
