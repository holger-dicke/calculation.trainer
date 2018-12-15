package de.dicke.education.calculation.trainer.gui;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.sql.SQLException;

import org.junit.Test;

import de.dicke.education.calculation.trainer.datahandling.Settings;

public class FileChooserTest {

	@Test
	public final void testUpdateMediaDataInvalidPathNameFails() throws SQLException {

		String filePath = "Media/invalidPath";
		File data = new File(filePath);
		try {
			FileChooser.updateMediaData(data);
			fail();
		} catch (InvalidPathException e) {
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("unexpected Exception of type " + e.getClass().getName(), false);
		}
	}

	@Test
	public final void testUpdateMediaDataValidPathName() {

		String filePath = "/home/aussi/dev/trainer.productive/./src/test/resources/SampleVideo.mp4";
		File data = new File(filePath);
		try {
			FileChooser.updateMediaData(data);

			Settings settings = new Settings();
			assertTrue("Invalid File path " + settings.getMedia(), filePath.equals(settings.getMedia()));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("unexpected Exception of type" + e.getClass().getName(), false);
		}
	}
}
