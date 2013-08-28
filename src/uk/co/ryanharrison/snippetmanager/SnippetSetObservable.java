/**
 * SnippetObservable.java
 */

package uk.co.ryanharrison.snippetmanager;

/**
 * An interface for observable classes that have a snippet set that can be observed. Maintain a set of observers and notify them
 * when a change has happened to the snippet set
 * 
 * @author Ryan Harrison
 */
public interface SnippetSetObservable {

  /**
   * Add a new listener (observer) to the current set of listeners. This listener will then be notified when any changes occur to
   * the snippet set
   * 
   * @param o
   *          The listener (observer) to add
   */
  public void addSnippetSetChangedListener(SnippetSetChangedListener o);

  /**
   * Notify all listeners (observers) that some change has happened to the snippet set
   * 
   * @param type
   *          The change that has occured to the snippet set. This is then passed on to the observer who can then take appropriate
   *          action
   */
  public void notifyObservers(ChangeType type);

  /**
   * Remove a new listener (observer) from the current set of listeners. This listener will then not be notified when any changes
   * occur to the snippet set
   * 
   * @param o
   *          The listener (observer) to remove
   */
  public void removeSnippetSetChangedListener(SnippetSetChangedListener o);
}
