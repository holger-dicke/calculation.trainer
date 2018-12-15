package de.dicke.education.calculation.trainer.generator;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.dicke.education.calculation.trainer.common.ArithmeticProblem;
import de.dicke.education.calculation.trainer.generator.ArithmeticProblemFactory.Types;

public class ArithmeticProblemFactoryTest {

	private ArithmeticProblemFactory ptf;

	@Before
	public void setUp() throws Exception {
		ptf = ArithmeticProblemFactory.getInstance();
	}

	@Test
	public final void testGetNextProblemTypeDoesResetWhenExceeded() {
		List <Types> list = new ArrayList<>();
		for (int idx=1; idx <= ptf.listOfTypes.size()*2; idx++) {
			Types type = ptf.getNextProblemType();			
			list.add(type);
		}
		
		for (int idx=0; idx <= ptf.listOfTypes.size()-1; idx++) {
			assertTrue(list.get(idx)==list.get(ptf.listOfTypes.size()+idx));			
		}
	}

	@Test
	public final void testGetNextProblemTypeHoldsState() {
		List <Types> list = new ArrayList<>();
		ptf.resetIterator();
		for (int idx=1; idx <= ptf.listOfTypes.size(); idx++) {
			Types type = ptf.getNextProblemType();			
			list.add(type);
		}
		
		for (int idx=0; idx <= ptf.listOfTypes.size()-1; idx++) {
			assertTrue("idx = " + idx +" Expected: " + list.get(idx) + " received: " + ptf.listOfTypes.get(idx), list.get(idx)==ptf.listOfTypes.get(idx));			
		}
	}
	
	@Test
	public final void testGetNewArithmeticProblem() throws SQLException {
		ArithmeticProblem ap = ptf.getNewArithmeticProblem();
		assertTrue(!ap.getProblem().isEmpty());
		assertTrue(!ap.getSolution().isEmpty());
	}

}
