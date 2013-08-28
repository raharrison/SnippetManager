/**
 * FilterComparator.java
 */

package uk.co.ryanharrison.snippetmanager;

/**
 * A comparator that compares an object with a string filter text. If the data can be in some way matched from the filter text,
 * return true
 * 
 * @author Ryan Harrison
 */
public interface FilterComparator<T> {

  /**
   * Compare the data with a search string. If the data could be matched in some way from the search string, return true
   * 
   * @param data
   *          The data to compare
   * @param filter
   *          The filter to compare
   * @return True if the data could be matched from the filter string, otherwise false
   */
  public boolean isMatch(T data, String filter);
}
