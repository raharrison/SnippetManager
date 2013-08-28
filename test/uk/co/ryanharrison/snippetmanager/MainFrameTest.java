/**
 * MainFrameTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import javax.swing.JFrame;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.MainFrame;

/**
 * Test class for MainFrame
 * 
 * @author Ryan Harrison
 */
public class MainFrameTest {

  /**
   * Test that a new object of MainFrame can be made successfully.
   */
  @Test
  public void testCreation() {
    JFrame frame = new JFrame();
    new MainFrame(frame);
  }

  /**
   * Test that an exception is thrown when a null parent is passed into the constructor.
   */
  @Test(expected = NullPointerException.class)
  public void testIllegalCreation() {
    new MainFrame(null);
  }

}
