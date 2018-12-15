package de.dicke.education.calculation.trainer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.dicke.education.calculation.trainer.common.BookKeeper;
import de.dicke.education.calculation.trainer.common.VideoPlayerExeption;

public class ApplicationCentralFrame extends JFrame  {

	private static final long serialVersionUID = -6277410257615410486L;
	static List<JInternalFrame> jiList = new ArrayList<JInternalFrame>();
	static JInternalFrame videoFrame;
	static JInternalFrame calculationFrame;
	static ApplicationCentralFrame applicationMainFrame = null;

	/*                            */

	static public synchronized ApplicationCentralFrame getInstance() throws VideoPlayerExeption, SQLException {

		if (applicationMainFrame == null) {
			applicationMainFrame = new ApplicationCentralFrame();
		}

		return applicationMainFrame;
	}

	/*                           */

	public ApplicationCentralFrame() throws VideoPlayerExeption, SQLException {

		ApplicationCentralFrameBase();

	}

	public void ApplicationCentralFrameBase() throws VideoPlayerExeption, SQLException {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int sheight = screenSize.height;
		int swidth = screenSize.width;
		setBounds(0, 0, swidth, sheight - 40);
		addExitBehavioutForApplication(this);

		JLayeredPane desktop = createDesktopPaneAndAddItToJFrame(this);

		setVisible(true);
		getMediaFileFromUser();

		createAndAddJInternalFrames(desktop);
		setAllInternalFramesToMaxSize();

	}

	public void addExitBehavioutForApplication(JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
	}

	private JLayeredPane createDesktopPaneAndAddItToJFrame(JFrame frame) {
		Container contentPane = frame.getContentPane();
		JLayeredPane desktop = new JDesktopPane();
		desktop.setOpaque(false);
		contentPane.add(desktop, BorderLayout.CENTER);
		
		JTextPane applicationTitleText = new JTextPane();
		applicationTitleText.setBorder(new LineBorder(new Color(0, 0, 0)));
		applicationTitleText.setEditable(false);
		applicationTitleText.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 23));
		applicationTitleText.setBounds(267, 81, 637, 28);
		applicationTitleText.setText(" Rechentrainer Klasse 1 - 4 ");
		StyledDocument document = applicationTitleText.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		document.setParagraphAttributes(0, document.getLength(), center, false);

		
		desktop.add(applicationTitleText);
		
		
		return desktop;
	}

	private void setAllInternalFramesToMaxSize() {
		try {
			for (JInternalFrame internalFrame : jiList) {
				internalFrame.setMaximum(true);
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	private void createAndAddJInternalFrames(JLayeredPane desktop) throws VideoPlayerExeption, SQLException {
		System.out.println("Create & Start JInternalFrames");

		calculationFrame = new CalculationFrame("Calculation", this);
		desktop.add(calculationFrame, JLayeredPane.DEFAULT_LAYER);
		calculationFrame.setEnabled(true);
		calculationFrame.setVisible(true);
		jiList.add(calculationFrame);

		videoFrame = new VideoIFrame("Video", this);
		videoFrame.setEnabled(false);
		videoFrame.setVisible(false);
		desktop.add(videoFrame, JLayeredPane.DEFAULT_LAYER);
		jiList.add(videoFrame);

	}

	public void toggleJInternalFramesVisibiliy() {

		boolean videoStatus = videoFrame.isEnabled();

		videoFrame.setEnabled(false);
		videoFrame.setVisible(false);

		calculationFrame.setEnabled(false);
		calculationFrame.setVisible(false);

		if (videoStatus == false) {
			System.out.println("mainFramex: Set video isEnabled to " + true);
			System.out.println("mainFramex: Set video visibility to " + true);
			videoFrame.setEnabled(true);
			videoFrame.setVisible(true);
		} else {
			System.out.println("mainFramex: Set calculation isEnabled to " + true);
			System.out.println("mainFramex: Set calculation visibility to " + true);
			calculationFrame.setEnabled(true);
			calculationFrame.setVisible(true);
		}

	}

	public void playVideoIfPossible() {
		try {
			if (BookKeeper.getInstance().shallVideoBeShown()) {
				System.out.println("In Centra Frame Observer : toggle frames if enough play time has been accumulated");
				toggleJInternalFramesVisibiliy();			
			} else {
				System.out.println("In Central Frame Observer : not enough correct answers yet for video display");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	protected void getMediaFileFromUser() {
		try {
			new FileChooser();
		} catch (VideoPlayerExeption | SQLException e) {
			e.printStackTrace();
			System.exit(2);
		}

	}

}
