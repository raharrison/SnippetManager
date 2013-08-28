/**
 * PreferencesDialog.java
 */

package uk.co.ryanharrison.snippetmanager;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/**
 * A dialog that lets users set the preferences of the program
 * 
 * @author Ryan Harrison
 */
public class PreferencesDialog extends JDialog {

  /** Serialisation identifier */
  private static final long serialVersionUID = -4856089819497107981L;

  /** Blue colour option */
  private JRadioButton      blue;
  /** Red colour option */
  private JRadioButton      red;
  /** Orange colour option */
  private JRadioButton      orange;
  /** Green colour option */
  private JRadioButton      green;

  /** Prompt label */
  private JLabel            highlightColourLabel;

  /** Whether or not to highlight numbers */
  private JCheckBox         numbers;

  /** Button to accept the changes to the preferences */
  private JButton           okButton;

  /**
   * Create a new preferences dialog
   * 
   * @param parent
   *          The parent to the dialog
   */
  public PreferencesDialog(JFrame parent) {
    super(parent, "Preferences", true);
    this.createGUI();
  }

  /**
   * Create the dialog interface
   */
  private void createGUI() {
    this.setLayout(new GridLayout(0, 1, 10, 10));
    this.setSize(300, 250);
    this.setResizable(false);
    this.setLocationRelativeTo(null);

    // Create components
    this.blue = new JRadioButton("Blue");
    this.red = new JRadioButton("Red");
    this.orange = new JRadioButton("Orange");
    this.green = new JRadioButton("Green");

    // Set the radio button that is initially selected depending on the existing preferences
    Color current = Preferences.getInstance().getHighlightColour();
    if (current == Color.BLUE) {
      this.blue.setSelected(true);
    }
    else if (current == Color.RED) {
      this.red.setSelected(true);
    }
    else if (current == Color.ORANGE) {
      this.orange.setSelected(true);
    }
    else if (current == Color.GREEN) {
      this.green.setSelected(true);
    }

    this.highlightColourLabel = new JLabel("Select highlight colour for keywords:");

    // Create the checkbox with initial value from the preferences
    this.numbers = new JCheckBox("Highlight numbers?", Preferences.getInstance().willHighlightNumbers());

    this.okButton = new JButton("OK");
    this.okButton.addActionListener(new ActionListener() {

      // Update the preferences using the options selected by the user
      @Override
      public void actionPerformed(ActionEvent e) {
        // Get the preferences singleton object instance
        Preferences prefs = Preferences.getInstance();

        // Set the highlight colour depending on which option is selected
        if (PreferencesDialog.this.blue.isSelected()) {
          prefs.setHighlightColour(Color.BLUE);
        }
        else if (PreferencesDialog.this.red.isSelected()) {
          prefs.setHighlightColour(Color.RED);
        }
        else if (PreferencesDialog.this.orange.isSelected()) {
          prefs.setHighlightColour(Color.ORANGE);
        }
        else if (PreferencesDialog.this.green.isSelected()) {
          prefs.setHighlightColour(Color.GREEN);
        }

        // Set the numbers option depending on the input
        prefs.setWillHighlightNumbers(PreferencesDialog.this.numbers.isSelected());
        PreferencesDialog.this.dispose();
      }
    });

    // A button group makes sure only one option can be selected at any one time
    ButtonGroup group = new ButtonGroup();
    group.add(this.blue);
    group.add(this.red);
    group.add(this.orange);
    group.add(this.green);

    this.add(this.highlightColourLabel);
    this.add(this.blue);
    this.add(this.red);
    this.add(this.orange);
    this.add(this.green);
    this.add(this.numbers);
    this.add(this.okButton);
  }
}
