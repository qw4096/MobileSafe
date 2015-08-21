package com.itgold.mobilesafe.test;

import com.itgold.mobilesafe.db.AntivirusDao;

import android.test.AndroidTestCase;

public class AntivirusDaoTest extends AndroidTestCase {

	public void testAdd() {

		AntivirusDao.add(getContext(), "4a515b37a9591cfa7d708e3790e60423");
	}

	public void testQuery() {
		boolean virus = AntivirusDao.isVirus(getContext(),
				"4a515b37a9591cfa7d708e3790e60423");
		assertEquals(true, virus);
	}
}
