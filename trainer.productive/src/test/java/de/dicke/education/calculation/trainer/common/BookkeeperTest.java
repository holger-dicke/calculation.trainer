package de.dicke.education.calculation.trainer.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import de.dicke.education.calculation.trainer.datahandling.Settings;

public class BookkeeperTest {

	static BookKeeper bookKeeper;
	Settings settings;

	@Before
	public void setUp() throws Exception {
		bookKeeper = BookKeeper.getInstance();
		bookKeeper.resetCounters();
		settings = new Settings();
		settings.setDefaults();
	}

	@Test
	public final void testBookKeeperIsSingelton() {
		try {
			assertTrue(bookKeeper == BookKeeper.getInstance());
			assertTrue(bookKeeper.equals(BookKeeper.getInstance()));
			assertTrue(bookKeeper.equals(BookKeeper.getInstance()));
		} catch (SQLException e) {
			e.printStackTrace();
			fail ();
		}
	}

	@Test
	public final void testGetCurrentArithmeticProblem() {
		ArithmeticProblem ap = bookKeeper.getCurrentArithmeticProblem();
		assertNotNull(ap);
	}

	@Test
	public final void testGetNumOfSecondsToPlayPenaltyOff() throws SQLException {
		settings.setPenaltyOn(false);
		bookKeeper.setNumOfCorrectAnswers(2);
		bookKeeper.setNumOfPenalties(1);

		long seconds2Play = 2 * settings.getSecondsPerCorrectAnswer();

		assertTrue(bookKeeper.getNumOfSecondsToPlay() == seconds2Play);
	}

	@Test
	public final void testGetNumOfSecondsToPlayPenaltyOn() throws SQLException {
		
		settings.setPenaltyOn(true);
		bookKeeper.setNumOfCorrectAnswers(2);
		bookKeeper.setNumOfPenalties(1);

		long seconds2Play = 2 * settings.getSecondsPerCorrectAnswer() - 1 * settings.getPenaltySeconds();

		assertTrue(bookKeeper.getNumOfSecondsToPlay() == seconds2Play);
	}
	
	@Test
	public final void testGettersAndSetters () {
		bookKeeper.setNumOfPenalties(17);
		assertTrue(bookKeeper.getNumOfPenalties() == 17);
		
		bookKeeper.setNumOfCorrectAnswers(32);
		assertTrue(bookKeeper.getNumOfCorrectAnswers() == 32);
	}
	
	@Test
	public final void testUpdateDataBasedOnUserInput() throws IllegalAccessException, SQLException {
		settings.setAccumulatedSecondsBeforeVideoIsShown(1L);
		bookKeeper.updateDataBasedOnUserInput("False Input");
		assertFalse(bookKeeper.hasChanged()); // for observer notification

		ArithmeticProblem lastAP = bookKeeper.getLastUpdatedAP();
		String lastInput = lastAP.getAllUserInputs().get(lastAP.getAllUserInputs().size() - 1).getUserInput();
		assertFalse(lastInput.equals(lastAP.getSolution()));

		bookKeeper.updateDataBasedOnUserInput(lastAP.getSolution());
		assertTrue(lastAP.isSolved());

		assertFalse(bookKeeper.hasChanged()); // for observer notification

	}

	@Test
	public final void testGenerateNewArithmeticProblemAndAddItToList() throws SQLException {
		ArithmeticProblem ap = bookKeeper.getCurrentArithmeticProblem();

		bookKeeper.generateNewArithmeticProblemAndAddItToList();
		assertFalse(bookKeeper.getCurrentArithmeticProblem().equals(ap));
	}

	@Test
	public final void testGetLastUpdatedAP() throws IllegalAccessException, SQLException {
		ArithmeticProblem ap1 = bookKeeper.getCurrentArithmeticProblem();
		bookKeeper.updateDataBasedOnUserInput(ap1.getSolution());
		assertTrue(ap1.equals(bookKeeper.getLastUpdatedAP()));

		ArithmeticProblem ap2 = bookKeeper.getCurrentArithmeticProblem();
		assertTrue(ap1.equals(bookKeeper.getLastUpdatedAP()));
		assertFalse(ap1.equals(ap2));

		bookKeeper.updateDataBasedOnUserInput("update latest AP");
		assertFalse(ap1.equals(bookKeeper.getLastUpdatedAP()));
	}

	@Test
	public final void testIsLastUpdatedAPSolved() throws IllegalAccessException, SQLException {

		ArithmeticProblem ap = bookKeeper.getCurrentArithmeticProblem();
		bookKeeper.updateDataBasedOnUserInput("False Input");
		assertFalse(bookKeeper.isLastUpdatedAPSolved());

		bookKeeper.updateDataBasedOnUserInput(ap.getSolution());
		assertTrue(bookKeeper.isLastUpdatedAPSolved());

	}
	
	@Test
	public void testShallVideoBeShown() throws SQLException {
		bookKeeper.setNumOfCorrectAnswers(1);
		int secsPerAnswer = settings.getSecondsPerCorrectAnswer();
		settings.setAccumulatedSecondsBeforeVideoIsShown(secsPerAnswer * 99);
		
		assertFalse(bookKeeper.shallVideoBeShown());
		
		bookKeeper.setNumOfCorrectAnswers(2);
		assertFalse(bookKeeper.shallVideoBeShown());
		
		bookKeeper.resetCounters();
		bookKeeper.setNumOfCorrectAnswers(100);
		assertTrue(bookKeeper.shallVideoBeShown());
		
		

	}

}
