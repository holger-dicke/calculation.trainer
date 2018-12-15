package de.dicke.education.calculation.trainer.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dicke.education.calculation.trainer.common.BookKeeper;
import de.dicke.education.calculation.trainer.common.VideoPlayerExeption;
import de.dicke.education.calculation.trainer.datahandling.Settings;
import uk.co.caprica.vlcj.player.MediaDetails;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcjinfo.MediaInfo;

class VideoIFrame extends JInternalFrame {

	private static final Logger logger = LoggerFactory.getLogger(VideoIFrame.class);

	private static final long serialVersionUID = 3001477144236016849L;

	private ApplicationCentralFrame applicationCentralFrame;

	private MediaPlayerFactory mediaPlayerFactory;

	private EmbeddedMediaPlayer mediaPlayer;

	private Canvas videoCanvas;

	private PlayerControlsPanel controlsPanel;

	private Settings settings = new Settings();

	public VideoIFrame(String s, ApplicationCentralFrame applicationCentralFrame)
			throws VideoPlayerExeption, SQLException {
		this.applicationCentralFrame = applicationCentralFrame;

		List<String> vlcArgs = new ArrayList<String>();

		vlcArgs.add("--no-snapshot-preview");
		vlcArgs.add("--quiet");
		vlcArgs.add("--quiet-synchro");
		vlcArgs.add("--intf");
		vlcArgs.add("dummy");

		videoCanvas = new Canvas();
		videoCanvas.setBackground(Color.black);
		videoCanvas.setSize(800, 600); // Only for initial layout

		mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		mediaPlayerFactory.setUserAgent("vlcj test player");

		FullScreenStrategy fullScreenStrategy = new DefaultFullScreenStrategy(applicationCentralFrame);
		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer(fullScreenStrategy);
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoCanvas));
		mediaPlayer.setPlaySubItems(true);

		mediaPlayer.setEnableKeyInputHandling(false);
		mediaPlayer.setEnableMouseInputHandling(false);

		controlsPanel = new PlayerControlsPanel(mediaPlayer);

		getContentPane().add(new JLabel(s, JLabel.CENTER), BorderLayout.CENTER);
		setName(s);
		setResizable(true);
		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
		setTitle(s);

		setLayout(new BorderLayout());
		setBackground(Color.black);
		add(videoCanvas, BorderLayout.CENTER);
		add(controlsPanel, BorderLayout.SOUTH);
		pack();

		setVisible(true);
		/* no title bar */
		InternalFrameUI ui = getUI();
		((BasicInternalFrameUI) ui).setNorthPane(null);

		mediaPlayer.addMediaPlayerEventListener(new TestPlayerMediaPlayerEventListener());
		
		addInternalFrameListener(listener);
	}

	public void sleepSeconds(long duration) {
		try {
			Thread.sleep(duration * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	InternalFrameListener listener = new InternalFrameListener() {
		public String frameName = "Video";

		public void internalFrameActivated(InternalFrameEvent e) {
			dumpInfo("Activated/Visible", e);
			playVideo();
		}

		private void playVideo() {
			try {
				System.out.println("Start playing video");
				mediaPlayer.enableOverlay(false);
				long startTime = settings.getCurrentPlayerPosition();
				long endTime = startTime + BookKeeper.getInstance().getNumOfSecondsToPlay();

				System.out.println(".... :start-time=" + startTime + ":stop-time=" + endTime);
				mediaPlayer.playMedia(new Settings().getMedia(), ":start-time=" + startTime, ":stop-time=" + endTime);
				mediaPlayer.enableOverlay(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		public void internalFrameClosed(InternalFrameEvent e) {
			dumpInfo("Closed", e);
		}

		public void internalFrameClosing(InternalFrameEvent e) {
			dumpInfo("Closing", e);
		}

		public void internalFrameDeactivated(InternalFrameEvent e) {
			dumpInfo("Deactivated", e);
		}

		public void internalFrameDeiconified(InternalFrameEvent e) {
			dumpInfo("Deiconified", e);
		}

		public void internalFrameIconified(InternalFrameEvent e) {
			dumpInfo("Iconified", e);
		}

		public void internalFrameOpened(InternalFrameEvent e) {
			dumpInfo("Opened", e);
		}

		private void dumpInfo(String s, InternalFrameEvent e) {
			System.out.println("Source : ( " + frameName + ")" + e.getInternalFrame().getName() + " : " + s);
		}
	};

	private final class TestPlayerMediaPlayerEventListener extends MediaPlayerEventAdapter {
		// @Override
		// public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media,
		// String mrl) {
		// logger.debug("mediaChanged(media={},mrl={})", media, mrl);
		// }
		//
		@Override
		public void finished(MediaPlayer mediaPlayer) {
			System.out.println("Event: Media Player has been finished");
			logger.debug("finished(mediaPlayer={})", mediaPlayer);

			try {
				System.out.println("... setting new currentplayer position to " + mediaPlayer.getTime()/1000);
				settings.setCurrentPlayerPosition(mediaPlayer.getTime()/1000);
				BookKeeper.getInstance().resetCounters();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			applicationCentralFrame.toggleJInternalFramesVisibiliy();
		}

		@Override
		public void stopped(MediaPlayer mediaPlayer) {
			System.out.println("Media Player has been stopped");
			logger.debug("stopped(mediaPlayer={})", mediaPlayer);
		}

		//
		//
		@Override
		public void paused(MediaPlayer mediaPlayer) {
			System.out.println("Media Player has been paused");
			logger.debug("paused(mediaPlayer={})", mediaPlayer);
		}

		//
		@Override
		public void playing(MediaPlayer mediaPlayer) {
			System.out.println("Event: Media Player is now playing");
			logger.debug("playing(mediaPlayer={})", mediaPlayer);
			MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
			logger.info("mediaDetails={}", mediaDetails);
			try {
				mediaPlayer.setVolume(settings.getVolume());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//
		// @Override
		// public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
		// logger.debug("videoOutput(mediaPlayer={},newCount={})", mediaPlayer,
		// newCount);
		// if (newCount == 0) {
		// return;
		// }
		//
		// MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
		// logger.info("mediaDetails={}", mediaDetails);
		//
		// MediaMeta mediaMeta = mediaPlayer.getMediaMeta();
		// logger.info("mediaMeta={}", mediaMeta);
		//
		// final Dimension dimension = mediaPlayer.getVideoDimension();
		// logger.debug("dimension={}", dimension);
		// if (dimension != null) {
		// SwingUtilities.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		// videoCanvas.setSize(dimension);
		// // mainFrame.pack();
		// }
		// });
		// }
		//
		// }
		//
		// @Override
		// public void error(MediaPlayer mediaPlayer) {
		// logger.debug("error(mediaPlayer={})", mediaPlayer);
		// }
		//
		// @Override
		// public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t
		// subItem) {
		// logger.debug("mediaSubItemAdded(mediaPlayer={},subItem={})", mediaPlayer,
		// subItem);
		// }
		//
		// @Override
		// public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
		// logger.debug("mediaDurationChanged(mediaPlayer={},newDuration={})",
		// mediaPlayer, newDuration);
		// }
		//
		// @Override
		// public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
		// logger.debug("mediaParsedChanged(mediaPlayer={},newStatus={})", mediaPlayer,
		// newStatus);
		// }
		//
		// @Override
		// public void mediaFreed(MediaPlayer mediaPlayer) {
		// logger.debug("mediaFreed(mediaPlayer={})", mediaPlayer);
		// }
		//
		// @Override
		// public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
		// logger.debug("mediaStateChanged(mediaPlayer={},newState={})", mediaPlayer,
		// newState);
		// }
		//
		// @Override
		// public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		// logger.debug("mediaMetaChanged(mediaPlayer={},metaType={})", mediaPlayer,
		// metaType);
		// }
	}
}
