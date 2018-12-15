package de.dicke.education.calculation.trainer.datahandling;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;

abstract class SettingsImpl implements SettingsItf {

	protected DbAccess db;
	protected UserSettingsPersistor userSettingsPersistor = new UserSettingsPersistor();

	public SettingsImpl() throws SQLException {
		try {
			db = new H2DBAccess();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("Problem instantiating DataBase");
		}
	}

	@Override
	public Map<String, String> querryAllData() throws SQLException {
		return db.queryAllData();
	}

	public long getTotalMediaDuration() throws SQLException {
		return Integer.valueOf(db.queryData("totalMediaDuration"));
	}

	public void setTotalMediaDuration(long totalMediaDuration) throws SQLException {
		System.out.println("Setting media duration = " + totalMediaDuration);
		db.setData("totalMediaDuration", totalMediaDuration);
	}

	public long getCurrentPlayerPosition() throws SQLException {
		return userSettingsPersistor.getCurrentPlayerPostition();
	}

	public void setCurrentPlayerPosition(long currentPlayerPosition) throws SQLException {
		userSettingsPersistor.setCurrentPlayerPosition(currentPlayerPosition);
	}

	public int getVolume() throws SQLException {
		return Integer.valueOf(db.queryData("volume"));
	}

	public void setVolume(int volume) throws SQLException {
		db.setData("volume", volume);
	}

	public int getMaxResult() throws SQLException {
		return Integer.valueOf(db.queryData("maxResult"));
	}

	public void setMaxResult(int maxResult) throws SQLException {
		db.setData("maxResult", maxResult);
	}

	public int getMinResult() throws SQLException {
		return Integer.valueOf(db.queryData("minResult"));
	}

	public void setMinResult(int minResult) throws SQLException {
		db.setData("minResult", minResult);
	}

	public long getAccumulatedSecondsBeforeVideoIsShown() throws SQLException {
		return Integer.valueOf(db.queryData("accumulatedSecondsBeforeVideoIsShown"));
	}

	public void setAccumulatedSecondsBeforeVideoIsShown(long accumulatedSecondsBeforeVideoIsShown) throws SQLException {
		db.setData("accumulatedSecondsBeforeVideoIsShown", accumulatedSecondsBeforeVideoIsShown);
	}

	public String getMediaPath() {
		return userSettingsPersistor.getMediaDirectory();
	}

	public void setMediaPath(String mediaDir) {
		userSettingsPersistor.setMediaDirectory(mediaDir);
	}

	public String getMedia() {
		return userSettingsPersistor.getMediaFile();
	}

	public void setMedia(String media) {

		if (!Files.isRegularFile(Paths.get(media))) {
			InvalidPathException ipe = new InvalidPathException(media,
					"File or directory is not a regular file " + media);
			ipe.printStackTrace();
			throw ipe;
		}

		if (!userSettingsPersistor.getMediaFile().equals(media)) {

			userSettingsPersistor.setMediaFile(media);
			userSettingsPersistor.setCurrentPlayerPosition(1L);
			File fileWithPath = new File(media);
			userSettingsPersistor.setMediaDirectory(fileWithPath.getParentFile().getAbsolutePath());

			System.out.println("Setting media file path to " + userSettingsPersistor.getMediaFile());
			System.out.println("Setting media dir path to " + userSettingsPersistor.getMediaDirectory());
		} else {
			System.out.println("Continue with former media -> restoring media information and current player position from preferences.");
		}

	}

	public int getSecondsPerCorrectAnswer() throws SQLException {
		return Integer.valueOf(db.queryData("secondsPerCorrectAnswer"));
	}

	public void setSecondsPerCorrectAnswer(int secondsPerCorrectAnswer) throws SQLException {
		db.setData("secondsPerCorrectAnswer", secondsPerCorrectAnswer);
	}

	public int getMaxAllowedSecondsForAnswer() throws SQLException {
		return Integer.valueOf(db.queryData("maxAllowedSecondsForAnswer"));
	}

	public void setMaxAllowedSecondsForAnswer(int maxAllowedSecondsForAnswer) throws SQLException {
		db.setData("maxAllowedSecondsForAnswer", maxAllowedSecondsForAnswer);
	}

	public int getPenaltySeconds() throws SQLException {
		boolean penaltyOn = Boolean.valueOf(db.queryData("penaltyOn"));
		System.out.println("SettingsImpl: penalty is " + penaltyOn);
		if (penaltyOn == true) {
			return Integer.valueOf(db.queryData("penaltySeconds"));
		} else {
			return 0;
		}
	}

	public void setPenaltySeconds(int penaltySeconds) throws SQLException {
		db.setData("penaltySeconds", penaltySeconds);
	}

	public boolean isPenaltyOn() throws SQLException {
		return Boolean.valueOf(db.queryData("penaltyOn"));
	}

	public void setPenaltyOn(boolean penaltyOn) throws SQLException {
		db.setData("penaltyOn", penaltyOn);
	}

}
