package de.dicke.education.calculation.trainer.gui;

public class Bell {

	public static void beep() {
		java.awt.Toolkit.getDefaultToolkit().beep();
		System.out.print("\007");
	     System.out.flush();
		
	}
}
