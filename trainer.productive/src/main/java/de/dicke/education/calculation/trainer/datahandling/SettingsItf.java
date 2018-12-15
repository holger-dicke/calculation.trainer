package de.dicke.education.calculation.trainer.datahandling;

import java.sql.SQLException;
import java.util.Map;

public interface SettingsItf {
	public long getTotalMediaDuration() throws SQLException;
	public void setTotalMediaDuration(long totalMediaDuration) throws SQLException; 

	public long getCurrentPlayerPosition() throws SQLException; 
	public void setCurrentPlayerPosition(long currentPlayerPosition)throws SQLException;
	
	public int getMaxResult()throws SQLException;
	public void setMaxResult(int maxResult)throws SQLException; 
	
	public int getMinResult()throws SQLException;
	public void setMinResult(int minResult)throws SQLException;
		
	public long getAccumulatedSecondsBeforeVideoIsShown () throws SQLException;
	public void setAccumulatedSecondsBeforeVideoIsShown (long accumulatedSecondsBeforeVideoIsShown)throws SQLException;

	public String getMedia(); 
	public void setMedia(String media); 
	
	public String getMediaPath(); 
	public void setMediaPath(String media); 

	public int getSecondsPerCorrectAnswer()throws SQLException; 
	public void setSecondsPerCorrectAnswer(int secondsPerCorrectAnswer)throws SQLException;
	
	public int getVolume()throws SQLException; 
	public void setVolume(int secondsPerCorrectAnswer)throws SQLException;
	
	public int getMaxAllowedSecondsForAnswer()throws SQLException; 
	public void setMaxAllowedSecondsForAnswer(int maxAllowedSecondsForAnswer)throws SQLException; 

	public int getPenaltySeconds()throws SQLException; 
	public void setPenaltySeconds(int penaltySeconds)throws SQLException; 

	public boolean isPenaltyOn()throws SQLException; 
	public void setPenaltyOn(boolean penaltyOn)throws SQLException; 
	
	public Map<String, String> querryAllData () throws SQLException;

}
