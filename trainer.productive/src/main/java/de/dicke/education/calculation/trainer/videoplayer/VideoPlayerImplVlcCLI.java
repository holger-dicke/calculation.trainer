package de.dicke.education.calculation.trainer.videoplayer;

import java.awt.Container;
import java.io.IOException;
import java.sql.SQLException;

import de.dicke.education.calculation.trainer.common.VideoPlayerExeption;
import de.dicke.education.calculation.trainer.datahandling.Settings;

public class VideoPlayerImplVlcCLI implements VideoPlayer {

	protected String mediaFilePath = "";
	private long totalDuration;

	final String FULLSCREEN_ON = "--fullscreen";
	final String FULLSCREEN_OFF = "";


	@Override
	public void startPlayback() throws VideoPlayerExeption, SQLException {
		startPlayback(0, 0);
	}

	public void startPlayback(long startTime) throws VideoPlayerExeption, SQLException {
		startPlayback(startTime, 0);
	}

	public void startPlayback(long startTime, long duration) throws VideoPlayerExeption, SQLException {
		Settings settings = new Settings ();

		if ((startTime < 0) || (startTime > totalDuration)) {
			throw new VideoPlayerExeption("StartTime out of boundary range (" + startTime + ")");
		}
		String vlcCLICmd = "vlc " + FULLSCREEN_OFF + " --start-time=" + startTime + " --stop-time=" + startTime
				+ duration + " --play-and-exit " + settings.getMedia();
		try {
			Process p;
			p = Runtime.getRuntime().exec(vlcCLICmd);
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			throw new VideoPlayerExeption("Problem with Videoplayer execution");
		}
	}

	@Override
	public Container getAWTContainerObjMediaPlayer() {
		throw new RuntimeException("Not yet implemented)");
		// return null;
	}

	@Override
	public long getTotalDuration() throws VideoPlayerExeption {
		throw new VideoPlayerExeption("Not yet implemented");

	}

}
