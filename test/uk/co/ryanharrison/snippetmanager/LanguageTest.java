/**
 * LanguageTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.Language;

/**
 * Test class for the Language enum
 * 
 * @author Ryan Harrison
 */
public class LanguageTest {

  /**
   * Test that the enum values is created successfully.
   */
  @Test
  public void testCreation() {
    Language java = Language.Java;
    assertEquals("Wrong value", "Java", java.getValue());

    Language php = Language.PHP;
    assertEquals("Wrong value", "PHP", php.getValue());
  }

  /**
   * Test that the regular expression patterns from each Language method match.
   */
  @Test
  public void testRegexPatterns() {
    Language java = Language.Java;

    String regex = java.getPattern();
    String patternRegex = java.getPattern();

    assertEquals("Regular expression patterns do not match", regex, patternRegex);
  }

}
