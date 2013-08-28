/**
 * FindReplaceObservable.java
 */

package uk.co.ryanharrison.snippetmanager;

/**
 * An interface for classes that can be observed by observers for find and replace events. Implementations include a list of
 * observers that get notified when any find replace events happen
 * 
 * @author Ryan Harrison
 */
public interface FindReplaceObservable {

  /**
   * Add an observer to the list of observers that this observable will notify
   * 
   * @param o
   *          The observer to add
   */
  public void addFindReplaceObserver(FindReplaceObserver o);

  /**
   * Notify all observers that a find or replace event has happened. Therefore appropriate action can be taken by the observer
   */
  public void notifyObservers();

  /**
   * Remove an observer from the list of observers that this observable will notify
   * 
   * @param o
   *          The observer to remove
   */
  public void removeFindReplaceObserver(FindReplaceObserver o);
}
