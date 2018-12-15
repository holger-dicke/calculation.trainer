package de.dicke.education.calculation.trainer.generator;

public class FormulaDTO {

	String formula = "";
	String solution = "";
	private String endString = " = ";

	public FormulaDTO(String formula, String solution) {
		this.formula = formula;
		this.solution = solution;
	}
	
	public FormulaDTO(String formula, String delimiter, String solution) {
		this.formula = formula;
		this.solution = solution;
		this.endString = delimiter;
	}

	public String getFormula() {
		return formula;
	}

	public String getSolution() { 
		return solution;
	}

	public String getEndString() {
		return endString;
	}

	public void print() {
		System.out.println("Formula=" + formula + "\tDelimiter " + endString + ";\tSolution=" + solution);

	}

}
