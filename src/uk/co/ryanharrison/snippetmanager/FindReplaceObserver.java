/**
 * FindReplaceObserver.java
 */

package uk.co.ryanharrison.snippetmanager;

/**
 * An interface for classes that can observe FindReplaceObservables for when find replace events occur. When they do the
 * onFindReplaceAction method is executed
 * 
 * @author Ryan Harrison
 */
public interface FindReplaceObserver {

  /**
   * Executed when find replace events occur from the observable that this observer is listening to
   * 
   * @param observable
   *          The observable that triggered the event
   */
  public void onFindReplaceAction(FindReplaceObservable observable);
}
