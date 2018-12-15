package de.dicke.education.calculation.trainer.videoplayer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import de.dicke.education.calculation.trainer.common.VideoPlayerExeption;
import de.dicke.education.calculation.trainer.datahandling.Settings;

public class VideoPlayerImplGenericTest {

	String validFilepath = "/home/aussi/dev/trainer.productive/src/test/resources/SampleVideo.mp4";
	String invalidFilepath = "/home/aussi/dev/trainer.productive/src/test/resources/invalid.mp4";
	VideoPlayerImplVlcJ videoPlayer;
	Settings settings;

	@Before
	public void setUp() throws Exception {
		// preparing the data by making the mock
		settings = new Settings();
		settings.setDefaults();
		settings.setMedia(validFilepath);
		videoPlayer = new VideoPlayerImplVlcJ();
	}

	@Test
	public final void testGetTotalDuration() throws VideoPlayerExeption, SQLException {
		settings.setTotalMediaDuration(999L);
		long duration = videoPlayer.getTotalDuration();
		assertTrue(duration == 999L);
	}

	@Test
	public final void testGetCurrentPosition() throws VideoPlayerExeption, SQLException {
		settings.setCurrentPlayerPosition(111L);
		
		long duration = videoPlayer.getCurrentTimePosition();
		assertTrue("Duration = " + duration, duration == 111L);
	}

	@Test
	public final void testAdjustMediaDuration() throws VideoPlayerExeption, SQLException {

		settings.setTotalMediaDuration(999L);
		settings.setCurrentPlayerPosition(200L);
		assertTrue(videoPlayer.adjustDuration(1L, 100L) == 100L);

		settings.setCurrentPlayerPosition(998L);
		videoPlayer.adjustDuration(998L, 100L);

		try {
			settings.setCurrentPlayerPosition(88L);
			videoPlayer.adjustDuration(1L, -1L);
			fail();
		} catch (VideoPlayerExeption e) {
			/* expected */
		} catch (Exception e) {
			fail();
		}

	}
}
