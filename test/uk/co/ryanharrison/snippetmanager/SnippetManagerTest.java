/**
 * SnippetManagerTest.java
 */

package uk.co.ryanharrison.snippetmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;

import javax.swing.tree.TreeNode;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.co.ryanharrison.snippetmanager.DuplicateSnippetException;
import uk.co.ryanharrison.snippetmanager.Language;
import uk.co.ryanharrison.snippetmanager.Snippet;
import uk.co.ryanharrison.snippetmanager.SnippetManager;

/**
 * Test class for SnippetManager
 * 
 * @author Ryan Harrison
 */
public class SnippetManagerTest {

  /** Some sample snippets for use in the tests. These are initialised in the setUpBeforeClass method. */
  private static Snippet sampleSnippet;
  private static Snippet sampleSnippet2;

  /**
   * Test that an exception is thrown if a snippet is added to the model that already exists in it.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet already exists (expected).
   */
  @Test(expected = DuplicateSnippetException.class)
  public void testAddDuplicateSnippet() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    // Add the same snippet twice
    manager.addSnippet(SnippetManagerTest.sampleSnippet);
    manager.addSnippet(SnippetManagerTest.sampleSnippet);
  }

  /**
   * Test that an exception is thrown if a snippet is added to the model that already exists in it.
   * 
   * @throws NullPointerException
   *           If the snippet already exists.
   */
  @Test(expected = NullPointerException.class)
  public void testAddNullSnippet() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(null);
  }

  /**
   * Test that a new snippet can be added to the manager.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet already exists.
   */
  @Test
  public void testAddSnippet() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    assertEquals("Wrong number of snippets", 0, manager.getSnippetCount());

    manager.addSnippet(SnippetManagerTest.sampleSnippet);
    assertEquals("Wrong number of snippets", 1, manager.getSnippetCount());

    manager.addSnippet(SnippetManagerTest.sampleSnippet2);
    assertEquals("Wrong number of snippets", 2, manager.getSnippetCount());
  }

  /**
   * Test that a new object of SnippetManager can be made successfully.
   */
  @Test
  public void testCreation() {
    SnippetManager manager = new SnippetManager();
    assertEquals("Wrong number of snippets", 0, manager.getSnippetCount());
  }

  /**
   * Test that an exception is thrown when a null snippet is deleted.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet that is added already exists.
   */
  @Test(expected = NullPointerException.class)
  public void testDeleteNullSnippet() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(SnippetManagerTest.sampleSnippet);
    manager.addSnippet(SnippetManagerTest.sampleSnippet2);

    // Try to delete a null snippet
    manager.deleteSnippet(null);
  }

  /**
   * Test that an existing snippet can be removed from the manager.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet that is added already exists.
   */
  @Test
  public void testDeleteSnippet() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(SnippetManagerTest.sampleSnippet);
    manager.addSnippet(SnippetManagerTest.sampleSnippet2);

    // Get the initial number of snippets
    assertEquals("Wrong number of snippets", 2, manager.getSnippetCount());

    // Remove snippets and make sure the number of snippets left is updated
    manager.deleteSnippet(SnippetManagerTest.sampleSnippet2);
    assertEquals("Wrong number of snippets", 1, manager.getSnippetCount());
    manager.deleteSnippet(SnippetManagerTest.sampleSnippet);
    assertEquals("Wrong number of snippets", 0, manager.getSnippetCount());
  }

  /**
   * Test that a snippet can be retrieved from the data model by passing in a snippet name.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet that is added already exists.
   */
  @Test
  public void testGetSnippetFromName() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(SnippetManagerTest.sampleSnippet);
    manager.addSnippet(SnippetManagerTest.sampleSnippet2);

    Snippet snippet = manager.getSnippetFromName(SnippetManagerTest.sampleSnippet.getName());

    // Make sure the snippet that is found is not null
    assertNotNull(snippet);

    // Make sure the data in the snippet is correct
    assertEquals("Names do not match", sampleSnippet.getName(), snippet.getName());
    assertEquals("Descriptions do not match", sampleSnippet.getDescription(), snippet.getDescription());
    assertEquals("Data do not match", sampleSnippet.getSnippet(), snippet.getSnippet());
    assertEquals("Languages do not match", sampleSnippet.getLanguage(), snippet.getLanguage());
    assertEquals("Keywords do not match", sampleSnippet.getKeywords(), snippet.getKeywords());
  }

  /**
   * Test that null is returned if a snippet is retrieved with a name that does not exist in the set.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet that is added already exists.
   */
  @Test
  public void testGetSnippetFromNameNotExists() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(SnippetManagerTest.sampleSnippet);
    manager.addSnippet(SnippetManagerTest.sampleSnippet2);

    Snippet snippet = manager.getSnippetFromName("not exists");
    // As a snippet with the name does not exist, the result should be null
    assertNull(snippet);
  }

  /**
   * Test that an exception is thrown if a null name string is passed into the getSnippetFromName method of the model.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet that is added already exists.
   */
  @Test(expected = NullPointerException.class)
  public void testGetSnippetFromNullName() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(SnippetManagerTest.sampleSnippet);
    manager.addSnippet(SnippetManagerTest.sampleSnippet2);

    Snippet snippet = manager.getSnippetFromName(null);
    // As a snippet with the name does not exist, the result should be null
    assertNull(snippet);
  }

  /**
   * Test that a root tree node containing all the snippets currently in the manager can be generated.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet already exists.
   */
  @Test
  public void testTreeGeneration() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(SnippetManagerTest.sampleSnippet);
    manager.addSnippet(SnippetManagerTest.sampleSnippet2);

    TreeNode root = manager.createTreeNodeFromSnippets();

    // As two snippets have been added, the number of child nodes must also be two
    assertEquals("Wrong number of children in the root node", 2, root.getChildCount());
  }

  /**
   * Test that an exception is thrown when a null snippet is updated.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet that is added already exists.
   */
  @Test(expected = NullPointerException.class)
  public void testUpdateNullSnippet() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(SnippetManagerTest.sampleSnippet);

    manager.updateSnippet(null, sampleSnippet2);
  }

  /**
   * Test that an existing snippet can be updated to become another snippet.
   * 
   * @throws DuplicateSnippetException
   *           If the snippet that is added already exists.
   */
  @Test
  public void testUpdateSnippet() throws DuplicateSnippetException {
    SnippetManager manager = new SnippetManager();
    manager.addSnippet(SnippetManagerTest.sampleSnippet);

    // Update the snippets
    manager.updateSnippet(sampleSnippet, sampleSnippet2);
    assertEquals("Wrong number of snippets", 1, manager.getSnippetCount());
    Snippet snippet = manager.getSnippetFromName(sampleSnippet2.getName());

    // Make sure the snippet is found
    assertNotNull(snippet);

    // Make sure the data in the snippet is correct
    assertEquals("Names do not match", sampleSnippet2.getName(), snippet.getName());
    assertEquals("Descriptions do not match", sampleSnippet2.getDescription(), snippet.getDescription());
    assertEquals("Data do not match", sampleSnippet2.getSnippet(), snippet.getSnippet());
    assertEquals("Languages do not match", sampleSnippet2.getLanguage(), snippet.getLanguage());
    assertEquals("Keywords do not match", sampleSnippet2.getKeywords(), snippet.getKeywords());
  }

  /**
   * Called before any tests are executed. Initialise the same snippets that are used in the tests.
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    sampleSnippet = new Snippet("name", "data", "description", new HashSet<String>(), Language.Java);
    sampleSnippet2 = new Snippet("name2", "data2", "description2", new HashSet<String>(), Language.PHP);
  }
}
