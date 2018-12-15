package de.dicke.education.calculation.trainer.datahandling;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.Test;

public class PreferenceTest {
  private Preferences prefs;

  public void setPreference() {
    // This will define a node in which the preferences can be stored
//    prefs = Preferences.userRoot().node(this.getClass().getName());
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

    // Delete the preference settings for the first value
    prefs.remove(ID1);
    
  }
  
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

  @Test
  public void preferenceTest () {
	  setPreference();
	  exportXml();
  }
}