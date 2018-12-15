package de.dicke.education.calculation.trainer.videoplayer;

import java.sql.SQLException;

import org.apache.log4j.BasicConfigurator;

import de.dicke.education.calculation.trainer.common.VideoPlayerExeption;
import de.dicke.education.calculation.trainer.datahandling.Settings;

public abstract class VideoPlayerBase implements VideoPlayer {

	protected Settings settings;

	public VideoPlayerBase() throws VideoPlayerExeption, SQLException {
		BasicConfigurator.configure();
		settings = new Settings();
		settings.setCurrentPlayerPosition(1L);
	}

	public long getCurrentTimePosition() throws VideoPlayerExeption {
		try {
			return settings.getCurrentPlayerPosition();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new VideoPlayerExeption("Can not get data from Settings obj");
		}
	}

	public void setCurrentTimePosition(long positionInSeconds) throws SQLException {
		settings.setCurrentPlayerPosition(positionInSeconds);
	}

	@Override
	public long getTotalDuration() throws VideoPlayerExeption {
		try {
			settings.getMedia();
			return settings.getTotalMediaDuration();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new VideoPlayerExeption("Can not get data from Settings obj");
		}
	}

	protected long adjustDuration(long startTime, long duration) throws VideoPlayerExeption {

		if (duration < 1) {
			throw new VideoPlayerExeption("duration is negative!");
		}
		if (startTime + duration > getTotalDuration()) {
			duration = getTotalDuration() - startTime;
		}

		return duration;
	}

	public void startPlayback() throws VideoPlayerExeption, SQLException {
		startPlayback(1L);
	}

	@Override
	public void startPlayback(long duration) throws VideoPlayerExeption, SQLException {
		startPlayback(getCurrentTimePosition(), duration);
	}

	@Override
	public void startPlayback(long startTime, long duration) throws VideoPlayerExeption, SQLException {
		throw new RuntimeException("Not yet implemented");
	}
}
