/**
 * SnippetFilterComparator.java
 */

package uk.co.ryanharrison.snippetmanager;

/**
 * A custom filter comparator that compares a snippet and a search filter string
 * 
 * @author Ryan Harrison
 */
public class SnippetFilterComparator implements FilterComparator<Snippet> {

  /**
   * Match the snippet to a filter search string. If the snippet in some way matches the filter string, return true
   * 
   * @param snippet
   *          The snippet to compare
   * @param search
   *          The search filter string to use when comparing
   * @return True if a value in the snippet contains the search string, false otherwise
   */
  @Override
  public boolean isMatch(Snippet snippet, String search) {
    // Check if any element in the snippet contains the search string
    boolean found = snippet.getName().toLowerCase().trim().contains(search);
    found |= snippet.getDescription().toLowerCase().trim().contains(search);
    found |= snippet.toString().toLowerCase().contains(search);
    found |= snippet.getLanguage().toString().toLowerCase().contains(search);
    found |= snippet.getKeywords().contains(search);

    return found;
  }
}
