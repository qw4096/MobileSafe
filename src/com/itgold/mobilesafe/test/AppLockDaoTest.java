package com.itgold.mobilesafe.test;

import com.itgold.mobilesafe.db.AppLockDao;

import android.test.AndroidTestCase;

public class AppLockDaoTest extends AndroidTestCase {

	public void testAdd() {
		AppLockDao dao = new AppLockDao(getContext());

		boolean add = dao.add("xxxx.aasd.dsss");
		assertEquals(true, add);
	}

	public void testFind() {
		AppLockDao dao = new AppLockDao(getContext());
		boolean findLock = dao.findLock("xxxx.aasd.dsss");

		assertEquals(false, findLock);
	}

	public void testDelete() {
		AppLockDao dao = new AppLockDao(getContext());
		boolean delete = dao.delete("xxxx.aasd.dsss");

		assertEquals(true, delete);
	}
}
