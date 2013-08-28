/**
 * FindReplaceTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.FindReplace;

/**
 * Test class for FindReplace
 * 
 * @author Ryan Harrison
 */
public class FindReplaceTest {

  /**
   * Test that a new object of FindReplace can be made successfully.
   */
  @Test
  public void testCreation() {
    FindReplace find = new FindReplace("some sample text");

    // Make sure the default values are correct
    assertEquals("Wrong text", "some sample text", find.getText());
    assertEquals("Wrong default start index", 0, find.getStart());
    assertEquals("Wrong default end index", 0, find.getEnd());
  }

  /**
   * Test that the object finds text correctly.
   */
  @Test
  public void testFind() {
    FindReplace find = new FindReplace("some sample text");
    find.findNext("sample");

    // Make sure the start and end indexes match up with the word
    assertEquals("Wrong start index", 5, find.getStart());
    assertEquals("Wrong end index", 11, find.getEnd());
  }

  /**
   * Test that regular expressions can be used to find text.
   */
  @Test
  public void testFindRegex() {
    FindReplace find = new FindReplace("some sample text");
    find.findNextRegex("s\\w+e");

    // Make sure the start and end indexes match up with the word
    assertEquals("Wrong start index", 0, find.getStart());
    assertEquals("Wrong end index", 4, find.getEnd());
  }

  /**
   * Test that all instances of a string can be replaced by another string in the text.
   */
  @Test
  public void testReplace() {
    FindReplace find = new FindReplace("some sample text");
    find.replaceAll("sample", "test");

    // Make sure that the word has been replaced
    assertEquals("Wrong replaced text", "some test text", find.getText());
  }

}
