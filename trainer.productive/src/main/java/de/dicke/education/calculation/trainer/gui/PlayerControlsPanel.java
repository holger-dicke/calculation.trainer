package de.dicke.education.calculation.trainer.gui;

/*
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2016 Caprica Software Limited.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import de.dicke.education.calculation.trainer.datahandling.Settings;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class PlayerControlsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	private final EmbeddedMediaPlayer mediaPlayer;

	private JLabel timeLabel;
	// private JProgressBar positionProgressBar;
	private JSlider positionSlider;

	private JButton pauseButton;
	private JButton playButton;
	private JButton toggleMuteButton;
	private JSlider volumeSlider;
	private JButton ejectButton;
	private JButton fullScreenButton;
	private JFileChooser fileChooser;

	private boolean mousePressedPlaying = false;

	public PlayerControlsPanel(EmbeddedMediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;

		createUI();

		executorService.scheduleAtFixedRate(new UpdateRunnable(mediaPlayer), 0L, 1L, TimeUnit.SECONDS);
	}

	private void createUI() {
		createControls();
		layoutControls();
		registerListenersForPanelUpdates();
	}

	private void createControls() {
		timeLabel = new JLabel("hh:mm:ss");

		positionSlider = new JSlider();
		positionSlider.setMinimum(0);
		positionSlider.setMaximum(1000);
		positionSlider.setValue(0);
		positionSlider.setToolTipText("Position");

		pauseButton = new JButton();
		URL url = PlayerControlsPanel.class.getResource("/resources/icons/control_pause_blue.png");
//		ImageIcon icon = new ImageIcon(url);
//		pauseButton.setIcon(icon);
		pauseButton.setToolTipText("Play/pause");
		

		playButton = new JButton();
		URL url1 = PlayerControlsPanel.class.getResource("/resources/icons/control_play_blue.png");
//		playButton.setIcon(new ImageIcon(url1));
		playButton.setToolTipText("Play");

		toggleMuteButton = new JButton();
		URL url2 = PlayerControlsPanel.class.getResource("/resources/icons/sound_mute.png");
//		toggleMuteButton.setIcon(new ImageIcon(url2));
		toggleMuteButton.setToolTipText("Toggle Mute");

		volumeSlider = new JSlider();
		volumeSlider.setOrientation(JSlider.HORIZONTAL);
		volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
		volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
		volumeSlider.setPreferredSize(new Dimension(100, 40));
		volumeSlider.setToolTipText("Change volume");

		ejectButton = new JButton();
		URL url3 = PlayerControlsPanel.class.getResource("/resources/icons/control_eject_blue.png");
//		ejectButton.setIcon(new ImageIcon(url3));
		ejectButton.setToolTipText("Load/eject media");

		fileChooser = new JFileChooser();
		fileChooser.setApproveButtonText("Play");
		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newVideoFileFilter());
		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newAudioFileFilter());
		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newPlayListFileFilter());
		FileFilter defaultFilter = SwingFileFilterFactory.newMediaFileFilter();
		fileChooser.addChoosableFileFilter(defaultFilter);
		fileChooser.setFileFilter(defaultFilter);

		fullScreenButton = new JButton();
		URL url4 = PlayerControlsPanel.class.getResource("/resources/icons/image.png");
//		fullScreenButton.setIcon(new ImageIcon(url4));
		fullScreenButton.setToolTipText("Toggle full-screen");

	}

	private void layoutControls() {
		setBorder(new EmptyBorder(4, 4, 4, 4));
		setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(8, 0));
		topPanel.add(timeLabel, BorderLayout.WEST);
		topPanel.add(positionSlider, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.add(pauseButton);
		bottomPanel.add(playButton);
		bottomPanel.add(volumeSlider);
		bottomPanel.add(toggleMuteButton);
//		bottomPanel.add(ejectButton);
		bottomPanel.add(fullScreenButton);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void setSliderBasedPosition() {
		if (!mediaPlayer.isSeekable()) {
			return;
		}
		float positionValue = positionSlider.getValue() / 1000.0f;
		// Avoid end of file freeze-up
		if (positionValue > 0.99f) {
			positionValue = 0.99f;
		}
		// TODO: calculate remaining play-duration for new position based on old start &
		// end time
		mediaPlayer.setPosition(positionValue);
	}

	private void updateUIState() {
		if (!mediaPlayer.isPlaying()) {
			// Resume play or play a few frames then pause to show current position in video
			mediaPlayer.play();
			if (!mousePressedPlaying) {
				try {
					// Half a second probably gets an iframe
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// Don't care if unblocked early
				}
				mediaPlayer.pause();
			}
		}
		long time = mediaPlayer.getTime();
		int position = (int) (mediaPlayer.getPosition() * 1000.0f);
		updateTimeDisplay(time);
		updatePositionSlider(position);
	}

	private void registerListenersForPanelUpdates() {

		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void playing(MediaPlayer mediaPlayer) {
				updateVolumeSlider(mediaPlayer.getVolume());
			}
		});

		positionSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (mediaPlayer.isPlaying()) {
					mousePressedPlaying = true;
					mediaPlayer.pause();
				} else {
					mousePressedPlaying = false;
				}
				setSliderBasedPosition();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				setSliderBasedPosition();
				updateUIState();
			}
		});

		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.pause();
			}
		});

		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.play();
			}
		});

		toggleMuteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.mute();
			}
		});

		volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				mediaPlayer.setVolume(source.getValue());
				try {
					new Settings().setVolume(source.getValue());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		ejectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.enableOverlay(false);
				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(PlayerControlsPanel.this)) {
					mediaPlayer.playMedia(fileChooser.getSelectedFile().getAbsolutePath());
				}
				mediaPlayer.enableOverlay(true);
			}
		});

		fullScreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.toggleFullScreen();
			}
		});

	}

	private final class UpdateRunnable implements Runnable {

		private final MediaPlayer mediaPlayer;

		private UpdateRunnable(MediaPlayer mediaPlayer) {
			this.mediaPlayer = mediaPlayer;
		}

		@Override
		public void run() {
			final long time = mediaPlayer.getTime();
			final int position = (int) (mediaPlayer.getPosition() * 1000.0f);

			// Updates to user interface components must be executed on the Event
			// Dispatch Thread
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (mediaPlayer.isPlaying()) {
						updateTimeDisplay(time);
						updatePositionSlider(position);
					}
				}
			});
		}
	}

	private void updateTimeDisplay(long millis) {
		String s = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		timeLabel.setText(s);
	}

	private void updatePositionSlider(int value) {
		// positionProgressBar.setValue(value);
		positionSlider.setValue(value);
	}

	private void updateVolumeSlider(int value) {
		volumeSlider.setValue(value);
	}

}