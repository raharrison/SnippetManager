package uk.co.ryanharrison.snippetmanager;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;

/**
 * Class that and highlights each keyword of the specified language in a text pane. Numbers in the textPane will also be highlighted
 * in a different colour NOTE: this is not currently implemented
 * 
 * @author Ryan Harrison
 */
public class SyntaxHighlighter {

  /** The regular expression pattern to use when matching keywords in the text */
  private Pattern            pattern;

  private SimpleAttributeSet highlighter = new SimpleAttributeSet();
  private SimpleAttributeSet black       = new SimpleAttributeSet();

  /**
   * Initialise a new SyntaxHighlighter object with the language to highlight
   * 
   * @param language
   *          The language to highlight the keywords of
   */
  public SyntaxHighlighter(Language language) {
    super();
    this.pattern = language.getRegex();

    // Get the colour to highlight to from the preferences singleton
    StyleConstants.setForeground(this.highlighter, Preferences.getInstance().getHighlightColour());
    StyleConstants.setForeground(this.black, Color.BLACK);
  }

  /**
   * Get a matcher object from a piece of text
   * 
   * @param text
   *          The text to match
   * @return A regex matcher that matches text
   */
  private Matcher getMatcherFrom(String text) {
    return this.pattern.matcher(text.replace("\r\n", "\r").replace("\n", "\r"));
  }

  /**
   * Highlight every keyword fond throughout the whole document in the pane
   * 
   * @param pane
   *          The textpane to highlight the keywords in the text of
   */
  public void highlightAll(SnippetTextPane pane) {
    // Set all the text to black initially
    int caret = pane.getCaretPosition();
    pane.select(0, pane.getText().length());
    pane.setCharacterAttributes(this.black, true);
    pane.setCaretPosition(caret);

    // Highlight every valid keyword
    Matcher matcher = this.getMatcherFrom(pane.getText());
    caret = pane.getCaretPosition();

    // Loop through each match
    while (matcher.find()) {
      // Get the indexes
      int begin = matcher.start();
      int end = matcher.end();

      // Select the keyword
      pane.select(begin, end);

      // Highlight the keyword using the colour
      pane.setCharacterAttributes(this.highlighter, true);
    }
    // Reset the caret position and colour to type in afterwards
    pane.setCaretPosition(caret);
    pane.setCharacterAttributes(this.black, true);
  }

  /**
   * Highlight the keywords in the current line (where the users cursor currently is) of a textpane
   * 
   * @param pane
   *          The textpane to highlight the keywords in the text of
   */
  public void highlightCurrentLine(SnippetTextPane pane) {
    // Get the current caret position and the current line number that the user is on
    int caret = pane.getCaretPosition();
    int lineNum = pane.getCurrentLine(caret);
    Element root = pane.getDocument().getDefaultRootElement();
    int line = lineNum;
    // Make sure the line number is in the bounds of the document
    line = Math.max(line, 1);
    line = Math.min(line, root.getElementCount());

    // Get the indexes of the start and end characters of the current line
    int begin, end;
    try {
      begin = Utilities.getRowStart(pane, caret);
      end = Utilities.getRowEnd(pane, caret);
    }
    catch (BadLocationException e1) {
      e1.printStackTrace();
      return;
    }

    String text;
    try {
      // Get the text that will be matched (the current line only)
      text = pane.getText(begin, end - begin);
    }
    catch (BadLocationException e2) {
      e2.printStackTrace();
      return;
    }

    // Set current line to black
    caret = pane.getCaretPosition();
    pane.select(begin, end);
    pane.setCharacterAttributes(this.black, true);
    pane.setCaretPosition(caret);

    // Highlight the current line only
    Matcher matcher = this.getMatcherFrom(text);

    // Loop through each matched keyword
    while (matcher.find()) {
      // Get the indexes of the match
      int beginIndex = matcher.start() + begin;
      int endIndex = matcher.end() + begin;

      // Select the keyword
      pane.select(beginIndex, endIndex);

      // Highlight the keyword using the colour
      pane.setCharacterAttributes(this.highlighter, true);
    }
    // Reset the caret position and colour to type in afterwards
    pane.setCaretPosition(caret);
    pane.setCharacterAttributes(this.black, true);
  }
}
