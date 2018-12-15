package de.dicke.education.calculation.trainer.datahandling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;

public class UserSettingsPersistor {

	private static final String ID_DUMMY = "dummy";
	private Preferences prefs;
	private String ID_MEDIA_DIR = "mediaDir";
	private String ID_MEDIA_FILE = "mediaFile";
	private String ID_CURRENT_CORRCET_ANSWERS = "currentCorrectAnswers";
	private String ID_CURRENT_PENALTIES = "currentPenalties";
	private String ID_CURRENT_PLAYER_POSITION = "currentPlayerPosition";
	static int cnt = 0;

	public String exportXml() {
		String preferencesAsXmlString = "";
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			prefs.exportNode(outStream);
			prefs.exportSubtree(outStream);
			preferencesAsXmlString = new String(outStream.toByteArray(), "UTF-8");
			System.out.println(preferencesAsXmlString);
		} catch (IOException | BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return preferencesAsXmlString;
	}

	public void importXml(String preferencesAsXmlString)
	{
		try {
			InputStream inStream = IOUtils.toInputStream(preferencesAsXmlString, "UTF-8");
			
			prefs.importPreferences(inStream);
			inStream.close();
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		} catch (InvalidPreferencesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void setDummy(String string) throws BackingStoreException {
		System.out.println("cnt = " + cnt + "\n");
		prefs.node("holger").put(ID_DUMMY, string);
		System.out.println("cnt = " + cnt + "\n");
		prefs.flush();
		prefs.sync();
	}

	public String getDummy() {
		try {
			return prefs.get(ID_DUMMY, "");
		} catch (NullPointerException e) {
			return "";
		}
	}
	
	
	
	public void setMediaDirectory(String directory) {
		prefs.put(ID_MEDIA_DIR, directory);
	}

	public String getMediaDirectory() {
		try {
			return prefs.get(ID_MEDIA_DIR, "");
		} catch (NullPointerException e) {
			return "";
		}
	}

	public void setMediaFile(String directory) {
		prefs.put(ID_MEDIA_FILE, directory);
	}

	public String getMediaFile() {
		try {
			return prefs.get(ID_MEDIA_FILE, "");
		} catch (NullPointerException e) {
			System.err.println("Can not get MediaFile from Preferences");
			return "";
		}
	}

	public void setCurrentCorrectAnswers(int correctAnswers) {
		prefs.putInt(ID_CURRENT_CORRCET_ANSWERS, correctAnswers);
	}

	public int getCurrentCorrectAnswers() {
		try {
			return prefs.getInt(ID_CURRENT_CORRCET_ANSWERS, 0);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void setCurrentPenalties(int penalties) {
		prefs.putInt(ID_CURRENT_PENALTIES, penalties);
	}

	public int getCurrentPenalties() {
		try {
			return prefs.getInt(ID_CURRENT_PENALTIES, 0);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void setCurrentPlayerPosition(long currentPlayerPosition) {
		prefs.putLong(ID_CURRENT_PLAYER_POSITION, currentPlayerPosition);
		try {
			prefs.flush();
			prefs.sync();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getCurrentPlayerPostition() {
		try {
			return prefs.getLong(ID_CURRENT_PLAYER_POSITION, 1L);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public UserSettingsPersistor() {
		cnt  ++;
		System.out.println("cnt = " + cnt + "\n");
//		prefs = Preferences.userRoot().node(this.getClass().getName());
		prefs = Preferences.userNodeForPackage(this.getClass());
		 String ID1 = "Test1";
		    String ID2 = "Test2";
		    String ID3 = "Test3";

		    // First we will get the values
		    // Define a boolean value
		    System.out.println(prefs.getBoolean(ID1, true));
		    // Define a string with default "Hello World
		    System.out.println(prefs.get(ID2, "Hello World"));
		    // Define a integer with default 50
		    System.out.println(prefs.getInt(ID3, 50));

		    // now set the values
		    prefs.putBoolean(ID1, false);
		    prefs.put(ID2, "Hello Europa");
		    prefs.putInt(ID3, 45);

	}

}
