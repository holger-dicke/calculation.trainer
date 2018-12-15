package de.dicke.education.calculation.trainer.common;

public class InputTimeDO {

	private long seconds;
	private String userInput;

	public InputTimeDO(String userInput, long seconds) {
		this.userInput = userInput;
		this.seconds = seconds;
	}
	
	public String getUserInput () {
		return userInput;
	}

	
	public long getTime () {
		return seconds;
	}
}
