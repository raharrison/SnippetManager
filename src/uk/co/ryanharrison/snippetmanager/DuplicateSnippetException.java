/**
 * DuplicateSnippetException.java
 */

package uk.co.ryanharrison.snippetmanager;

/**
 * A custom exception for when a snippet is being added that already exists in the current set
 * 
 * @author Ryan Harrison
 */
public class DuplicateSnippetException extends Exception {

  /** Serialisation identifier */
  private static final long serialVersionUID = 8670973024870852924L;

  /**
   * Create a new duplicate snippet exception
   * 
   * @param message
   *          The message for this exception
   */
  public DuplicateSnippetException(String message) {
    super(message);
  }
}
