package de.dicke.education.calculation.trainer.datahandling;

import static org.junit.Assert.*;

import java.util.prefs.BackingStoreException;

import org.junit.Before;
import org.junit.Test;

public class UserSettingsPersistorTest {

	private UserSettingsPersistor usp;

	@Before
	public void setUp() throws Exception {
	}

//	@Test
	public final void testSetCurrentPlayerPosition() throws InterruptedException {
		long orgValue = 10L;
		usp.setCurrentPlayerPosition(orgValue);
		Thread.sleep(2000L);
		long retrievedValue = usp.getCurrentPlayerPostition();
		assertTrue(orgValue == retrievedValue);
	}

	@Test
	public final void testXML() throws BackingStoreException {
		usp = new UserSettingsPersistor();
//		usp.setDummy("testString");
		String str = usp.exportXml();
//		System.out.println(str);
//		String replaced = str.replace("10", "33");
//		usp.importXml(replaced);
//		usp.exportXml();
	}

}
