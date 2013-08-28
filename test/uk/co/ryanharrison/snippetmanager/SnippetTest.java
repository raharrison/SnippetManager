/**
 * SnippetTes.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.Language;
import uk.co.ryanharrison.snippetmanager.Snippet;

/**
 * Test class for Snippet
 * 
 * @author Ryan Harrison
 */
public class SnippetTest {

  /**
   * Test that a new object of Snippet can be made successfully and that all getters work correctly.
   */
  @Test
  public void testCreation() {
    String name = "name";
    String description = "description";
    String data = "sample data";
    Language lang = Language.Java;
    Set<String> keywords = new HashSet<String>();
    keywords.add("sample");

    // Create a sample snippet
    Snippet snippet = new Snippet(name, data, description, keywords, lang);

    // Test the getter methods
    assertEquals("Wrong name", name, snippet.getName());
    assertEquals("Wrong content", data, snippet.getSnippet());
    assertEquals("Wrong description", description, snippet.getDescription());
    assertEquals("Wrong language", lang, snippet.getLanguage());
    assertEquals("Wrong keywords", keywords, snippet.getKeywords());
    assertEquals("Wrong number ofkeywords", 1, snippet.getKeywords().size());
  }

  /**
   * Test that an exception is thrown if an empty name is passed into the constructor.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIllegalEmptyCreation() {
    String name = "";
    String description = "description";
    String data = "sample data";
    Language lang = Language.Java;
    Set<String> keywords = new HashSet<String>();

    new Snippet(name, data, description, keywords, lang);
  }

  /**
   * Test that an exception is thrown if a null name is passed into the constructor.
   */
  @Test(expected = NullPointerException.class)
  public void testIllegalNullCreation() {
    String name = null;
    String description = "description";
    String data = "sample data";
    Language lang = Language.Java;
    Set<String> keywords = new HashSet<String>();

    new Snippet(name, data, description, keywords, lang);
  }

}
