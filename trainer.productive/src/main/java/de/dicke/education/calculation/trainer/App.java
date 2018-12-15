package de.dicke.education.calculation.trainer;

import java.awt.EventQueue;

import de.dicke.education.calculation.trainer.gui.ApplicationCentralFrame;


public class App 
{
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					ApplicationCentralFrame frame = ApplicationCentralFrame.getInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
