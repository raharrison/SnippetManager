/**
 * AboutDialogTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import javax.swing.JFrame;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.AboutDialog;

/**
 * Test class for AboutDialog
 * 
 * @author Ryan Harrison
 */
public class AboutDialogTest {

  /**
   * Test that a new object of AboutDialog can be made successfully.
   */
  @Test
  public void testCreation() {
    JFrame frame = new JFrame();
    new AboutDialog(frame);
  }

}
