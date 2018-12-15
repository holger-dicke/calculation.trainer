package de.dicke.education.calculation.trainer.common;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.dicke.education.calculation.trainer.generator.FormulaDTO;

public class ArithmeticProblemTest {

	private FormulaDTO formulaDTO;

	@Before
	public void setUp() throws Exception {
		formulaDTO = new FormulaDTO("1 + 2",  "=", "3");
	}

	@Test
	public final void testBasicConstructor()  {
	
		ArithmeticProblem ap = new ArithmeticProblem(formulaDTO);
		
		assertTrue(ap.getProblem().equals(formulaDTO.getFormula()));
		assertTrue(ap.getSolution().equals(formulaDTO.getSolution()));
		assertTrue(ap.getProblemEndString().equals(formulaDTO.getEndString()));
		
		assertTrue(ap.getNumOfTries() == 0);
		assertTrue(ap.hasPenalty() == false);
		assertTrue(ap.isSolved() == false);
	}

	@Test
	public final void testIsValidUserInputCorrect() {
	
		ArithmeticProblem ap = new ArithmeticProblem(formulaDTO);
		assertTrue(ap.isUserInputCorrect(formulaDTO.getSolution()) == true);
		assertTrue(ap.getNumOfTries() == 0);
		assertTrue(ap.hasPenalty() == false);
		assertTrue(ap.isSolved() == false);
	}

	@Test
	public final void testCheckThatFalseUserInputReturnsFalseWithoutDataUpdate() {
		ArithmeticProblem ap = new ArithmeticProblem(formulaDTO);
		assertTrue(ap.isUserInputCorrect("99") == false);
		assertTrue(ap.getNumOfTries() == 0);
		assertTrue(ap.hasPenalty() == false); 
		assertTrue(ap.isSolved() == false);
	}

	@Test
	public final void testUpdateUserInputCorrect()  {
		ArithmeticProblem ap = new ArithmeticProblem(formulaDTO);
		
		ap.updateResultData(formulaDTO.getSolution());

		assertTrue(ap.getNumOfTries() == 1);
		assertTrue(ap.hasPenalty() == false);
		assertTrue(ap.isSolved() == true);
	}

	@Test
	public final void testUpdateUserInputFalse() {
		ArithmeticProblem ap = new ArithmeticProblem(formulaDTO);
		ap.updateResultData("99");
		assertTrue(ap.getNumOfTries() == 1);
		assertTrue(ap.hasPenalty() == true);
		assertTrue(ap.isSolved() == false);

		ap.updateResultData("99");
		assertTrue(ap.getNumOfTries() == 2);
		assertTrue(ap.hasPenalty() == true);
		assertTrue(ap.isSolved() == false);

		ap.updateResultData(formulaDTO.getSolution());
		assertTrue(ap.getNumOfTries() == 3);
		assertTrue(ap.hasPenalty() == true);
		assertTrue(ap.isSolved() == true);
	}

	@Test
	public final void testStartAndDurationTimeStampsAreSet () throws InterruptedException {
	
		System.out.println("start new TC");
		long tmpStartTime = System.currentTimeMillis()/1000;
		Thread.sleep(1*1000);
		ArithmeticProblem ap = new ArithmeticProblem(formulaDTO);
		System.out.println(tmpStartTime - ap.startTime);
		assertTrue(tmpStartTime<ap.startTime);
		Thread.sleep(1*1000);
		assertTrue(ap.startTime<System.currentTimeMillis()/1000);
		
		ap.updateResultData(formulaDTO.getSolution());
		
		Thread.sleep(1*1000);
		assertTrue(ap.duration < (System.currentTimeMillis()/1000 - tmpStartTime));
		assertTrue(ap.duration > 0);
	}
	
	@Test
	public final void testGetAllUserInputsCanNotBeChanged() {
		ArithmeticProblem ap = new ArithmeticProblem(formulaDTO);
		ap.updateResultData(formulaDTO.getSolution());
		List<InputTimeDO> userInputList1 = ap.getAllUserInputs();

		assertTrue(userInputList1.size() == 1);

		String userInput = userInputList1.get(0).getUserInput();
		assertTrue(userInput.equals(formulaDTO.getSolution()));

		try {
			userInputList1.remove(0);
			fail();
		} catch (UnsupportedOperationException e) {
		}
		
		try {
			userInputList1.add (new InputTimeDO("99", 1));
			fail();
		} catch (UnsupportedOperationException e) {
		}
	}
	
}
