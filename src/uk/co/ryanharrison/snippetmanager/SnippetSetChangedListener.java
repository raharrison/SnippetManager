/**
 * SnippetSetChangedListener.java
 */

package uk.co.ryanharrison.snippetmanager;

/**
 * A set of constants representing the type of change that has happened to the snippet set
 * 
 * @author Ryan Harrison
 */
enum ChangeType {
  Add, Delete, Update, Save, Load;
}

/**
 * An interface for classes that can listen to a snippet set and are notified when the snippet set has been changed in some way
 * Implementations can then take action depending on the type of change that has occured to the snippet set that is being listened
 * to
 * 
 * @author Ryan Harrison (rh00148)
 */
public interface SnippetSetChangedListener {

  /**
   * Called when the observable has notified its observers that the snippet set has changed
   * 
   * @param subject
   *          The observable whose snippet set has been changed
   * @param type
   *          The type of the change
   */
  public void onSnippetSetChanged(SnippetSetObservable subject, ChangeType type);
}
