/**
 * AboutDialog.java
 */

package uk.co.ryanharrison.snippetmanager;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Dialog box that displays information about the program to the user
 * 
 * @author Ryan Harrison
 */
public class AboutDialog extends JDialog {

  /** The label to hold the about text */
  private JLabel              aboutText;

  /** A button to close the dialog */
  private JButton             okButton;

  /** Serialisation identifier */
  private static final long   serialVersionUID = -1107019736187366759L;

  /** The about text */
  private static final String ABOUTTEXT        = "<html>Snippet Manager<br/>COM1028<br/>Ryan Harrison (rh00148)<br/>2013<br/><br/>A centralised repository of all of your small code snippets and memos.<br/>Create, edit, and organise a selection of code, or plain text, snippets with syntax highlighting.<br/>Assign keywords and descriptions to each snippet and use the search field to filter your current set of snippets.<br/>Save your snippet collection to an xml to and load in the file later on for further organising.</html>";

  /**
   * Create a new about dialog
   * 
   * @param parent
   *          The parent of this dialog
   */
  public AboutDialog(JFrame parent) {
    super(parent, "About Snippet Manager", true);
    this.createGUI();
  }

  /**
   * Create the gui elements and create the dialog
   */
  private void createGUI() {
    // Set dialog frame properties
    this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    this.setSize(600, 200);
    this.setResizable(false);
    this.setLocationRelativeTo(null);

    // Create the label
    this.aboutText = new JLabel(ABOUTTEXT);
    this.okButton = new JButton("OK");
    this.okButton.addActionListener(new ActionListener() {

      // When the button is pressed, close the dialog
      @Override
      public void actionPerformed(ActionEvent e) {
        AboutDialog.this.dispose();
      }
    });

    // Add the controls to the dialog
    this.add(this.aboutText);
    this.add(this.okButton);
  }
}
