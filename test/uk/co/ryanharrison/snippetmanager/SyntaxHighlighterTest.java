/**
 * SyntaxHighlighterTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.Language;
import uk.co.ryanharrison.snippetmanager.SyntaxHighlighter;

/**
 * Test class for SyntaxHighlighter
 * 
 * @author Ryan Harrison
 */
public class SyntaxHighlighterTest {

  /**
   * Test that a new object of SyntaxHighlighter can be made successfully.
   */
  @Test
  public void testCreation() {
    Language lang = Language.Java;
    new SyntaxHighlighter(lang);
  }

}
