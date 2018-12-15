package de.dicke.education.calculation.trainer.videoplayer;

import java.awt.Container;
import java.sql.SQLException;

import de.dicke.education.calculation.trainer.common.VideoPlayerExeption;

public interface VideoPlayer {
	
	public void startPlayback() throws VideoPlayerExeption, SQLException ;
	public void startPlayback(long duration) throws VideoPlayerExeption, SQLException;
	public void startPlayback(long startTime, long duration) throws VideoPlayerExeption, SQLException;
	long getTotalDuration () throws VideoPlayerExeption;

	public Container getAWTContainerObjMediaPlayer();
		
	}
	