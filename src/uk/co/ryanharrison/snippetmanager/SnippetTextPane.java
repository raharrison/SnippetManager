package uk.co.ryanharrison.snippetmanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Utilities;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Class representing a text pane where keywords of languages are highlighted. This text pane also has a right click menu to display
 * common tools.
 * 
 * @author Ryan Harrison
 */
public class SnippetTextPane extends JTextPane implements ActionListener {

  /** Serialisaton identifier */
  private static final long serialVersionUID = 5562822940346845853L;

  /** The highlighter used when highlighting the keywords in the text of this pane */
  private SyntaxHighlighter highlighter;

  /** The right click context menu */
  private JPopupMenu        contextMenu;

  /** A manager that takes care of undoing changes made to the text in the pane */
  private UndoManager       undo;

  /** Menu item to undo changes. This is a field as it will be enabled/disabled dynamically */
  private JMenuItem         undoItem;

  /**
   * Construct a new SnippetTextPane. Initialise fields and construct the context menu
   */
  public SnippetTextPane() {
    super();
    this.highlighter = null;
    this.undo = new UndoManager();

    // Create the context menu
    this.contextMenu = new JPopupMenu();

    this.undoItem = new JMenuItem("Undo");
    this.undoItem.setEnabled(false);
    this.undoItem.addActionListener(this);
    this.undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    this.contextMenu.add(this.undoItem);
    this.contextMenu.addSeparator();

    JMenuItem cut = new JMenuItem("Cut");
    cut.addActionListener(this);
    cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    this.contextMenu.add(cut);

    JMenuItem copy = new JMenuItem("Copy");
    copy.addActionListener(this);
    copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    this.contextMenu.add(copy);

    JMenuItem paste = new JMenuItem("Paste");
    paste.addActionListener(this);
    paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    this.contextMenu.add(paste);

    JMenuItem delete = new JMenuItem("Delete");
    delete.addActionListener(this);
    delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    this.contextMenu.add(delete);

    JMenuItem selectAll = new JMenuItem("Select All");
    selectAll.addActionListener(this);
    selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    this.contextMenu.addSeparator();
    this.contextMenu.add(selectAll);

    // Enable the drag and drop of text into the pane
    this.setDragEnabled(true);
    this.setDropMode(DropMode.INSERT);

    // Add a mouse listener to determine if the pop up menu should open
    this.addMouseListener(new MouseAdapter() {

      @Override
      public void mousePressed(MouseEvent e) {
        this.showPopUp(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        this.showPopUp(e);
      }

      private void showPopUp(MouseEvent e) {
        // If the context menu should open, open it
        if (e.isPopupTrigger()) {
          SnippetTextPane.this.contextMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    // Enable the ability to undo changes in the current document
    this.getDocument().addUndoableEditListener(new UndoableEditListener() {

      @Override
      public void undoableEditHappened(UndoableEditEvent e) {
        // Remember the edit that has just been made
        SnippetTextPane.this.undo.addEdit(e.getEdit());
        // Set the menu item to be enabled only if an undo can be made
        SnippetTextPane.this.undoItem.setEnabled(SnippetTextPane.this.undo.canUndo());
      }
    });
  }

  /**
   * Handle the actionPerformed event of each context menu item
   * 
   * @param e
   *          Event information
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    JMenuItem item = (JMenuItem) e.getSource();
    String text = item.getText();
    // Depending on which item that has been clicked on, take the appropriate action
    if (text.equals("Undo")) {
      try {
        SnippetTextPane.this.undo.undo();
      }
      catch (CannotUndoException ex) {
        ex.printStackTrace();
      }
    }
    else if (text.equals("Cut")) {
      this.cut();
    }
    else if (text.equals("Copy")) {
      this.copy();
    }
    else if (text.equals("Paste")) {
      this.paste();
    }
    else if (text.equals("Delete")) {
      this.replaceSelection("");
    }
    else if (text.equals("Select All")) {
      this.selectAll();
    }
  }

  /**
   * Add listeners to this pane to highlight the keywords when the appropriate keys are pressed
   */
  public void addListeners() {
    this.addKeyListener(new KeyAdapter() {

      // Called when any key is pressed
      @Override
      public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // If there is text to highlight and the key that was pressed is either space or delete
        if (SnippetTextPane.this.getSelectedText() != null && (code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE)) {
          try {
            // Remove the current selection from the document. This mimics common behaviour in text editors
            SnippetTextPane.this.getDocument().remove(SnippetTextPane.this.getSelectionStart(),
                SnippetTextPane.this.getSelectionEnd() - SnippetTextPane.this.getSelectionStart());
          }
          catch (BadLocationException e1) {
            e1.printStackTrace();
          }
        }

        // If the key that was pressed is space, backspace or delete, highlight the current line for keywords
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE) {
          SnippetTextPane.this.highlightCurrentLine();
        }
      }
    });
  }

  /**
   * Append text to the text pane
   * 
   * @param text
   *          The text to append
   */
  public void appendText(String text) {
    this.setText(this.getText() + text);
  }

  /**
   * Get whether or not an undo operator can be made
   * 
   * @return True if an undo operation can be made, otherwise false
   */
  public boolean canUndo() {
    return this.undo.canUndo();
  }

  /**
   * Get the column number from a caret position in the document
   * 
   * @param caretPosition
   *          The caret position to get the column number frmo
   * @return The column number corresponding to the caret index
   */
  public int getColumnAtCaret(int caretPosition) {
    try {
      return caretPosition - Utilities.getRowStart(this, caretPosition) + 1;
    }
    catch (BadLocationException e) {
      e.printStackTrace();
    }
    return 1;
  }

  /**
   * Get the line in the document from a specified caret position
   * 
   * @param caretPosition
   *          The position to get the line number from
   * @return The line number corresponding to the caret index
   */
  public int getCurrentLine(int caretPosition) {
    Document doc = this.getDocument();
    Element map = doc.getDefaultRootElement();
    return map.getElementIndex(caretPosition);
  }

  /**
   * Get the number of lines in the document
   * 
   * @return The number of lines in the document
   */
  public int getNumberLines() {
    Element root = this.getDocument().getDefaultRootElement();
    return root.getElementCount();
  }

  /**
   * Go to a line in the current document
   * 
   * @param line
   *          The line to go to
   */
  public void gotoLineInDocument(int line) {
    Element root = this.getDocument().getDefaultRootElement();
    // Make sure the line is in the bounds of the document
    line = Math.max(line, 1);
    line = Math.min(line, root.getElementCount());
    this.setCaretPosition(root.getElement(line - 1).getStartOffset());
  }

  /**
   * Use the current SyntaxHighlighter instance (if set) to highlight the keywords in the whole current text
   */
  private void highlight() {
    if (this.highlighter != null) {
      this.highlighter.highlightAll(this);
    }
  }

  /**
   * Use the current SyntaxHighlighter instance (if set) to highlight the keywords in just the current line of text
   */
  public void highlightCurrentLine() {
    if (this.highlighter != null) {
      this.highlighter.highlightCurrentLine(this);
    }
  }

  /**
   * When content is pasted into the pane, highlight all the keywords as well
   * 
   * @see javax.swing.text.JTextComponent#paste()
   */
  @Override
  public void paste() {
    super.paste();
    this.highlight();
  }

  /**
   * Set the SyntaxHighlighter to use when highlighting the keywords in the current text
   * 
   * @param highlighter
   *          The new highlighter to use
   */
  public void setSyntaxHighlighter(SyntaxHighlighter highlighter) {
    this.highlighter = highlighter;
    this.highlight();
  }

  /**
   * Undo the last change
   */
  public void undo() {
    // If an undo can be made, undo it
    if (this.undo.canUndo()) {
      this.undo.undo();
    }
    // Set the menu item to be enabled only if an undo can be made
    this.undoItem.setEnabled(this.undo.canUndo());
  }
}
