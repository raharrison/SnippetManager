/**
 * Preferences.java
 */

package uk.co.ryanharrison.snippetmanager;

import java.awt.Color;

/**
 * A singleton class that holds preferences for this application
 * 
 * @author Ryan Harrison
 */
public class Preferences {

  /** The colour to use when highlighting the keywords of a language */
  private Color              highlightColour;

  /** Whether or not the highlight numbers. NOTE: This is not currently implemented */
  private boolean            highlightNumbers;

  /** As this class is a singleton, maintain a single instance of it */
  private static Preferences instance;

  /**
   * Private constructor as singleton. Initialise fields
   */
  private Preferences() {
    super();
    this.highlightColour = Color.BLUE;
    this.highlightNumbers = true;
    instance = null;
  }

  /**
   * Gets the colour to highlight the text in
   * 
   * @return The colour to highlight in
   */
  public Color getHighlightColour() {
    return this.highlightColour;
  }

  /**
   * Set the colour to highlight text in
   * 
   * @param color
   *          The colour to highlight using
   */
  public void setHighlightColour(Color color) {
    this.highlightColour = color;
  }

  /**
   * Set if numbers will be highlighted
   * 
   * @param highlightNumbers
   *          If numbers will be highlighted
   */
  public void setWillHighlightNumbers(boolean highlightNumbers) {
    this.highlightNumbers = highlightNumbers;
  }

  /**
   * Get if numbers will be highlighted
   * 
   * @return Whether or not numbers should be highlighted
   */
  public boolean willHighlightNumbers() {
    return this.highlightNumbers;
  }

  /**
   * Get the singleton instance of this class
   * 
   * @return The Preferences instance
   */
  public static synchronized Preferences getInstance() {
    // If the instance has not been created yet, create the one new object
    if (instance == null) {
      instance = new Preferences();
    }

    // Return the singleton instance
    return instance;
  }
}
