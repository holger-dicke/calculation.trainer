package de.dicke.education.calculation.trainer.datahandling;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

public class DbAccessTest {

	String name = "dummyName";
	String data = "dummyData";
	DbAccess db;

	@Before
	public void setUp() throws Exception {
		db = new H2DBAccess();
	}

	@Test
	public final void testDoubleCreateDBAccessesSameDB() throws SQLException {
		db.setData("dummyName0", "dummyData0");

		DbAccess h2 = new H2DBAccess();
		assertTrue(h2.queryData("dummyName0").equals("dummyData0"));
	}

	@Test
	public final void testSetNewDataString() {
		try {
			db.setData("dummyNameString", "dummyDataString");
			assertTrue(db.queryData("dummyNameString").equals("dummyDataString"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public final void testSetNewDataInt() {
		try {
			db.setData("dummyInt", 1);
			assertTrue(db.queryData("dummyInt").equals("1"));

		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public final void testSetNewDataBool() {
		try {
			db.setData("dummyBool", true);
			assertTrue(db.queryData("dummyBool").equals("true"));
			assertTrue(Boolean.valueOf(db.queryData("dummyBool")) == true);

		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public final void testSetNewDataMAX_INTThrowsSQLException() {
		try {
			db.setData("dummyInt", Integer.MAX_VALUE);
			fail();
		} catch (SQLException e) {
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unexpected exception ");
			fail();
		}
	}

	@Test
	public final void testSetNewDataLong() {
		try {
			long number = 99999999L;
			db.setData("dummyLong", number);
			assertTrue(db.queryData("dummyLong").equals(String.valueOf(number)));
			assertTrue(Long.valueOf(db.queryData("dummyLong")).equals(number));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public final void testUpdateData() throws Exception {
		try {
			String dummyName = "dummyName2";
			db.setData(dummyName, "dummyData2");
			assertTrue(db.queryData(dummyName).equals("dummyData2"));

			db.setData(dummyName, "dummyData3");
			
			assertTrue(db.queryData(dummyName).equals("dummyData3"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public final void testQuerryAllData() throws SQLException {
		Map<String, String> input = new HashMap<String, String>();
		input.put("setting3", "value3");
		input.put("setting4", "value4");

		for (Entry<String, String> entry : input.entrySet()) {
			System.out.println("Adding entry to DB: " + entry.getKey() + "/" + entry.getValue());
			db.setData(entry.getKey(), entry.getValue());
		}

		Map<String, String> output = db.queryAllData();

		assertTrue("Number of rows is incorrect (expected: " + input.size() + "received: " + output.size() + ")",
				input.size() <= output.size());

		for (Entry<String, String> keyValuePair : input.entrySet()) {
			assertTrue("Missing key: " + keyValuePair.getKey(), output.containsKey(keyValuePair.getKey()));
			output.get(keyValuePair.getKey());
			assertTrue("Missing entry with key " + input.get(keyValuePair.getKey()),
					output.get(keyValuePair.getKey()).equals(input.get(keyValuePair.getKey())));
		}
	}

	@Test
	public final void testQueryData() throws SQLException {
		String name = "dummyName";
		String data = "dummyData";
		db.setData(name, data);
		db.queryData(name);
		assertTrue(data.equals(db.queryData(name)));
	}

	@Test
	public final void testQueryNonExistingDataThrowsException() {
		String name = "nonExistingName";
		try {
			db.queryData(name);
		} catch (SQLException e) {

		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

}
