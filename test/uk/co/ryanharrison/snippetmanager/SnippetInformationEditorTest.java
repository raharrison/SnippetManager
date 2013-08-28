/**
 * SnippetInformationEditorTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.Language;
import uk.co.ryanharrison.snippetmanager.Snippet;
import uk.co.ryanharrison.snippetmanager.SnippetInformationEditor;

/**
 * Test class for SnippetInformationEditor
 * 
 * @author Ryan Harrison
 */
public class SnippetInformationEditorTest {

  /**
   * Test that a new object of SnippetInformationEditor can be made successfully. Check that the data from the new snippet from the
   * editor matches that of the old one when nothing is edited.
   */
  @Test
  public void testCreation() {
    String name = "name";
    String description = "description";
    String data = "sample data";
    Language lang = Language.Java;
    Set<String> keywords = new HashSet<String>();
    keywords.add("sample");

    // Create a default snippet
    Snippet snippet = new Snippet(name, data, description, keywords, lang);
    JFrame frame = new JFrame();

    SnippetInformationEditor editor = new SnippetInformationEditor(frame, snippet, true);

    // Check that the data from the new snippet from the editor matches that of the old one when nothing is edited
    Snippet newSnippet = editor.getNewSnippet();

    assertEquals("Names do not match", snippet.getName(), newSnippet.getName());
    assertEquals("Descriptions do not match", snippet.getDescription(), newSnippet.getDescription());
    assertEquals("Data do not match", snippet.getSnippet(), newSnippet.getSnippet());
    assertEquals("Languages do not match", snippet.getLanguage(), newSnippet.getLanguage());
    assertEquals("Keywords do not match", snippet.getKeywords(), newSnippet.getKeywords());
  }

}
