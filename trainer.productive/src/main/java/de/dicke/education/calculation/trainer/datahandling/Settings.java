package de.dicke.education.calculation.trainer.datahandling;

import java.sql.SQLException;

public class Settings extends SettingsImpl {

	public Settings() throws SQLException  {
		super ();
	}

	public void setDefaults () throws SQLException {
		int secondsPerCorrectAnswer = 10;
		int maxAllowedSecondsForAnswer = 20;
		int penaltySeconds = (int) (secondsPerCorrectAnswer * 0.9);
		boolean penaltyOn = true;
		long accumulatedSecondsBeforeVideoIsShown = 180L; 
		int maxResult = 1000;
	
		long totalMediaDuration = 0L;
		int playerVolume = 200;
	
		int minResult = 1;

		setSecondsPerCorrectAnswer(secondsPerCorrectAnswer);
		setMaxAllowedSecondsForAnswer(maxAllowedSecondsForAnswer);
		setPenaltySeconds(penaltySeconds);
		setPenaltyOn(penaltyOn);
		setAccumulatedSecondsBeforeVideoIsShown(accumulatedSecondsBeforeVideoIsShown);
		setMaxResult(maxResult);
		setTotalMediaDuration(totalMediaDuration);
		setVolume(playerVolume);
		setMinResult(minResult);
		
	}
}
