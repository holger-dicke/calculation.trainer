package de.dicke.education.calculation.trainer.generator;

import java.sql.SQLException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.dicke.education.calculation.trainer.common.Random;
import de.dicke.education.calculation.trainer.datahandling.Settings;

public class Generator {

	Settings settings;

	public Generator() throws SQLException {
		settings = new Settings();
		settings.setDefaults();
	}

	public FormulaDTO generate_simpleFormula(int num1Min, int num1Max, int num2Min, int num2Max, char operation)
			throws SQLException {
		String formula;
		int result;
		do {
			int num1 = getRandom(num1Min, num1Max);
			int num2 = getRandom(num2Min, num2Max);

			formula = makeFormula(num1, operation, num2);
			result = formulaResultInt(formula);
			System.out.println("formula = " + formula + "=" + result);
		} while (result > settings.getMaxResult() || result < settings.getMinResult());

		return new FormulaDTO(formula, formulaResultString(formula));
	}

	public FormulaDTO generate_quersumme(int numMin, int numMax, int maxQuersumme, int numOfDigits)
			throws SQLException {
		String formula;
		int quersumme;
		int number;
		do {
			quersumme = 0;
			number = 0;
			for (int idx = 1; idx <= numOfDigits; idx++) {
				int digit = getRandom(numMin, numMax);
				quersumme = quersumme +digit;
				number = number + digit * (int) (Math.pow(10, idx-1));
			}
			formula = "Wie lautet die Quersumme von " + number;
			System.out.println(formula + "\t quersumme=" + quersumme);
		} while (quersumme > maxQuersumme);

		return new FormulaDTO(formula, "?", String.valueOf(quersumme));
	}

	public FormulaDTO generate_simpleFormulaWithMultipleOperands(int numMin, int numMax, int numOfOperands,
			char operator) {

		String formula;
		int result;
		do {
			formula = String.valueOf(getRandom(numMin, numMax));
			for (int idx = 0; idx < numOfOperands - 1; idx++) {
				int operand = getRandom(numMin, numMax);

				formula = makeFormula(formula, operator, operand);
			}
			result = formulaResultInt(formula);
		} while (result > 100 || result < 0);

		return new FormulaDTO(formula, formulaResultString(formula));

	}

	public FormulaDTO generate_simpleFormulaDiv(int num1Min, int num1Max, int num2Min, int num2Max)
			throws SQLException {
		String formula = "";

		int result, num1, num2;
		do {
			num1 = getRandom(num1Min, num1Max);
			num2 = getRandom(num2Min, num2Max);
			result = formulaResultInt(makeFormula(num1, '*', num2));

		} while (result > settings.getMaxResult() || result < settings.getMinResult());

		formula = makeFormula(result, '/', num2);
		FormulaDTO formulaDTO = new FormulaDTO(formula, String.valueOf(num1));
		return formulaDTO;
	}
	
	public FormulaDTO generate_nachbarZahl(int num1Min, int num1Max, int exponent)
			throws SQLException {
		String formula = "";
		if (exponent < 1 || exponent > 10 ) {
			throw new IllegalArgumentException("value for exponent exceeded boundaries:" + exponent);
		} else {
			int zahl = (int) Math.pow(10, exponent);
		}

		
		int result;
		do {
			result = getRandom(num1Min, num1Max);

		} while (result > settings.getMaxResult() || result < settings.getMinResult());

		formula = "Was sind die Nachbar "+ (int) Math.pow(10, exponent) + "-er von " + result + " (Unterer Nachbar|Oberer Nachbar))?";
		FormulaDTO formulaDTO = new FormulaDTO(formula, String.valueOf(result));
		return formulaDTO;
	}
	

	public FormulaDTO generate_simpleSequence(int startMin, int startMax, int stepMin, int stepMax, int number,
			char operation) throws SQLException {
		int result, start, step;
		do {
			start = getRandom(startMin, startMax);
			step = getRandom(stepMin, stepMax);
			if (operation == '+') {
				result = start + step * number + step;
			} else if (operation == '-') {
				result = start - step * number - step;
			} else {
				throw new IllegalArgumentException("Unsupported operation" + operation);
			}

		} while (result > settings.getMaxResult() || result < settings.getMinResult());

		String formula = "";
		int tmp = 0;
		int tmpSolution = 0;
		for (int idx = 0; idx < number; idx++) {
			if (operation == '+') {
				tmp = start + step * idx;
				tmpSolution = tmp + step;
			} else if (operation == '-') {
				tmp = start - step * idx;
				tmpSolution = tmp - step;
			} else {
				throw new IllegalArgumentException("Unsupported operation" + operation);
			}

			formula = formula + " " + String.valueOf(tmp);
		}
		formula = "Wie lautet die nächste Zahl: " + formula;
		FormulaDTO formulaDTO = new FormulaDTO(formula, " ? ", String.valueOf(tmpSolution));
		return formulaDTO;
	}

	public FormulaDTO generateWhatSequenceAmIFromAdd(int seqMin, int seqMax, int number) throws SQLException {
		int op1, op2;
		char operation = '+';
		do {
			op1 = getRandom(seqMin, seqMax);
			op2 = getRandom(2, 10);

		} while (op1 * op2 + op2 * number > settings.getMaxResult());

		String formula = "";
		int tmp = 0;
		for (int idx = 0; idx < number; idx++) {
			if (operation == '+') {
				tmp = op1 * op2 + op2 * idx;
			} else {
				throw new IllegalArgumentException("Unsupported operation" + operation);
			}
			formula = formula + " " + String.valueOf(tmp);
		}
		return new FormulaDTO("Wie heisst die Zahlenreihe : " + formula, " ? ", String.valueOf(op2));
	}

	
	public FormulaDTO generateRoundUpDown(int num1Min, int num1Max, int zehnerPotenz) throws SQLException {
		float num1 = getRandom(num1Min, num1Max);
		int rounded = Math.round (num1/zehnerPotenz) * zehnerPotenz;
		if (zehnerPotenz != 10 && zehnerPotenz != 100 && zehnerPotenz != 1000) {
			throw new IllegalArgumentException("Invalid Zehnerpotenz!");
		}
		System.out.println( "Zahl = " + (int)num1 + "\tgerundet = " + rounded);
		
		return new FormulaDTO("Runde auf die " + zehnerPotenz +"er: " + (int)num1, String.valueOf(rounded));
	}
	
	public FormulaDTO generatePlatzhalter(int num1Min, int num1Max, int num2Min, int num2Max, char operation,
			char position) throws SQLException {
		String formula;
		int result, num1, num2;
		int solution;
		if (position != 'f') {
			System.err.println("position is currently not implemented in generatePlatzhalter!");
			System.exit(4);
		}

		do {
			num1 = getRandom(num1Min, num1Max);
			num2 = getRandom(num2Min, num2Max);

			formula = makeFormula(num1, operation, num2);
			result = formulaResultInt(formula);
			System.out.println("try formula = " + formula + "=" + result);
		} while (result > settings.getMaxResult() || result < settings.getMinResult());

		switch (position) {
		case 'f':
			formula = makeFormula("X", operation, num2);
			solution = num1;
			break;
		default:
			formula = makeFormula(num1, operation, "X");
			solution = num2;
			break;
		}
		formula = formula + " = " + result + " Wie lautet X";
		System.out.println("final formula = " + formula + "=" + solution);
		return new FormulaDTO(formula, " ? ", String.valueOf(solution));
	}

	public FormulaDTO generatePunktVorStrich(int addMin, int add1Max, int mult1Min, int mult1Max, int mult2Min,
			int mult2Max) throws SQLException {
		int result, num1, num2, num3;
		String formula;
		do {
			num1 = getRandom(addMin, add1Max);
			num2 = getRandom(mult1Min, mult1Max);
			num3 = getRandom(mult2Min, mult2Max);
			formula = makeFormula(num1, '+', makeFormula(num2, '*', num3));
			result = formulaResultInt(formula);

		} while (result > settings.getMaxResult() || result < settings.getMinResult());

		FormulaDTO formulaDTO = new FormulaDTO(formula, String.valueOf(result));
		return formulaDTO;
	}
	/*-------------------------------------------------------------------- */

	protected String formulaResultString(String formula) {
		return String.valueOf(formulaResultInt(formula));
	}

	protected int formulaResultInt(String formula) {
		int result;
		try {
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("JavaScript");
			result = (int) engine.eval(formula);
		} catch (ScriptException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not calculate result for formula " + formula);
		}
		return result;
	}

	protected String makeFormula(int operand1, char operator, int operand2) {
		return String.valueOf(operand1) + " " + operator + " " + String.valueOf(operand2);
	}

	protected String makeFormula(String formula, char operator, int operand2) {
		return formula + operator + String.valueOf(operand2);
	}

	protected String makeFormula(String formula1, char operator, String formula2) {
		return formula1 + operator + formula2;
	}

	protected String makeFormula(int operand1, char operator, String formula) {
		return String.valueOf(operand1) + operator + formula;
	}

	protected int getRandom(int num1Min, int num1Max) {
		return Random.getRandom(num1Min, num1Max);
	}

}
