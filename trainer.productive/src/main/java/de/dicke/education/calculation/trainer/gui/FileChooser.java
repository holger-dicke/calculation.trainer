package de.dicke.education.calculation.trainer.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import de.dicke.education.calculation.trainer.common.VideoPlayerExeption;
import de.dicke.education.calculation.trainer.datahandling.Settings;
import de.dicke.education.calculation.trainer.datahandling.SettingsItf;
import de.dicke.education.calculation.trainer.datahandling.UserSettingsPersistor;

public class FileChooser {

	public FileChooser() throws VideoPlayerExeption, SQLException {
		String userHome = System.getProperty("user.home");
		System.out.println("User Home = " + userHome);

		SettingsItf settings = new Settings ();
		
		String mediaDir = settings.getMediaPath();
		if (mediaDir.equals("")) {
			System.out.println("Setting default media dir -> $HOME/Downloads");
			mediaDir = FileSystemView.getFileSystemView().getHomeDirectory() + "/Downloads";
		}
		
		JFileChooser fileChooser = new JFileChooser(mediaDir);
		fileChooser.setDialogTitle("Media File Auswahl");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		
		// set Details view for file chooser
		Action details = fileChooser.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);
		
		// set size of dialog window
		fileChooser.setPreferredSize(new Dimension(800, 600));

		FileNameExtensionFilter filter = new FileNameExtensionFilter("MP4 Media Files", "mp4");
		fileChooser.addChoosableFileFilter(filter);
		disableNewFileButton(fileChooser);

		int returnValue = fileChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			System.out.println("Selected file = \n" + fileChooser.getSelectedFile());
			File selectedFile = fileChooser.getSelectedFile();
			File path = selectedFile.getParentFile();
			System.out.println("Path = " + path.getAbsolutePath());
			System.out.println("Selected file with absolute path = \n" + selectedFile.getAbsolutePath());

			if (selectedFile.isFile() && selectedFile.canRead()) {
				updateMediaData(selectedFile);
			} else {
				throw new VideoPlayerExeption("Invalid Media File selection. File is either no file or not readable");
			}
		} else {
			throw new VideoPlayerExeption("No media File selected!");
		}

	}

	private void disableNewFileButton(Container container) {
		int len = container.getComponentCount();
		for (int idx = 0; idx < len; idx++) {
			Component tmpComponent = container.getComponent(idx);
			if (tmpComponent instanceof JButton) {
				JButton button = (JButton) tmpComponent;
				Icon icon = button.getIcon();
				if (icon != null && icon == UIManager.getIcon("FileChooser.newFolderIcon"))
					button.setEnabled(false);
			} else if (tmpComponent instanceof Container) {
				disableNewFileButton((Container) tmpComponent);
			}
		}
	}

	public static void updateMediaData(File selectedFile) throws SQLException {
		Settings settings = new Settings ();
		settings.setMedia(selectedFile.getAbsolutePath());
		System.out.println("FileCooser - do not set media duration");
	}

}
