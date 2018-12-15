package de.dicke.education.calculation.trainer.generator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.dicke.education.calculation.trainer.common.Random;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Random.class })
public class GeneratorTest {

	Generator generator;

	@Before
	public void setUp() throws Exception {

		generator = new Generator();

	}

	@Test
	public final void testMakeFormula() {
		String formula = generator.makeFormula(1, '+', 2);
		assertTrue(formula.replace(" ", "").equals("1+2"));

		formula = generator.makeFormula("3", '*', 4);
		assertTrue(formula.replace(" ", "").equals("3*4"));

		formula = generator.makeFormula("3*4", '+', "5*6");
		assertTrue(formula.replace(" ", "").equals("3*4+5*6"));
	}

	@Test
	public final void testCalculateResultForFormula() {

		String formula = "2+3*4-1";
		int result = generator.formulaResultInt(formula);
		assertTrue(result == 13);

		formula = "(2+3)*(4-1)";
		result = generator.formulaResultInt(formula);
		assertTrue(result == 15);

	}

	@Test
	public final void testGetRandom() {
		assertTrue(generator.getRandom(17, 17) == 17);

		int number = generator.getRandom(18, 19);
		assertTrue(number >= 18 && number <= 19);

		number = generator.getRandom(21, 11);
		assertTrue(number >= 11 && number <= 21);
	}

	@Test
	public final void testGenerate_roundUpDown() throws SQLException {
		try {
			generator.generateRoundUpDown(11, 11, 17);
			fail();

		} catch (IllegalArgumentException e) {
		}

		FormulaDTO formula = generator.generateRoundUpDown(11, 11, 10);
		assertTrue(formula.getSolution().equals("10"));
		assertTrue(formula.getFormula(), formula.getFormula().startsWith("Runde auf die 10er:"));
		System.out.println(formula.getFormula());
		formula = generator.generateRoundUpDown(111, 111, 10);
		assertTrue(formula.getSolution().equals("110"));
		formula = generator.generateRoundUpDown(119, 119, 10);
		assertTrue(formula.getSolution().equals("120"));
		formula = generator.generateRoundUpDown(115, 115, 10);
		assertTrue(formula.getSolution().equals("120"));
		formula = generator.generateRoundUpDown(611, 611, 100);
		assertTrue(formula.getSolution().equals("600"));
		assertTrue(formula.getFormula(), formula.getFormula().startsWith("Runde auf die 100er:"));
	}



	@Test
	public final void testGenerate_quersumme() throws SQLException {
		FormulaDTO formula = generator.generate_quersumme(1, 1, 10, 5);
		assertTrue(formula.getFormula().equals("Wie lautet die Quersumme von 11111"));
		assertTrue(Integer.valueOf(formula.getSolution()) == 5);
	}

	@Test
	public final void testGenerate_simpleFormulaWithMultipleOperands() {
		FormulaDTO formula = generator.generate_simpleFormulaWithMultipleOperands(4, 4, 5, '+');

		assertTrue(Integer.valueOf(formula.getSolution()) == 20);
	}

	@Test
	public final void testGenerate_simpleAddHighNumber() throws SQLException {
		FormulaDTO formula = generator.generate_simpleFormula(100, 900, 1, 99, '+');
		System.out.println("formula = " + formula.getFormula());

	}

	@Test
	public final void testGenerate_simpleFormulaWith2Operands() throws SQLException {
		FormulaDTO formula = generator.generate_simpleFormula(2, 2, 5, 5, '*');
		assertTrue(Integer.valueOf(formula.getSolution()) == 10);
	}

	@Test
	public void testDivision() throws SQLException {
		FormulaDTO formula = generator.generate_simpleFormulaDiv(8, 8, 4, 4);
		assertTrue(Integer.valueOf(formula.getSolution()) == 8);

		for (int idx = 0; idx < 10; idx++) {
			generator.generate_simpleFormulaDiv(2, 10, 2, 10).print();
		}
	}

	@Test
	public void testSimpleSequenceAdd() throws SQLException {
		FormulaDTO formulaDTO = generator.generate_simpleSequence(5, 5, 3, 3, 5, '+');
		String formula = formulaDTO.getFormula();
		System.out.println(formula);
		assertTrue("Received formula= " + formula, "Wie lautet die nächste Zahl:  5 8 11 14 17".equals(formula));
		assertTrue("Received solution =" + formulaDTO.getSolution(), formulaDTO.getSolution().equals("20"));
		assertTrue(formulaDTO.getEndString().equals(" ? "));

	}

	@Test
	public void testSimpleSequenceSub() throws SQLException {
		FormulaDTO formulaDTO = generator.generate_simpleSequence(20, 20, 3, 3, 5, '-');
		String formula = formulaDTO.getFormula();
		System.out.println(formula);
		assertTrue("Received formula= " + formula, "Wie lautet die nächste Zahl:  20 17 14 11 8".equals(formula));
		assertTrue("Received solution =" + formulaDTO.getSolution(), formulaDTO.getSolution().equals("5"));
		assertTrue(formulaDTO.getEndString().equals(" ? "));
	}

	@Test
	public void testWhatSequence() throws SQLException {
		mockStatic(Random.class);
		when(Random.getRandom(2, 10)).thenReturn(5);
		when(Random.getRandom(9, 9)).thenReturn(9);
		FormulaDTO formulaDTO = generator.generateWhatSequenceAmIFromAdd(9, 9, 4);
		formulaDTO.print();
		assertTrue("Received formula= " + formulaDTO.getFormula(),
				formulaDTO.getFormula().startsWith("Wie heisst die Zahlenreihe :"));
		assertTrue("Received solution =" + formulaDTO.getSolution(), formulaDTO.getSolution().equals("5"));
		assertTrue(formulaDTO.getEndString().equals(" ? "));
	}

	@Test
	public void testGeneratePlatzhalterSmallNumber() throws SQLException {
		mockStatic(Random.class);
		when(Random.getRandom(2, 9)).thenReturn(5);
		when(Random.getRandom(2, 8)).thenReturn(4);
		FormulaDTO formulaDTO = generator.generatePlatzhalter(2, 9, 2, 8, '-', 'f');
		formulaDTO.print();
		assertTrue("Received formula= " + formulaDTO.getFormula(),
				formulaDTO.getFormula().startsWith("X-4 = 1 Wie lautet X"));
		assertTrue("Received solution =" + formulaDTO.getSolution(), formulaDTO.getSolution().equals("5"));
		assertTrue(formulaDTO.getEndString().equals(" ? "));
	}

	@Test
	public void testGeneratePlatzhalterLargeNumber() throws SQLException {
		mockStatic(Random.class);
		when(Random.getRandom(200, 900)).thenReturn(900);
		when(Random.getRandom(200, 800)).thenReturn(800);
		FormulaDTO formulaDTO = generator.generatePlatzhalter(200, 900, 200, 800, '-', 'f');
		formulaDTO.print();
		assertTrue("Received formula= " + formulaDTO.getFormula(),
				formulaDTO.getFormula().startsWith("X-800 = 100 Wie lautet X"));
		assertTrue("Received solution =" + formulaDTO.getSolution(), formulaDTO.getSolution().equals("900"));
		assertTrue(formulaDTO.getEndString().equals(" ? "));
	}

	@Test
	public final void testGeneratePlatzhalterMult() throws SQLException {
		mockStatic(Random.class);
		when(Random.getRandom(2, 9)).thenReturn(5);
		when(Random.getRandom(2, 8)).thenReturn(4);
		FormulaDTO formulaDTO = generator.generatePlatzhalter(2, 9, 2, 8, '*', 'f');
		formulaDTO.print();
		assertTrue("Received formula= " + formulaDTO.getFormula(),
				formulaDTO.getFormula().startsWith("X*4 = 20 Wie lautet X"));
		assertTrue("Received solution =" + formulaDTO.getSolution(), formulaDTO.getSolution().equals("5"));
		assertTrue(formulaDTO.getEndString().equals(" ? "));
	}
	
	@Test
	public void testPunktForStrich() throws SQLException {
		FormulaDTO formulaDTO = generator.generatePunktVorStrich(2, 2, 3, 3, 5, 5);
		formulaDTO.print();
		String formula = StringUtils.deleteWhitespace(formulaDTO.getFormula());
		assertTrue("Received formula= " + formula, formula.startsWith("2+3*5"));
		assertTrue("Received solution =" + formulaDTO.getSolution(), formulaDTO.getSolution().equals("17"));
		assertTrue(formulaDTO.getEndString().equals(" = "));
	}
}
