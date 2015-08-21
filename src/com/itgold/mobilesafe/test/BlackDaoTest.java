package com.itgold.mobilesafe.test;

import com.itgold.mobilesafe.db.BlackDao;

import android.test.AndroidTestCase;

public class BlackDaoTest extends AndroidTestCase {

	public void testAdd() {
		BlackDao dao = new BlackDao(getContext());

		// boolean add = dao.add("5556", 0);
		//
		// assertEquals(true, add);

		for (int i = 0; i < 11; i++) {
			dao.add("135123456" + i, 2);
		}

	}

	public void testUpdate() {

		BlackDao dao = new BlackDao(getContext());

		assertEquals(true, dao.update("5556", 1));
	}

	public void testFind() {

		BlackDao dao = new BlackDao(getContext());

		assertEquals(-1, dao.findType("5556"));
	}

	public void testDelete() {
		BlackDao dao = new BlackDao(getContext());

		assertEquals(true, dao.delete("5556"));
	}

}
