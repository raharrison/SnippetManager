/**
 * DuplicateSnippetExceptionTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.DuplicateSnippetException;

/**
 * Test class for DuplicateSnippetException
 * 
 * @author Ryan Harrison
 */
public class DuplicateSnippetExceptionTest {

  /**
   * Test that a new object of DuplicateSnippetException can be made successfully and the getters work correctly.
   */
  @Test
  public void testCreation() {
    DuplicateSnippetException exception = new DuplicateSnippetException("duplicate snippet");
    assertEquals("Messages are not equal", "duplicate snippet", exception.getMessage());
  }

}
