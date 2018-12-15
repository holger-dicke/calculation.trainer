package de.dicke.education.calculation.trainer.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dicke.education.calculation.trainer.generator.FormulaDTO;

public class ArithmeticProblem {
	private String problemString = "";
	private String solutionString = "";
	private String problemEndString = "=";
	private boolean penalty = false;
	protected long startTime = 0L;
	protected long duration = 0L;
	List<InputTimeDO> listOfUserInputs = new ArrayList<InputTimeDO>();

	@SuppressWarnings("unused")
	private ArithmeticProblem() throws IllegalAccessException {
		throw new IllegalAccessException("Illegal constructor used");
	}

	public ArithmeticProblem(FormulaDTO formula) {
		setProblem(formula.getFormula());
		setSolution(formula.getSolution());
		problemEndString = formula.getEndString();
		startTime = System.currentTimeMillis()/1000;
	}

	public boolean hasPenalty() {
		return penalty;
	}

	public String getProblemEndString() {
		return problemEndString;
	}
	
	public String getProblem() {
		return problemString;
	}

	public void setProblem(String problem) {
		this.problemString = problem;
	}

	public String getSolution() {
		return solutionString;
	}

	/* for JUNIT */
	public void setSolution(String solution) {
		this.solutionString = solution;
	}

	public boolean isUserInputCorrect(String solution) {
		if (getSolution().equals(solution)) {
			return true;
		}
		return false;
	}

	public int getNumOfTries() {
		return listOfUserInputs.size();
	}

	public void updateResultData(String userInput) {
		listOfUserInputs.add(new InputTimeDO(userInput, System.currentTimeMillis()/1000 - startTime));
		if (!isUserInputCorrect(userInput)) {
			setPenalty();
		} else {
			duration = System.currentTimeMillis()/1000 - startTime;
			System.out.println("... Solved. Duration = " + duration);
		}
	}

	private void setPenalty() {
		this.penalty = true;
	}

	public boolean hasPenalty1() {
		return this.penalty;
	}

	public boolean isSolved() {
		if (listOfUserInputs.isEmpty()) {
			return false;
		} else {
			if (listOfUserInputs.get(listOfUserInputs.size() - 1).getUserInput().equals(solutionString)) {
				return true;
			}

		}
		return false;
	}

	public List<InputTimeDO> getAllUserInputs() {
		
		final List<InputTimeDO> fixedList = Collections.unmodifiableList(listOfUserInputs);
		return fixedList;
	}

	public void print() {
		System.out.println(problemString + "=" + solutionString);
	}
}
