package de.dicke.education.calculation.trainer.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.dicke.education.calculation.trainer.datahandling.Settings;
import de.dicke.education.calculation.trainer.datahandling.UserSettingsPersistor;
import de.dicke.education.calculation.trainer.generator.ArithmeticProblemFactory;

public class BookKeeper extends Observable {

	protected static BookKeeper bookkeeper = null;
	protected static Settings settings;

	private UserSettingsPersistor usp = new UserSettingsPersistor();
	
	private List<ArithmeticProblem> listOfArithmeticProblems;

	protected int getNumOfPenalties() {
		return usp.getCurrentPenalties();
	}

	protected void setNumOfPenalties(int numOfPenalties) {
		usp.setCurrentPenalties(numOfPenalties);
	}

	protected void setNumOfCorrectAnswers(int numOfCorrectAnswers) {
		usp.setCurrentCorrectAnswers(numOfCorrectAnswers);
	}

	private BookKeeper() throws SQLException {

		listOfArithmeticProblems = new ArrayList<ArithmeticProblem>();
		generateNewArithmeticProblemAndAddItToList();
	}

	static public synchronized BookKeeper getInstance() throws SQLException {

		if (bookkeeper == null) {
			bookkeeper = new BookKeeper();
			settings = new Settings();
			settings.setDefaults();
		}

		return bookkeeper;
	}

	public int getTotalNumOfArithmeticProblems() {
		return listOfArithmeticProblems.size();
	}

	public int getNumOfCorrectAnswers() {
		return usp.getCurrentCorrectAnswers();
	}

	public ArithmeticProblem getCurrentArithmeticProblem() {
		return listOfArithmeticProblems.get(listOfArithmeticProblems.size() - 1);
	}

	public void updateDataBasedOnUserInput(String userInput) throws SQLException {
		ArithmeticProblem currentProblem = getCurrentArithmeticProblem();
		currentProblem.updateResultData(userInput);

		if (currentProblem.isSolved()) {
			usp.setCurrentCorrectAnswers(usp.getCurrentCorrectAnswers() + 1);
			if (currentProblem.hasPenalty()) {
				usp.setCurrentPenalties(usp.getCurrentPenalties() + 1);
			}
			generateNewArithmeticProblemAndAddItToList();
			System.out.println("Bookkeeper numOfCorrectAnswers=" + usp.getCurrentCorrectAnswers());

			setChanged(); // inform all observers that a problem is now finally solved
			System.out.println("BookKeeper: ->informing all Observers ...");
			notifyObservers();
		}
	}

	public boolean shallVideoBeShown() throws SQLException {

		System.out.println("BookKeeper: accumulated seconds = " + getNumOfSecondsToPlay());
		System.out.println("BookKeeper: configured = " + settings.getAccumulatedSecondsBeforeVideoIsShown());

		if (settings.getAccumulatedSecondsBeforeVideoIsShown() < getNumOfSecondsToPlay()) {
			System.out.println("BookKeeper: accumulated seconds is exceeding limit "
					+ settings.getAccumulatedSecondsBeforeVideoIsShown() + " -> video can be shown ..."
					+ getNumOfSecondsToPlay());
			return true;
		} else {
			System.out.println("BookKeeper: no show ...");
			return false;
		}

		// if ((numOfCorrectAnswers %
		// settings.getNumOfCorrectAnswersBeforeVideoIsShown()) == 0) {
		// System.out.println("BookKeeper: numOfCorrectAnswers is exceeding limit ->
		// video can be shown ...");
		// return true;
		// } else {
		// return false;
		// }
	}

	protected void generateNewArithmeticProblemAndAddItToList() throws SQLException {
		ArithmeticProblem currentProblem = ArithmeticProblemFactory.getInstance().getNewArithmeticProblem();
		listOfArithmeticProblems.add(currentProblem);
	}

	public ArithmeticProblem getLastUpdatedAP() throws IllegalAccessException {

		int numberOfAPUntilNow = listOfArithmeticProblems.size();
		ArithmeticProblem lastUpdatedAP = listOfArithmeticProblems.get(numberOfAPUntilNow - 1);

		if (lastUpdatedAP.getAllUserInputs().isEmpty()) {
			if (listOfArithmeticProblems.size() > 1) {
				lastUpdatedAP = listOfArithmeticProblems.get(numberOfAPUntilNow - 2);
			} else {
				throw new IllegalAccessException();
			}
		}

		return lastUpdatedAP;
	}

	public long getNumOfSecondsToPlay() throws SQLException {
		System.out.println("NumOfCorrectAnswers = " + usp.getCurrentCorrectAnswers());
		System.out.println("Seconds per correct answer = " + settings.getSecondsPerCorrectAnswer());
		System.out.println("NumOfPenalties = " + usp.getCurrentPenalties());
		System.out.println("PenaltySeconds =" + settings.getPenaltySeconds());
		System.out.println("Penalty = " + settings.isPenaltyOn());
		long secondsToPlay = usp.getCurrentCorrectAnswers() * settings.getSecondsPerCorrectAnswer()
				- usp.getCurrentPenalties() * settings.getPenaltySeconds();
		System.out.println("Seconds to play = " + secondsToPlay);
		return secondsToPlay;

	}

	public long resetCounters() throws SQLException {
		long secondsToPlay = getNumOfSecondsToPlay();
		usp.setCurrentCorrectAnswers(0);
		usp.setCurrentPenalties(0);
		System.out.println("Seconds to play = " + secondsToPlay);
		return secondsToPlay;

	}

	public boolean isLastUpdatedAPSolved() throws IllegalAccessException {
		return getLastUpdatedAP().isSolved();
	}

	public void addObserver(Observer o) {
		System.out.println("BookKeeper: adding observer");
		super.addObserver(o);
	}
}
