/**
 * DialogResult.java
 */

package uk.co.ryanharrison.snippetmanager;

/**
 * A set of constants representing the result of a dialog being shown to the user. The caller method can then decide what action to
 * take depending on the result from the user.
 * 
 * @author Ryan Harrison
 */
public enum DialogResult {
  // Whether or not the user clicked the ok button or the cancel button
  OK_OPTION, CANCEL_OPTION;
}
