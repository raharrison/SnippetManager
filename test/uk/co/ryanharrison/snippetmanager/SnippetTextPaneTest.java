/**
 * SnippetTextPaneTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.SnippetTextPane;

/**
 * Test class for SnippetTextPane
 * 
 * @author Ryan Harrison
 */
public class SnippetTextPaneTest {

  /**
   * Test that a new object of SnippetTextPane can be made successfully and that the getter method work correctly.
   */
  @Test
  public void testCreation() {
    SnippetTextPane pane = new SnippetTextPane();

    // Check the default line numbers and text
    assertEquals("Wrong line number", 0, pane.getCurrentLine(pane.getCaretPosition()));
    assertEquals("Wrong text", "", pane.getText());
    assertEquals("Wrong number of lines", 1, pane.getNumberLines());

    // Use the append method to add more text and check that it was added
    pane.appendText("more text");
    assertEquals("Wrong text", "more text", pane.getText());
  }

}
