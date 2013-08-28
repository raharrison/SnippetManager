/**
 * SnippetInformationEditor.java
 */

package uk.co.ryanharrison.snippetmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A dialog box that lets users enter and modify the information associated with a snippet
 * 
 * @author Ryan Harrison
 */
public class SnippetInformationEditor extends JDialog {

  /** Serialisation identifier */
  private static final long   serialVersionUID = 7825777491222888637L;

  /** The name of the snippet */
  private JTextField          nameField;

  /** The keywords of the snippet */
  private JTextField          keywordField;

  /** The description of the snippet */
  private JTextField          descriptionField;

  /** The button to save the information */
  private JButton             saveButton;

  /** The button to close the dialog without saving */
  private JButton             closeButton;

  /** The label to display the name */
  private JLabel              nameLabel;

  /** The label to display keywords */
  private JLabel              keywordLabel;

  /** The label to display the description */
  private JLabel              descriptionLabel;

  /** The language of the snippet */
  private JComboBox<Language> languages;

  /** The result of this dialog being shown to the user. Whether or not they clicked the ok or cancel buttons */
  private DialogResult        dialogResult;

  /** The snippet that is being edited */
  private Snippet             snippet;

  /**
   * Create the dialog with the frame parent and the snippet to edit
   * 
   * @param parent
   *          The frame parent
   * @param snippet
   *          The snippet object to edit
   * @param enableNameField
   *          Whether or not to enable the user to modify the name of the snippet
   */
  public SnippetInformationEditor(JFrame parent, Snippet snippet, boolean enableNameField) {
    super(parent, "Snippet information editor", true);
    this.createGui(enableNameField);
    if (snippet != null) {
      this.fillFieldsFromSnippet(snippet);
      this.snippet = snippet;
    }
  }

  /**
   * Create the user interface
   * 
   * @param enableNameField
   *          Whether or not to enable the user to modify the name of the snippet
   */
  private void createGui(boolean enableNameField) {
    // Set frame properties
    this.setLayout(new BorderLayout());
    this.setSize(500, 175);
    this.setResizable(false);
    this.setLocationRelativeTo(null);

    // Create the controls
    this.nameField = new HintTextField("Enter snippet name");
    this.nameField.setEditable(enableNameField);
    this.keywordField = new HintTextField("Enter keywords separated by commas");
    this.descriptionField = new HintTextField("Enter snippet description");

    this.nameLabel = new JLabel("Snippet name:");
    this.keywordLabel = new JLabel("Snippet keywords:");
    this.descriptionLabel = new JLabel("Snippet description:");

    this.saveButton = new JButton("Save");
    this.saveButton.addActionListener(new ActionListener() {

      // When the save button is clicked
      @Override
      public void actionPerformed(ActionEvent e) {
        // The name field cannot be empty
        if (SnippetInformationEditor.this.nameField.getText().isEmpty()) {
          JOptionPane.showMessageDialog(SnippetInformationEditor.this, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Set the result of this dialog and exit
        else {
          SnippetInformationEditor.this.dialogResult = DialogResult.OK_OPTION;
          SnippetInformationEditor.this.dispose();
        }
      }
    });

    this.closeButton = new JButton("Close");
    this.closeButton.addActionListener(new ActionListener() {

      // When the cancel button is pressed set the dialog result and exit
      @Override
      public void actionPerformed(ActionEvent e) {
        SnippetInformationEditor.this.dialogResult = DialogResult.CANCEL_OPTION;
        SnippetInformationEditor.this.dispose();
      }
    });

    this.languages = new JComboBox<>(Language.values());

    // Create the left hand panel and add components
    GridLayout layout = new GridLayout(3, 3, 10, 10);
    JPanel left = new JPanel(layout);
    left.add(this.nameLabel);
    left.add(this.nameField);
    left.add(this.keywordLabel);
    left.add(this.keywordField);
    left.add(this.descriptionLabel);
    left.add(this.descriptionField);

    // Create the right hand panel and add components
    JPanel right = new JPanel();
    right.setMaximumSize(new Dimension(300, 150));
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
    right.add(this.saveButton);
    right.add(Box.createRigidArea(new Dimension(0, 5)));
    right.add(this.closeButton);
    right.add(Box.createRigidArea(new Dimension(0, 20)));
    right.add(this.languages);

    // Add both panels to the frame
    this.add(left, BorderLayout.WEST);
    this.add(right, BorderLayout.EAST);

    // Set a default value for the dialog result
    this.dialogResult = DialogResult.CANCEL_OPTION;
  }

  /**
   * Fill in the values of the components from an existing snippet
   * 
   * @param snippet
   *          The snippet to use when filling in the default values of the components
   */
  private void fillFieldsFromSnippet(Snippet snippet) {
    this.nameField.setText(snippet.getName());
    this.languages.setSelectedItem(snippet.getLanguage());
    this.descriptionField.setText(snippet.getDescription());
    String keywords = snippet.getKeywords().toString();
    this.keywordField.setText(keywords.replace("[", "").replace("]", ""));
  }

  /**
   * Get the result of this dialog being shown the user
   * 
   * @return The result of this dialog
   */
  public DialogResult getDialogResult() {
    return this.dialogResult;
  }

  /**
   * Gets a newly modified snippet with the new information entered by the user
   * 
   * @return A new snippet object with new information
   */
  public Snippet getNewSnippet() {
    String name = this.nameField.getText();
    String description = this.descriptionField.getText();
    Language language = (Language) this.languages.getSelectedItem();

    // As the keywords are comma separated, split them and add them all to a set first
    String[] keywordsString = this.keywordField.getText().split(",");
    for (int i = 0; i < keywordsString.length; i++) {
      keywordsString[i] = keywordsString[i].trim().toLowerCase();
    }
    Set<String> keywords = new HashSet<String>(Arrays.asList(keywordsString));
    String data = this.snippet == null ? "" : this.snippet.getSnippet();
    // Return a new snippet from the data entered into this dialog box
    return new Snippet(name, data, description, keywords, language);
  }
}
