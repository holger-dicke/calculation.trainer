package de.dicke.education.calculation.trainer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import de.dicke.education.calculation.trainer.common.ArithmeticProblem;
import de.dicke.education.calculation.trainer.common.BookKeeper;
import de.dicke.education.calculation.trainer.common.InputTimeDO;
import de.dicke.education.calculation.trainer.datahandling.Settings;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class CalculationFrame extends JInternalFrame implements Observer {

	private JPanel contentPane;
	private JTextField txt_userInput;
	@SuppressWarnings("unused")
	private ApplicationCentralFrame applicationCentralFrame = null;
	private JTextArea txt_generated_question;
	private JTextArea historyDisplay;
	private JProgressBar progressBar;
	private JTextArea statisticsOutput;
	protected Settings settings;
	JButton btnPlayVideo;
	
	protected BookKeeper bookkeeper = BookKeeper.getInstance();

	/**
	 * Create the frame.
	 * 
	 * @throws SQLException
	 */
	public CalculationFrame(String frameDisplayName, ApplicationCentralFrame mainApplicationFrame) throws SQLException {

		bookkeeper.addObserver(this);
		settings = new Settings();

		ArithmeticProblem problem = bookkeeper.getCurrentArithmeticProblem();

		applicationCentralFrame = mainApplicationFrame;
		basicSettingsForJFrame(frameDisplayName);
		createBasicContentPane();
		createArithmeticProblemDisplayArea(problem);
		createStatisticArea();
		createUserInputArea(mainApplicationFrame, problem, txt_generated_question);

		JTextArea videoInformation = new JTextArea();
		videoInformation.setEditable(false);
		videoInformation.setFont(new Font("Dialog", Font.PLAIN, 14));
		videoInformation.setBounds(24, 578, 886, 15);

		videoInformation.setText(settings.getMedia());
		contentPane.add(videoInformation);

		statisticsOutput = new JTextArea();
		statisticsOutput.setLineWrap(true);
		statisticsOutput.setEditable(false);
		statisticsOutput.setFont(new Font("Dialog", Font.PLAIN, 14));
		statisticsOutput.setBounds(640, 137, 270, 238);
		contentPane.add(statisticsOutput);

		JLabel lblNewLabel_1 = new JLabel("Video Information");
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, 17));
		lblNewLabel_1.setBounds(24, 550, 191, 15);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Statistik");
		lblNewLabel_2.setFont(new Font("Dialog", Font.BOLD, 17));
		lblNewLabel_2.setBounds(640, 105, 175, 17);
		contentPane.add(lblNewLabel_2);

		progressBar = new JProgressBar();
		progressBar.setBounds(640, 409, 270, 20);
		updateProgressBar();
		contentPane.add(progressBar);

		addPlayVideoButton();
		
		updateStatisticOutput();

	}

	private void addPlayVideoButton() {
		btnPlayVideo = new JButton("Video abspielen");
		btnPlayVideo.setToolTipText("Video abspielen");
		btnPlayVideo.setEnabled(false);
		btnPlayVideo.setBounds(640, 450, 270, 25);
		contentPane.add(btnPlayVideo);

		btnPlayVideo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Button 'Play Video' has been pressed");
				btnPlayVideo.setEnabled(false);
				applicationCentralFrame.toggleJInternalFramesVisibiliy();
			};
		});
	}

	private void createUserInputArea(ApplicationCentralFrame mainApplicationFrame, ArithmeticProblem problem,
			JTextArea txt_generated_question) {
		txt_userInput = new JTextField() {
			/* the following is just to have the focus in the user-input field */
			public void addNotify() {
				super.addNotify();
				requestFocus();
			}
		};
		txt_userInput.setFont(new Font("Dialog", Font.PLAIN, 17));

		txt_userInput.setBounds(510, 57, 81, 20);
		txt_userInput.setColumns(10);

		txt_userInput.addActionListener(new ActionListener() {

			private String userInput;
			private String correctNotCorrect;

			public void actionPerformed(ActionEvent arg0) {
				userInput = txt_userInput.getText();
				correctNotCorrect = "Leider nicht Richtig";

				try {
					bookkeeper.updateDataBasedOnUserInput(userInput);

					ArithmeticProblem currAP = bookkeeper.getCurrentArithmeticProblem();

					updateStatisticOutput();

					txt_generated_question.setText(currAP.getProblem() + currAP.getProblemEndString());
					txt_userInput.setText("");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void updateStatisticOutput() throws SQLException {

				ArithmeticProblem lastUpdatedAP;

				try {
					lastUpdatedAP = bookkeeper.getLastUpdatedAP();
					if (lastUpdatedAP.isSolved()) {
						correctNotCorrect = "Richtig";
					} else {
						correctNotCorrect = "Nicht richtig";
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return;
				}

				List<InputTimeDO> allUserinputs = lastUpdatedAP.getAllUserInputs();
				int size = allUserinputs.size();

				String tmpText = historyDisplay.getText();
				String tmpLastUserInput = allUserinputs.get(size - 1).getUserInput();
				long tmpDuration = allUserinputs.get(size - 1).getTime();
				tmpText = lastUpdatedAP.getProblem() + " \t= " + tmpLastUserInput + "\t" + correctNotCorrect + " ("
						+ tmpDuration + " Sekunden)" + "\n" + tmpText;

				historyDisplay.setText(tmpText);
				historyDisplay.setCaretPosition(0);

				if (bookkeeper.shallVideoBeShown()) {
					btnPlayVideo.setEnabled(true);
				}
			}

		});

		contentPane.add(txt_userInput);
		txt_userInput.setText("");
	}

	private void basicSettingsForJFrame(String frameDisplayName) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(new JLabel(frameDisplayName, JLabel.CENTER), BorderLayout.CENTER);
		setName(frameDisplayName);
		setResizable(true);
		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
		setTitle(frameDisplayName);
		setVisible(true);
		/* no title bar */
		InternalFrameUI ui = getUI();
		((BasicInternalFrameUI) ui).setNorthPane(null);

		addInternalFrameListener(listener);
	}

	private void createStatisticArea() {
		historyDisplay = new JTextArea();
		historyDisplay.setFont(new Font("Dialog", Font.PLAIN, 14));
		historyDisplay.setBounds(24, 105, 567, 407);
		historyDisplay.setEditable(false);
		historyDisplay.setRows(5);
		historyDisplay.setBackground(Color.ORANGE);

		JScrollPane scrollPane = new JScrollPane(historyDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(24, 102, 567, 410);
		contentPane.add(scrollPane);
	}

	private void createArithmeticProblemDisplayArea(ArithmeticProblem problem) {
		/* text area for user input */
		txt_generated_question = new JTextArea();
		txt_generated_question.setFont(new Font("Dialog", Font.PLAIN, 17));
		txt_generated_question.setEditable(false);
		txt_generated_question.setBounds(24, 57, 480, 20);
		txt_generated_question.setText(problem.getProblem() + problem.getProblemEndString());
		contentPane.add(txt_generated_question);
	}

	private void createBasicContentPane() {

		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Rechentrainer für die Klassen 1 - 4");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 17));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 0, 368, 29);
		contentPane.add(lblNewLabel);
	}

	InternalFrameListener listener = new InternalFrameListener() {
		public String frameName = "Calculation";

		public void internalFrameActivated(InternalFrameEvent e) {
			dumpInfo("Activated/Visible " + frameName, e);
			txt_userInput.grabFocus();
		}

		public void internalFrameClosed(InternalFrameEvent e) {
			dumpInfo("Closed", e);
		}

		public void internalFrameClosing(InternalFrameEvent e) {
			dumpInfo("Closing", e);
		}

		public void internalFrameDeactivated(InternalFrameEvent e) {
			dumpInfo("Deactivated", e);
		}

		public void internalFrameDeiconified(InternalFrameEvent e) {
			dumpInfo("Deiconified", e);
		}

		public void internalFrameIconified(InternalFrameEvent e) {
			dumpInfo("Iconified", e);
		}

		public void internalFrameOpened(InternalFrameEvent e) {
			dumpInfo("Opened", e);
		}

		private void dumpInfo(String s, InternalFrameEvent e) {
			System.out.println("Source : ( " + frameName + ")" + e.getInternalFrame().getName() + " : " + s);
		}
	};

	@Override
	public void update(Observable arg0, Object arg1) {
		updateProgressBar();
		updateStatisticOutput();
	}

	public void updateProgressBar() {
		try {
			long pb;
			pb = (long) ((double) settings.getCurrentPlayerPosition() / settings.getTotalMediaDuration() * 100);
			progressBar.setValue((int) pb);
			System.out.println("Set progress bar to " + pb);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void updateStatisticOutput() {

		try {
			if (bookkeeper.shallVideoBeShown()) {
				btnPlayVideo.setEnabled(true);
			}
			String correctAnswers = "Anzahl richtiger Antworten: "
					+ String.valueOf((bookkeeper.getTotalNumOfArithmeticProblems() - 1)) + "\n";

			String maxAllowedSeconds;
			maxAllowedSeconds = "Max. erlaubte Zeit pro Aufgabe:" + settings.getMaxAllowedSecondsForAnswer() + "Sec.\n";
			String gutschriftProRichtigeAufgabe = "Je richtige Aufgabe: " + settings.getSecondsPerCorrectAnswer()
					+ "Sec.\n";
			String penaltySeconds = "Penalty Sec: " + settings.getPenaltySeconds() + ("Sec.\n");
			String penaltyOn = "Penalty is : " + settings.isPenaltyOn() + ("\n");
			String accumulatedTime = "Zeitguthaben: " + bookkeeper.getNumOfSecondsToPlay() + "Sec.\n";

			statisticsOutput.setText(maxAllowedSeconds + penaltyOn + penaltySeconds + gutschriftProRichtigeAufgabe
					+ correctAnswers + accumulatedTime);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
