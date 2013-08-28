/**
 * GoToDialog.java
 */

package uk.co.ryanharrison.snippetmanager;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * A custom dialog that lets users enter a line to which they want to go to in the text view
 * 
 * @author Ryan Harrison
 */
public class GoToDialog extends JDialog {

  /** Serialisation identifier */
  private static final long serialVersionUID = -8909818173861825961L;

  /** The number of lines in the target text */
  private int               maxLines;

  /** The line number that has been entered by the user */
  private int               lineNumber;

  /** Field to enter the number */
  private JTextField        lineNumberField;

  /** Label to prompt the user */
  private JLabel            lineNumberLabel;

  /** Button to accept the number and go to that line */
  private JButton           okButton;

  /** Button to cancel the operation */
  private JButton           cancelButton;

  /** The result of this dialog depending on which button was pressed */
  private DialogResult      dialogResult;

  /**
   * Create a new go to dialog
   * 
   * @param parent
   *          The parent of the dialog
   * @param maxLines
   *          The number of lines in the target text
   * @throws IllegalArgumentException
   *           If the maxLines is less than zero
   */
  public GoToDialog(JFrame parent, int maxLines) throws IllegalArgumentException {
    super(parent, "Go To Line", true);

    if (maxLines < 0) {
      throw new IllegalArgumentException("Max number of lines cannot be negative");
    }

    this.maxLines = maxLines;
    this.createGui();
  }

  /**
   * Create the user interface
   */
  private void createGui() {
    // Set frame properties
    this.setLayout(new GridLayout(0, 2, 15, 15));
    this.setSize(300, 100);
    this.setResizable(false);
    this.setLocationRelativeTo(null);

    // Create the controls
    this.lineNumberField = new HintTextField("Enter a line number");

    this.lineNumberLabel = new JLabel("Enter a line number:", SwingConstants.CENTER);

    this.okButton = new JButton("OK");
    this.okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // If the input is not valid prompt the user
        if (!GoToDialog.this.validateLineNumber()) {
          JOptionPane.showMessageDialog(GoToDialog.this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Otherwise set the result of this dialog and exit
        else {
          GoToDialog.this.dialogResult = DialogResult.OK_OPTION;
          GoToDialog.this.dispose();
        }
      }
    });

    this.cancelButton = new JButton("Cancel");
    this.cancelButton.addActionListener(new ActionListener() {

      // If the cancel button is pressed set the dialog result and exit
      @Override
      public void actionPerformed(ActionEvent e) {
        GoToDialog.this.dialogResult = DialogResult.CANCEL_OPTION;
        GoToDialog.this.dispose();
      }
    });

    this.add(this.lineNumberLabel);
    this.add(this.lineNumberField);
    this.add(this.okButton);
    this.add(this.cancelButton);

    // Set a default value for the dialog result
    this.dialogResult = DialogResult.CANCEL_OPTION;
    this.lineNumber = 0;
  }

  /**
   * Get the result of this dialog being shown
   * 
   * @return The result of this dialog being shown
   */
  public DialogResult getDialogResult() {
    return this.dialogResult;
  }

  /**
   * Get the line number that the user has entered
   * 
   * @return The line number the user entered
   */
  public int getLineNumber() {
    return this.lineNumber;
  }

  /**
   * Validate that the user input is numerical and is in the bounds of the target text
   * 
   * @return True if the input is valid, false otherwise
   */
  private boolean validateLineNumber() {
    String in = this.lineNumberField.getText();
    // False if the input is empty
    if (in.isEmpty()) {
      return false;
    }
    try {
      int line = Integer.parseInt(in);
      // If the input is negative set it to zero by default
      line = Math.max(line, 0);
      // If the input is greater than the max lines, set it to the max lines
      line = Math.min(line, this.maxLines);
      this.lineNumber = line;
      return true;
    }
    // The input is not numerical
    catch (NumberFormatException e) {
      return false;
    }
  }
}
