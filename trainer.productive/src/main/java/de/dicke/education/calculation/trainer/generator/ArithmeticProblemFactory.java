package de.dicke.education.calculation.trainer.generator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import de.dicke.education.calculation.trainer.common.ArithmeticProblem;

public class ArithmeticProblemFactory {

	private static ArithmeticProblemFactory me = null;
	private static Generator generator;
	// protected List<Types> listOfTypes = new ArrayList<Types>(
	// Arrays.asList(Types.PLATZHALTER_ADD, Types.PLATZHALTER_SUB,
	// Types.PUNKT_VOR_STRICH, Types.WHAT_SEQ,
	// Types.SEQ_ADD, Types.SEQ_SUB, Types.SIMPLE_ADD, Types.SIMPLE_MULT,
	// Types.MULTIPLE_ADD,
	// Types.SIMPLE_MULT, Types.SIMPLE_DIV, Types.MULTIPLE_MULT, Types.SIMPLE_DIV,
	// Types.SEQ_ADD));

	public enum Types {
		SIMPLE_ADD_HIGH, SIMPLE_SUB_HIGH, PLATZHALTER_ADD_HIGH, QUERSUMME, ROUND_UP_DOWN_100, ROUND_UP_DOWN_10,
		PLATZHALTER_SUB_HIGH, SIMPLE_ADD, SIMPLE_SUB, SIMPLE_MULT, SIMPLE_DIV, MULTIPLE_ADD, MULTIPLE_MULT, 
		SEQ_ADD, SEQ_SUB, WHAT_SEQ, PUNKT_VOR_STRICH, PLATZHALTER_ADD, PLATZHALTER_SUB, PLATZHALTER_MULT;
	}

//	protected List<Types> listOfTypes = new ArrayList<Types>(Arrays.asList(Types.ROUND_UP_DOWN_10, Types.ROUND_UP_DOWN_100, 
//			Types.PLATZHALTER_MULT, Types.QUERSUMME, Types.SIMPLE_ADD_HIGH,
//			Types.PUNKT_VOR_STRICH, Types.SIMPLE_SUB_HIGH, Types.PLATZHALTER_ADD_HIGH, Types.PLATZHALTER_SUB_HIGH,
//			Types.MULTIPLE_ADD, Types.PUNKT_VOR_STRICH));

	protected List<Types> listOfTypes = new ArrayList<Types>(Arrays.asList( Types.SIMPLE_ADD, Types.SIMPLE_SUB));
	
	ListIterator<Types> iter;


	static public List<Types> getListOfTypes () {
		return getListOfTypes();
	}
	
	static public synchronized ArithmeticProblemFactory getInstance() throws SQLException {

		if (me == null) {
			me = new ArithmeticProblemFactory();
			generator = new Generator();
		}
		return me;
	}

	private ArithmeticProblemFactory() {
		resetIterator();
	}

	public Types getNextProblemType() {
		if (!iter.hasNext()) {
			System.out.println("ArithmeticProblem: reset iterator");
			resetIterator();
		}
		return iter.next();
	}

	protected void resetIterator() {
		iter = listOfTypes.listIterator();

	}

	protected FormulaDTO getNewProblem() throws SQLException {
		FormulaDTO formulaDTO;
		Types type = getNextProblemType();
		System.out.println("Generating formula for " + type.toString());
		switch (type) {
		case QUERSUMME:
			formulaDTO = generator.generate_quersumme(1, 9, 100, 3);
			break;
		case SIMPLE_ADD_HIGH:
			formulaDTO = generator.generate_simpleFormula(100, 990, 33, 400, '+');
			break;
		case ROUND_UP_DOWN_100:
			formulaDTO = generator.generateRoundUpDown(20, 999, 100);
			break;
		case ROUND_UP_DOWN_10:
			formulaDTO = generator.generateRoundUpDown(20, 999, 10);
			break;
		case SIMPLE_SUB_HIGH:
			formulaDTO = generator.generate_simpleFormula(300, 990, 100, 400, '-');
			break;
		case PLATZHALTER_ADD_HIGH:
			formulaDTO = generator.generatePlatzhalter(200, 999, 100, 500, '+', 'f');
			break;
		case PLATZHALTER_SUB_HIGH:
			formulaDTO = generator.generatePlatzhalter(200, 999, 200, 999, '-', 'f');
			break;
		case PLATZHALTER_MULT:
			formulaDTO = generator.generatePlatzhalter(2, 9, 2, 9, '*', 'f');
			break;

		case SIMPLE_ADD:
			formulaDTO = generator.generate_simpleFormula(0, 5, 1, 5, '+');
			break;
		case SIMPLE_SUB:
			formulaDTO = generator.generate_simpleFormula(1, 9, 1, 9, '-');
			break;
		case SIMPLE_MULT:
			formulaDTO = generator.generate_simpleFormula(2, 10, 2, 10, '*');
			break;
		case SIMPLE_DIV:
			formulaDTO = generator.generate_simpleFormulaDiv(2, 10, 2, 10);
			break;
		case MULTIPLE_ADD:
			formulaDTO = generator.generate_simpleFormulaWithMultipleOperands(10, 200, 4, '+');
			break;
		case MULTIPLE_MULT:
			formulaDTO = generator.generate_simpleFormulaWithMultipleOperands(2, 10, 3, '*');
			break;
		case SEQ_ADD:
			formulaDTO = generator.generate_simpleSequence(20, 80, 3, 11, 4, '+');
			break;
		case SEQ_SUB:
			formulaDTO = generator.generate_simpleSequence(20, 80, 3, 11, 4, '-');
			break;
		case WHAT_SEQ:
			formulaDTO = generator.generateWhatSequenceAmIFromAdd(2, 10, 4);
			break;
		case PUNKT_VOR_STRICH:
			formulaDTO = generator.generatePunktVorStrich(20, 60, 2, 9, 2, 10);
			break;
		case PLATZHALTER_ADD:
			formulaDTO = generator.generatePlatzhalter(2, 99, 2, 99, '+', 'f');
			break;
		case PLATZHALTER_SUB:
			formulaDTO = generator.generatePlatzhalter(2, 99, 2, 99, '-', 'f');
			break;
		default:
			System.out.println("No formula type found for " + type.toString());
			throw new RuntimeException("No formula type found for " + type.toString());
		}
		return formulaDTO;
	}

	public ArithmeticProblem getNewArithmeticProblem() throws SQLException {
		FormulaDTO formula = getNewProblem();
		ArithmeticProblem ap = new ArithmeticProblem(formula);
		return ap;

	}

}
