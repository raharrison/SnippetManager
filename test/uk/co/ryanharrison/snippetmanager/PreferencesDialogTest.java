/**
 * PreferencesDialogTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import javax.swing.JFrame;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.PreferencesDialog;

/**
 * Test class for PreferencesDialog
 * 
 * @author Ryan Harrison
 */
public class PreferencesDialogTest {

  /**
   * Test that a new object of PreferencesDialog can be made successfully.
   */
  @Test
  public void testCreation() {
    JFrame frame = new JFrame();
    new PreferencesDialog(frame);
  }

}
