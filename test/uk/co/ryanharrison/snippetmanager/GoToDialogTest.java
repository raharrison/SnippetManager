/**
 * GoToDialogTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import javax.swing.JFrame;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.GoToDialog;

/**
 * Test class for GoToDialog
 * 
 * @author Ryan Harrison
 */
public class GoToDialogTest {

  /**
   * Test that a new object of GoToDialog can be made successfully.
   */
  @Test
  public void testCreation() {
    JFrame frame = new JFrame();
    GoToDialog dialog = new GoToDialog(frame, 11);
    assertEquals("Wrong default line number", 0, dialog.getLineNumber());
  }

  /**
   * Test that an exception thrown if a negative maximum line number is passed into the constructor.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIllegalCreation() {
    JFrame frame = new JFrame();
    GoToDialog dialog = new GoToDialog(frame, -4);
    assertEquals("Wrong default line number", -4, dialog.getLineNumber());
  }
}
