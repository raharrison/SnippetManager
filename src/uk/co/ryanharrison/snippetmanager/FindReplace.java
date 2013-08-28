package uk.co.ryanharrison.snippetmanager;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Frame representing a find/replace dialog box that can find words in a text pane or replace them with another word.
 * 
 * @author Ryan Harrison
 */
public class FindReplace extends JFrame implements ActionListener, FindReplaceObservable {

  private static final long         serialVersionUID = 5068260384806127496L;

  /** List of observers that listen to find/replace events */
  private List<FindReplaceObserver> observers;

  /** The text to find */
  private JTextField                findField;

  /** The text to replace with */
  private JTextField                replaceField;

  /** The button to find the text */
  private JButton                   findButton;

  /** The button to close the dialog */
  private JButton                   closeButton;

  /** The button to replace all occurrences of the text */
  private JButton                   replaceAllButton;

  /** The label to display find */
  private JLabel                    findLabel;

  /** The label to display replace */
  private JLabel                    replaceLabel;

  /** Whether or not to consider the text as a regular expression when searching */
  private JCheckBox                 regularExpressions;

  /** The text to search in */
  private String                    text;

  /** The current index to start searching for text from */
  private int                       index;

  /** The start index of a match when searching for text */
  private int                       start;

  /** The end index of a match when searching for text */
  private int                       end;

  /**
   * Create a new Find/Replace dialog.
   * 
   * @param text
   *          The text to search through
   */
  public FindReplace(String text) {
    super("Find/Replace");
    this.text = text;
    this.index = 0;
    this.start = 0;
    this.end = 0;
    this.observers = new ArrayList<FindReplaceObserver>();

    // Set frame properties
    this.setLayout(new FlowLayout(FlowLayout.CENTER));
    this.setSize(550, 150);
    this.setResizable(false);
    this.setLocationRelativeTo(null);

    // Create the controls
    this.findField = new JTextField(20);
    this.findField.requestFocus();
    this.replaceField = new JTextField(20);

    this.findLabel = new JLabel("Find:");
    this.replaceLabel = new JLabel("Replace with:");

    this.findButton = new JButton("Find");
    this.findButton.addActionListener(this);
    this.closeButton = new JButton("Close");
    this.closeButton.addActionListener(this);
    this.replaceAllButton = new JButton("Replace All");
    this.replaceAllButton.addActionListener(this);

    this.regularExpressions = new JCheckBox("Use regular expressions");

    // Create the left hand panel and add components
    GridLayout layout = new GridLayout(2, 2, 10, 10);
    JPanel left = new JPanel(layout);
    left.add(this.findLabel);
    left.add(this.findField);
    left.add(this.replaceLabel);
    left.add(this.replaceField);

    // Create the right hand panel and add components
    JPanel right = new JPanel();
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
    right.add(this.findButton);
    right.add(Box.createRigidArea(new Dimension(0, 5)));
    right.add(this.replaceAllButton);
    right.add(Box.createRigidArea(new Dimension(0, 5)));
    right.add(this.closeButton);
    right.add(this.regularExpressions);

    // Add both panels to the frame
    this.add(left);
    this.add(right);
  }

  /**
   * Action event for the click of the buttons
   * 
   * @param e
   *          Event information
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    JButton button = (JButton) e.getSource();
    // If the user clicked the close button, close the dialog
    if (button == this.closeButton) {
      this.dispose();
    }
    // If the user clicked the replace all button, replace all occurrences of a piece of text with another
    else if (button == this.replaceAllButton) {
      this.replaceAll(this.findField.getText(), this.replaceField.getText());
    }
    // If the user clicked the find button
    else if (button == this.findButton) {
      // Get the text to search for, if empty show an error message
      String find = this.findField.getText();
      if (find.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No find text entered", "Error", JOptionPane.ERROR_MESSAGE);
      }
      else {
        // If the current index to search from is at the end, prompt the user to restart
        if (this.index == this.text.length()) {
          int restart = JOptionPane.showConfirmDialog(this, "Reached the end of the document. Begin searching at the beginning?",
              "Find", JOptionPane.YES_NO_OPTION);
          // If the say yes the reset the index
          if (restart == JOptionPane.YES_OPTION) {
            this.index = 0;
          }
        }
        // Find the next match and notify the observers
        this.findNext(find);
      }
    }
  }

  /**
   * Add a new observer to this dialog. Observers will be notified when text has been found or replaced. Observers can then take the
   * appropriate action using the start and end fields
   * 
   * @param o
   * 
   * @see uk.co.ryanharrison.snippetmanager.FindReplaceObservable#addFindReplaceObserver(uk.co.ryanharrison.snippetmanager.FindReplaceObserver)
   */
  @Override
  public void addFindReplaceObserver(FindReplaceObserver o) {
    this.observers.add(o);
  }

  /**
   * Find the next occurrence of 'find' in the text String and notify the observers that a new match has been found Observers can
   * then take the appropriate action using the start and end fields
   * 
   * @param find
   *          The string to find
   */
  public void findNext(String find) {
    // If the user wants to use regular expressions
    if (this.regularExpressions.isSelected()) {
      this.findNextRegex(find);
    }
    // Otherwise use more basic searching methods that set the start and end fields
    else {
      this.start = this.text.indexOf(find, this.index);
      this.end = this.start + find.length();
    }

    // If a match was found, update the starting index for the next search and notify the observers that a match has been found
    if (this.start != -1) {
      this.index = this.end;
      this.notifyObservers();
    }
    else {
      JOptionPane.showMessageDialog(this, "No matches found", "Find", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Find the next occurence of a regex string in the find tex
   * 
   * @param regex
   *          The regular expression to match
   */
  public void findNextRegex(String regex) {
    Pattern p = null;
    try {
      p = Pattern.compile(regex);
    }
    catch (PatternSyntaxException e) {
      JOptionPane.showMessageDialog(this, "Invalid regular expression", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    // Create a matcher from the input text
    Matcher m = p.matcher(this.text);
    // If a match has been found from the current starting index
    if (m.find(this.index)) {
      // Set the start and end points of the match
      this.start = m.start();
      this.end = m.end();
    }
    else {
      this.start = -1;
    }
  }

  /**
   * Get the end index of a match in the text. Can be used to highlight a match
   * 
   * @return The end index of a match in the target text
   */
  public int getEnd() {
    return this.end;
  }

  /**
   * Get the start index of a match in the text. Can be used to highlight a match
   * 
   * @return The start index of a match in the target text
   */
  public int getStart() {
    return this.start;
  }

  /**
   * Get the text that is being searched through. This text is also modified when the replace button is clicked. Observers can use
   * this field to update their version of the text
   * 
   * @return
   */
  public String getText() {
    return this.text;
  }

  /**
   * Notify all the observers of this dialog that a piece of text has been searched for and found or that the text has been modified
   * through replacement
   * 
   * 
   * @see uk.co.ryanharrison.snippetmanager.FindReplaceObservable#notifyObservers()
   */
  @Override
  public void notifyObservers() {
    for (FindReplaceObserver observer : this.observers) {
      observer.onFindReplaceAction(this);
    }
  }

  /**
   * Remove an observer from the current set of observers of this dialog. The observer will no longer be notified when find events
   * happen
   * 
   * @param o
   *          The observer to remove
   * 
   * @see uk.co.ryanharrison.snippetmanager.FindReplaceObservable#removeFindReplaceObserver(uk.co.ryanharrison.snippetmanager.FindReplaceObserver)
   */
  @Override
  public void removeFindReplaceObserver(FindReplaceObserver o) {
    this.observers.remove(o);
  }

  /**
   * Replace every occurrence of a piece of text with another piece as entered by the user. Also take into account whether or not
   * regular expressions should be used
   * 
   * @param find
   *          The string to find
   * @param replace
   *          The string to replace all instances of find to
   */
  public void replaceAll(String find, String replace) {
    // If the user is using regular expressions
    if (this.regularExpressions.isSelected()) {
      this.text = this.text.replaceAll(find, replace);
    }
    else {
      this.text = this.text.replace(find, replace);
    }
    // Notify the observers that the text has been replaced
    this.notifyObservers();
  }
}