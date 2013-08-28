/**
 * SnippetFilterComparatorTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.Language;
import uk.co.ryanharrison.snippetmanager.Snippet;
import uk.co.ryanharrison.snippetmanager.SnippetFilterComparator;

/**
 * Test class for SnippetFilterComparator
 * 
 * @author Ryan Harrison
 */
public class SnippetFilterComparatorTest {

  /**
   * Test that a new object of SnippetFilterComparator can be made successfully and that the match method works correctly.
   */
  @Test
  public void testCreation() {
    String name = "name";
    String description = "description";
    String data = "sample data";
    Language lang = Language.Java;
    Set<String> keywords = new HashSet<String>();
    keywords.add("sample");

    // Make a sample snippet to match
    Snippet snippet = new Snippet(name, data, description, keywords, lang);

    SnippetFilterComparator comparator = new SnippetFilterComparator();

    // Make sure the comparator matches based upon each piece of data in the snippet
    boolean result = comparator.isMatch(snippet, "descrip");
    assertTrue("Comparator should match", result);

    result = comparator.isMatch(snippet, "java");
    assertTrue("Comparator should match", result);

    result = comparator.isMatch(snippet, "sample");
    assertTrue("Comparator should match", result);
  }

}
