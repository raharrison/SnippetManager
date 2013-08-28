/**
 * HintTextFieldTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.HintTextField;

/**
 * Test class for HintTextField
 * 
 * @author Ryan Harrison
 */
public class HintTextFieldTest {

  /**
   * Test that a new object of HintTextField can be made successfully.
   */
  @Test
  public void testCreation() {
    // Initially the text should be empty (it will actually show the hint)
    HintTextField hintField = new HintTextField("sample hint");
    assertEquals("Wrong textfield text", "", hintField.getText());

    // When the text is set the hint hides and the text is returned
    hintField.setText("sample text");
    assertEquals("Wrong textfield text", "sample text", hintField.getText());
  }

  /**
   * Test that an exception is thrown if a null hint is passed into the constructor.
   */
  @Test(expected = NullPointerException.class)
  public void testIllegalCreation() {
    HintTextField hintField = new HintTextField(null);
    assertEquals("Wrong textfield text", "", hintField.getText());
  }

}
