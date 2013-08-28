/**
 * HintTextField.java
 */

package uk.co.ryanharrison.snippetmanager;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * A custom textfield that shows users a hint on what they should insert into the field
 * 
 * @author Ryan Harrison
 */
public class HintTextField extends JTextField implements FocusListener {

  /** Serialisation identifier */
  private static final long serialVersionUID = 8986901300415132144L;

  /** The hint to show the user */
  private final String      hint;

  /**
   * Create a new hint text field
   * 
   * @param hint
   *          The hint to show the user on what they should enter
   * @throws NullPointerException
   *           If the hint is null
   */
  public HintTextField(String hint) {
    super(hint);
    if (hint == null) {
      throw new NullPointerException("Hint cannot be null");
    }
    this.hint = hint;
    this.addFocusListener(this);
  }

  /**
   * When focus is gained if the text is empty, remove the hint
   * 
   * @param e
   *          Event params
   * 
   * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
   */
  @Override
  public void focusGained(FocusEvent e) {
    // If the text is empty, remove the hint
    if (this.getText().isEmpty()) {
      this.setText("");
    }
  }

  /**
   * When focus is lost if the text is empty show the hint
   * 
   * @param e
   *          Event params
   * 
   * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
   */
  @Override
  public void focusLost(FocusEvent e) {
    // If the text is empty show the hint
    if (this.getText().isEmpty()) {
      this.setText(this.hint);
    }
  }

  /**
   * Get the text from this textfield
   * 
   * @return The text from this field. If it is empty return nothing
   * 
   * @see javax.swing.text.JTextComponent#getText()
   */
  @Override
  public String getText() {
    String text = super.getText();
    // If the text is the hint then return nothing
    return text.equals(this.hint) ? "" : text;
  }
}
