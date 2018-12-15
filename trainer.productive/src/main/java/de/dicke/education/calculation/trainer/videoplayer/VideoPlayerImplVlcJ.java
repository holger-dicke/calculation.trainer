package de.dicke.education.calculation.trainer.videoplayer;

import java.awt.Container;
import java.sql.SQLException;

import de.dicke.education.calculation.trainer.common.VideoPlayerExeption;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class VideoPlayerImplVlcJ extends VideoPlayerBase {

	protected EmbeddedMediaPlayerComponent mediaPlayerComponent;
	// private long currentTimePosition;

	public VideoPlayerImplVlcJ() throws VideoPlayerExeption, SQLException {
		super();
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("OS type detected: " + os);
		if (os.contains("windows")) {
			System.out.println(os + " OS - special lib handling required");
		} else if (os.contains("linux") || os.contains("unix")) {
			System.out.println(os + " OS - no special lib handling required");
		} else {
			System.err.println(os + " ->unsupported OS -> Exit");
			System.exit(1);
		}

	}

	public void setMediaPlayerComponent(EmbeddedMediaPlayerComponent mediaPlayerComponent) {
		this.mediaPlayerComponent = mediaPlayerComponent;
	}

	public Container getAWTContainerObjMediaPlayer() {
		initializeMediaPlayerComponent();
		return mediaPlayerComponent;
	}

	protected void initializeMediaPlayerComponent() {

		if (mediaPlayerComponent == null) {
			System.out.println("Initializing VLCJ VideoPlayer ...");
			try {
				mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
				System.out.println("VideoPlayerImplVlcj: created MP component");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				System.out.println("Failure while initializing MP component -> exit");
				System.exit(2);
			}
		}
	}

	public void setMediaLength() throws SQLException {
		System.out.println("VLCJ setting media duration");
		long totalMediaDuration;
		if (settings.getTotalMediaDuration() < 1) {
			mediaPlayerComponent.getMediaPlayer().playMedia(settings.getMedia(), ":start-time=" + 1, ":stop-time=" + 1);
			mediaPlayerComponent.getMediaPlayer().parseMedia();
			totalMediaDuration = mediaPlayerComponent.getMediaPlayer().getLength() / 1000L;
			System.out.println("VLCJ derived media duration: " + totalMediaDuration);
			settings.setTotalMediaDuration(totalMediaDuration);
		}
	}

	@Override
	public void startPlayback(long tmpStartTime, long duration) throws VideoPlayerExeption, SQLException {
		setMediaLength();
		adjustDuration(tmpStartTime, duration);
		setCurrentTimePosition(tmpStartTime);
		long stopTime = getCurrentTimePosition() + duration;
		String validFilepath = settings.getMedia();

		if (validFilepath.equals("")) {
			throw new VideoPlayerExeption("Empty media path ");
		}

		System.out.println("start playing video from position" + getCurrentTimePosition() + "\t Duration = " + duration
				+ "\t stopTime = " + stopTime);

		initializeMediaPlayerComponent();
		mediaPlayerComponent.getMediaPlayer().setFullScreen(true);
		mediaPlayerComponent.getMediaPlayer().playMedia(validFilepath, ":start-time=" + getCurrentTimePosition(),
				":stop-time=" + stopTime);

		sleepSeconds(duration);
		while (true) {
			long time = mediaPlayerComponent.getMediaPlayer().getTime();
			System.err.println("Elapsed Time = " + time);
			if ((time >= stopTime) || (time < 0)) {
				System.err.println("Time is up -> stop video player");
				break;
			}
			sleepSeconds(2);
		}

		System.out.println("pause playing video");
		setCurrentTimePosition(getCurrentTimePosition() + duration);
	}

	public void sleepSeconds(long duration) {
		try {
			Thread.sleep(duration * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
