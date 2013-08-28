/**
 * PreferencesTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.Preferences;

/**
 * Test class for Preferences
 * 
 * @author Ryan Harrison
 */
public class PreferencesTest {

  /**
   * Test that the singleton Preferences works correctly and that all changes are kept inside the one singleton instance.
   */
  @Test
  public void testCreation() {
    // The class is a singleton so call the static getInstance method to create the one object
    Preferences prefs = Preferences.getInstance();
    assertNotNull(prefs);

    // Test the default values
    assertEquals("Wrong highlight colour", Color.BLUE, prefs.getHighlightColour());
    assertEquals("Wrong highlight numbers value", true, prefs.willHighlightNumbers());

    prefs.setHighlightColour(Color.RED);
    prefs.setWillHighlightNumbers(false);

    // Makes sure the changes are kept in the static instance
    Preferences prefs2 = Preferences.getInstance();

    assertEquals("Wrong highlight colour", Color.RED, prefs2.getHighlightColour());
    assertEquals("Wrong highlight numbers value", false, prefs2.willHighlightNumbers());
  }

}
