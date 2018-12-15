package de.dicke.education.calculation.trainer.datahandling;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.InvalidPathException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class SettingsTest {

	Settings settings;

	@Before
	public void setUp() throws Exception {
		settings = new Settings();
		settings.setDefaults();
	}

	@Test
	public final void testSetGetTotalMediaDuration() throws SQLException {
		long data = 99999999L;
		settings.setTotalMediaDuration(data);
		assertTrue(settings.getTotalMediaDuration() == data);
	}

	@Test
	public final void testSetGetCurrentPlayerPosition() throws SQLException {
		long data = 99999999L;
		settings.setCurrentPlayerPosition(data);
		assertTrue(settings.getCurrentPlayerPosition() == data);
	}

	@Test
	public final void testSetGetMaxResult() throws SQLException {
		int data = 100;
		settings.setMaxResult(data);
		assertTrue(settings.getMaxResult() == data);
	}

	@Test
	public final void testSetGetMinResult() throws SQLException {
		int data = 2;
		settings.setMinResult(data);
		assertTrue(settings.getMinResult() == data);
	}

	@Test
	public final void testSetGetMediaInvalidPathThrowsException() throws SQLException {
		try {
			String data = "Media/invalidPath";
			settings.setMedia(data);
			fail();
		} catch (InvalidPathException e) {
			assertTrue(true);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public final void testSetGetMediaValidPath() throws SQLException {
		try {
			String data = "/home/aussi/dev/trainer.productive/src/test/resources/SampleVideo.mp4";
			settings.setMedia(data);
			assertTrue(settings.getMedia().equals(data));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public final void testSetGetSecondsPerCorrectAnswer() throws SQLException {
		int data = 4;
		settings.setSecondsPerCorrectAnswer(data);
		assertTrue(settings.getSecondsPerCorrectAnswer() == data);
	}

	@Test
	public final void testSetGetMaxAllowedSecondsForAnswer() throws SQLException {
		int data = 5;
		settings.setMaxAllowedSecondsForAnswer(data);
		assertTrue(settings.getMaxAllowedSecondsForAnswer() == data);
	}

	@Test
	public final void testSetGetPenaltySeconds() throws SQLException {
		int data = 6;
		settings.setPenaltySeconds(data);
		settings.setPenaltyOn(false);
		assertTrue("received:" + settings.getPenaltySeconds(), settings.getPenaltySeconds() == 0);
		
		settings.setPenaltyOn(true);
		assertTrue("received:" + settings.getPenaltySeconds(), settings.getPenaltySeconds() == data);
	}
	
	@Test
	public final void testSetGetPenaltyOn() throws SQLException {
		boolean data = true;
		settings.setPenaltyOn(data);
		assertTrue(settings.isPenaltyOn() == data);
	}

	@Test
	public final void testSetDefaults() throws SQLException {
		settings.setDefaults();
	}
	
//	@Test
//	public final void testPrintAlldata() throws SQLException {
//		settings.setDefaults();
//		Map<String, String> aa = settings.querryAllData();
//	}
}
