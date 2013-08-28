package uk.co.ryanharrison.snippetmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

/**
 * MainFrame is the main user interface of the application. It provides abilities to create/edit/organise/delete/save snippets.
 * 
 * @author Ryan Harrison
 */
public class MainFrame extends JPanel implements TreeSelectionListener, ActionListener, SnippetSetChangedListener,
    FindReplaceObserver {

  /** Serialisation identifier */
  private static final long   serialVersionUID = 2591543453130285121L;

  /** The data model for use throughout the application. All changes to the snippets go through this model */
  private SnippetManager      manager;

  /** The text pane to show snippet content in */
  private SnippetTextPane     snippetPane;

  /** The tree to show all the current snippets in */
  private JTree               tree;

  /** File chooser to allow user to choose file paths */
  private JFileChooser        fc;

  /** The drop down list of supported languages */
  private JComboBox<Language> languages;

  /** The text field used to search for a snippet depending on its content */
  private JTextField          searchField;

  /** Label to display information about the editor status */
  private JLabel              statusBar;

  /** Label to display the description of the current snippet */
  private JLabel              description;

  /** The frame parent of this panel */
  private JFrame              parent;

  /** The button to expand all nodes in the tree view */
  private JButton             expandButton;

  /** The button to collapse all nodes in the tree view */
  private JButton             collapseButton;

  /** Date formatter used to format the current date for timestamps */
  private DateFormat          dateFormatter;

  /** Flag specifying whether or not the snippet set has been modified without saving to a file */
  private boolean             hasSavedToFile;

  /** The snippet that is currently loaded in the editor */
  private Snippet             snippet;

  /** The tree path directing to the currently loaded snippet in the tree view */
  private TreePath            treePath;

  /**
   * Construct a new SnippetManager frame, Initialise all GUI elements and add events
   * 
   * @param parent
   *          The parent of this panel
   * @throws NullPointerException
   *           If the parent is null
   */
  public MainFrame(JFrame parent) {
    super(new GridLayout(1, 0));

    if (parent == null) {
      throw new NullPointerException("Parent frame cannot be null");
    }

    // Initialise fields
    this.parent = parent;
    this.manager = new SnippetManager();
    this.manager.addSnippetSetChangedListener(this);
    this.fc = new XMLFileChooser();
    this.dateFormatter = new SimpleDateFormat("hh:mm a dd/MM/yyyy");
    this.hasSavedToFile = true;
    this.treePath = null;

    // Create the root node of the tree
    TreeNode node = new DefaultMutableTreeNode("Snippets");
    // Create a tree with the previously created root node that allows one selection at a time
    this.tree = new JTree(new FilteredTreeModel(new DefaultTreeModel(node)));
    this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    // Listen for when the selection changes in the tree
    this.tree.addTreeSelectionListener(this);

    // Create a scroll pane and add the tree to it
    JScrollPane treeView = new JScrollPane(this.tree);

    // Create the snippet viewing pane
    this.snippetPane = new SnippetTextPane();

    // Create a pane to show the line numbers against the snippet pane
    final JTextPane lines = new JTextPane();
    try {
      lines.getDocument().insertString(0, "1", null);
    }
    catch (BadLocationException e) {
      e.printStackTrace();
    }

    this.snippetPane.setFont(new Font("Arial", Font.PLAIN, 14));

    JScrollPane snippetView = new JScrollPane(this.snippetPane);
    snippetView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    // Add the lines to the snippet scroll pane to ensure it displays next to it
    snippetView.setRowHeaderView(lines);

    // Change properties of the lines pane to improve appearance and make sure the user cannot edit it
    lines.setBackground(Color.LIGHT_GRAY);
    lines.setFont(new Font("Arial", Font.PLAIN, 14));
    lines.setEditable(false);

    // Add the dynamic line number generation
    this.snippetPane.getDocument().addDocumentListener(new DocumentListener() {

      /** Refresh the line numbers */
      @Override
      public void changedUpdate(DocumentEvent de) {
        lines.setText(this.getText());
      }

      /**
       * Get the line number text corresponding to the current number of lines
       * 
       * @return The line number text
       */
      public String getText() {
        // Get the current caret position
        int caretPosition = MainFrame.this.snippetPane.getDocument().getLength();
        Element root = MainFrame.this.snippetPane.getDocument().getDefaultRootElement();
        String text = "1" + "\n";
        // Loop through each line in the text, for each adding on a new number to the line text
        for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
          text += i + "\n";
        }
        return text;
      }

      /** Refresh the line numbers */
      @Override
      public void insertUpdate(DocumentEvent de) {
        lines.setText(this.getText());
      }

      /** Refresh the line numbers */
      @Override
      public void removeUpdate(DocumentEvent de) {
        lines.setText(this.getText());
      }

    });

    // Add the necessary listeners to the snippet editor
    this.snippetPane.addListeners();

    JPanel statusPanel = new JPanel();
    statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

    // Initialise the status bar with default text
    this.statusBar = new JLabel("Line: 1     Column: 1");
    this.statusBar.setHorizontalAlignment(SwingConstants.RIGHT);

    this.description = new JLabel("No Description");
    this.description.setHorizontalAlignment(SwingConstants.LEFT);

    statusPanel.add(this.description);
    statusPanel.add(Box.createHorizontalGlue());
    statusPanel.add(this.statusBar);

    // Add a listener to monitor when the caret position changes
    this.snippetPane.addCaretListener(new CaretListener() {

      // When the caret position changes, update the status bar with the current information
      @Override
      public void caretUpdate(CaretEvent e) {
        int dot = e.getDot();
        // Get the current line and column numbers
        int line = MainFrame.this.snippetPane.getCurrentLine(dot) + 1;
        int column = MainFrame.this.snippetPane.getColumnAtCaret(dot);
        // Update the status bar
        MainFrame.this.statusBar.setText("Line: " + line + "     Column: " + column);
        MainFrame.this.hasSavedToFile = false;
      }
    });

    // Create the languages combo box with all values of the Language enumeration
    this.languages = new JComboBox<Language>(Language.values());
    this.languages.setMinimumSize(new Dimension(100, 25));
    this.languages.setSelectedIndex(this.languages.getItemCount() - 1);

    // Every time the language changes, set the SyntaxHighlighter of the snippet viewing pane to the corresponding instance in the
    // new selected language. This allows syntax highlighting to remain consistent with the current language
    this.languages.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // Get the new selected Language
        Language selected = (Language) MainFrame.this.languages.getSelectedItem();
        // Assign the SyntaxHighlighter of the Language to the snippet pane to update the highlighting
        MainFrame.this.snippetPane.setSyntaxHighlighter(selected.getSyntaxHighlighter());
      }
    });

    // Initialise the searching field
    this.searchField = new HintTextField("Search for snippet...");
    // Add a listener to the search field to filter the tree when text is entered
    this.searchField.getDocument().addDocumentListener(this.createDocumentListener(this.tree, this.searchField));

    // Create the two tree buttons
    this.expandButton = new JButton("Expand All");
    this.expandButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        MainFrame.this.expandTree();
      }
    });

    this.collapseButton = new JButton("Collapse All");
    this.collapseButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        MainFrame.this.collapseTree();
      }
    });

    // Add the scroll panes to a split pane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    JPanel left = new JPanel(new BorderLayout());
    left.add(treeView, BorderLayout.CENTER);

    JPanel buttons = new JPanel();
    buttons.add(this.expandButton);
    buttons.add(this.collapseButton);

    JPanel bottom = new JPanel(new BorderLayout());
    bottom.add(buttons, BorderLayout.NORTH);
    bottom.add(this.searchField, BorderLayout.SOUTH);

    left.add(bottom, BorderLayout.SOUTH);
    splitPane.setLeftComponent(left);

    JPanel right = new JPanel(new BorderLayout());
    right.add(snippetView, BorderLayout.CENTER);
    right.add(statusPanel, BorderLayout.SOUTH);
    splitPane.setRightComponent(right);

    // Set some resizing dimensions of the panels
    Dimension minimumSize = new Dimension(100, 100);
    right.setMinimumSize(minimumSize);
    left.setMinimumSize(minimumSize);
    splitPane.setDividerLocation(200);
    splitPane.setPreferredSize(new Dimension(700, 500));

    // Add the split pane to this panel.
    this.add(splitPane);

    // Add a listener that prompts the user to save when they exit the application
    parent.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) {
        // If the snippet have not been save
        if (!MainFrame.this.hasSavedToFile) {
          int dialogResult = MainFrame.this.promptToSave();
          // If the user wants to save
          if (dialogResult == JOptionPane.YES_OPTION) {
            MainFrame.this.saveToFile();
          }
          else if (dialogResult == JOptionPane.CANCEL_OPTION) {
            return;
          }
        }
        System.exit(0);
      }
    });
  }

  /**
   * Handle the action event of each menu item
   * 
   * @param e
   *          Event information
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    JMenuItem item = (JMenuItem) e.getSource();

    // If the source is the new menu item, create a new snippet
    if (item.getText().equals("New snippet")) {
      this.updateCurrentSnippet();
      SnippetInformationEditor editor = new SnippetInformationEditor(this.parent, null, true);
      editor.setVisible(true);
      // If the user clicked the ok button
      if (editor.getDialogResult() == DialogResult.OK_OPTION) {
        try {
          // Add the new snippet to the set of snippets
          this.manager.addSnippet(editor.getNewSnippet());
        }
        // Prompt the user if the snippet already exists
        catch (DuplicateSnippetException exc) {
          JOptionPane.showMessageDialog(this.parent, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
    // If the source is the exit menu item, exit the program
    else if (item.getText().equals("Exit")) {
      // If the user wants to exit yet has not saved the snippets prompt the user to save them
      if (!this.hasSavedToFile) {
        int dialogResult = this.promptToSave();
        // If the user wants to save
        if (dialogResult == JOptionPane.YES_OPTION) {
          this.saveToFile();
        }
        else if (dialogResult == JOptionPane.CANCEL_OPTION) {
          return;
        }
      }
      System.exit(0);
    }
    // If the source is the save menu item, prompt the user to enter a path to save
    else if (item.getText().equals("Save As...")) {
      this.updateCurrentSnippet();
      this.saveToFile();
    }
    // If the source is the load menu item, prompt the user to enter a path to load snippets from
    else if (item.getText().equals("Load...")) {
      this.updateCurrentSnippet();
      // Get the user to select a file to load
      if (this.fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = this.fc.getSelectedFile();
        try {
          // Load the file
          this.manager.loadFromFile(file.getAbsolutePath());
          this.hasSavedToFile = true;
        }
        catch (NullPointerException | ParserConfigurationException | SAXException | IOException e1) {
          JOptionPane.showMessageDialog(this, "Unable to load snippets from file", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
    else if (item.getText().equals("Edit current snippet")) {
      this.updateCurrentSnippet();
      Snippet selected = this.getCurrentlySelectedSnippet();
      if (selected != null) {
        // Create an editor dialog to edit the current snippet
        SnippetInformationEditor editor = new SnippetInformationEditor(this.parent, selected, false);
        editor.setVisible(true);
        // If the user presses the ok button
        if (editor.getDialogResult() == DialogResult.OK_OPTION) {
          Snippet newSnippet = editor.getNewSnippet();
          this.snippet = newSnippet;
          // Update the snippet to new one from the editor
          this.manager.updateSnippet(selected, newSnippet);
        }
      }
      else {
        JOptionPane.showMessageDialog(this, "No snippet selected", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if (item.getText().endsWith("Delete current snippet")) {
      this.updateCurrentSnippet();
      Snippet selected = this.getCurrentlySelectedSnippet();
      if (selected != null) {
        // Delete the snippet from the current set
        this.manager.deleteSnippet(selected);
        this.snippet = null;
      }
      else {
        JOptionPane.showMessageDialog(this, "No snippet selected", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    // If the source is the find/replace menu item, open up the find/replace dialog
    else if (item.getText().equals("Find/Replace")) {
      FindReplace fr = new FindReplace(this.snippetPane.getText());
      fr.addFindReplaceObserver(this);
      fr.setVisible(true);
    }
    // If the source is the status bar menu item, toggle the visibility of the status bar labels
    else if (item.getText().equals("Status Bar")) {
      this.statusBar.setVisible(!this.statusBar.isVisible());
      this.description.setVisible(!this.description.isVisible());
    }
    // If the source is the undo menu item, undo a change in the snippet view
    else if (item.getText().equals("Undo")) {
      this.snippetPane.undo();
      item.setEnabled(this.snippetPane.canUndo());
    }
    else if (item.getText().equals("Cut")) {
      this.snippetPane.cut();
    }
    else if (item.getText().equals("Copy")) {
      this.snippetPane.copy();
    }
    else if (item.getText().equals("Paste")) {
      this.snippetPane.paste();
    }
    else if (item.getText().equals("Delete")) {
      this.snippetPane.replaceSelection("");
    }
    else if (item.getText().equals("Select All")) {
      this.snippetPane.selectAll();
    }
    else if (item.getText().equals("Time/Date")) {
      // Add a timestamp to the editor
      String date = this.dateFormatter.format(Calendar.getInstance().getTime());
      this.snippetPane.appendText(date);
    }
    else if (item.getText().equals("Go To")) {
      // Show the go to dialog
      GoToDialog dialog = new GoToDialog(this.parent, this.snippetPane.getNumberLines());
      dialog.setVisible(true);
      // If the user presses ok
      if (dialog.getDialogResult() == DialogResult.OK_OPTION) {
        int line = dialog.getLineNumber();
        // Go to the line number the user entered
        this.snippetPane.gotoLineInDocument(line);
      }
    }
    else if (item.getText().equals("About Snippet Manager")) {
      AboutDialog dialog = new AboutDialog(this.parent);
      dialog.setVisible(true);
    }
    else if (item.getText().equals("Preferences")) {
      PreferencesDialog dialog = new PreferencesDialog(this.parent);
      dialog.setVisible(true);
      // Update the syntax highlighter to the new colours if necessary
      Language selected = (Language) this.languages.getSelectedItem();
      MainFrame.this.snippetPane.setSyntaxHighlighter(selected.getSyntaxHighlighter());
    }
  }

  /**
   * Collapse all the nodes inside the tree view
   */
  private void collapseTree() {
    for (int i = 1; i < this.tree.getRowCount(); i++) {
      this.tree.collapseRow(i);
    }
  }

  /**
   * Create a listener for the filter textfield. When the text has changed, apply a new filter to the tree view to filter and show
   * only those nodes that match the filter text string
   */
  private DocumentListener createDocumentListener(final JTree tree, final JTextField filter) {
    return new DocumentListener() {

      /** Apply a new filter to the tree view with the filter text */
      public void applyFilter() {
        // Get the current model
        FilteredTreeModel filteredModel = (FilteredTreeModel) tree.getModel();
        // Set the new filter text
        filteredModel.setFilter(filter.getText());

        // Reload the model
        DefaultTreeModel treeModel = (DefaultTreeModel) filteredModel.getTreeModel();
        treeModel.reload();

        // Expand all the results
        MainFrame.this.expandTree();
      }

      /** When the text changes, apply a new filter */
      @Override
      public void changedUpdate(final DocumentEvent e) {
        this.applyFilter();
      }

      /** When the text changes, apply a new filter */
      @Override
      public void insertUpdate(final DocumentEvent e) {
        this.applyFilter();
      }

      /** When the text changes, apply a new filter */
      @Override
      public void removeUpdate(final DocumentEvent e) {
        this.applyFilter();
      }
    };
  }

  /**
   * Create a menu bar associated with the SnippetManager. This is added to the frame when initialised.
   * 
   * @return A menu bar associated with this panel
   */
  public JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // Add the file menu
    // Action Listeners are also added to each item
    JMenu file = new JMenu("File");

    JMenuItem save = new JMenuItem("Save As...");
    save.addActionListener(this);
    save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    file.add(save);

    JMenuItem load = new JMenuItem("Load...");
    load.addActionListener(this);
    load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    file.add(load);
    file.addSeparator();

    JMenuItem exit = new JMenuItem("Exit");
    exit.addActionListener(this);
    file.add(exit);

    // Add the file menu to the menu bar
    menuBar.add(file);

    // Create the edit menu
    JMenu edit = new JMenu("Edit");

    JMenuItem undo = new JMenuItem("Undo");
    undo.addActionListener(this);
    undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    edit.add(undo);
    edit.addSeparator();

    JMenuItem cut = new JMenuItem("Cut");
    cut.addActionListener(this);
    cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    edit.add(cut);

    JMenuItem copy = new JMenuItem("Copy");
    copy.addActionListener(this);
    copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    edit.add(copy);

    JMenuItem paste = new JMenuItem("Paste");
    paste.addActionListener(this);
    paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    edit.add(paste);

    JMenuItem delete = new JMenuItem("Delete");
    delete.addActionListener(this);
    delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    edit.add(delete);

    JMenuItem findReplace = new JMenuItem("Find/Replace");
    findReplace.addActionListener(this);
    findReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    edit.addSeparator();
    edit.add(findReplace);

    JMenuItem goTo = new JMenuItem("Go To");
    goTo.addActionListener(this);
    goTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
    edit.add(goTo);

    JMenuItem selectAll = new JMenuItem("Select All");
    selectAll.addActionListener(this);
    selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    edit.addSeparator();
    edit.add(selectAll);

    JMenuItem timeDate = new JMenuItem("Time/Date");
    timeDate.addActionListener(this);
    timeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    edit.add(timeDate);

    // Add the edit menu to the menu bar
    menuBar.add(edit);

    // Create the Snippet menu
    JMenu snippet = new JMenu("Snippet");

    JMenuItem newItem = new JMenuItem("New snippet");
    newItem.addActionListener(this);
    newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    snippet.add(newItem);
    snippet.addSeparator();

    JMenuItem editItem = new JMenuItem("Edit current snippet");
    editItem.addActionListener(this);
    snippet.add(editItem);
    snippet.addSeparator();

    JMenuItem deleteCurrent = new JMenuItem("Delete current snippet");
    deleteCurrent.addActionListener(this);
    snippet.add(deleteCurrent);

    // Add the snippet menu to the menu bar
    menuBar.add(snippet);

    // Create the view menu
    JMenu view = new JMenu("View");
    JMenuItem status = new JMenuItem("Status Bar");
    status.addActionListener(this);
    view.add(status);

    // Add the view menu to the menu bar
    menuBar.add(view);

    // Create the options menu
    JMenu options = new JMenu("Options");
    JMenuItem preferences = new JMenuItem("Preferences");
    preferences.addActionListener(this);
    options.add(preferences);

    // Add the options menu to the menu bar
    menuBar.add(options);

    // Create the help menu
    JMenu help = new JMenu("Help");
    JMenuItem about = new JMenuItem("About Snippet Manager");
    about.addActionListener(this);
    help.add(about);

    // Add the help menu to the menu bar
    menuBar.add(help);

    // Add the languages combo box to the right hand corner of the menu bar
    menuBar.add(Box.createHorizontalGlue());
    menuBar.add(this.languages);
    return menuBar;
  }

  /**
   * Display a snippet object to the user
   * 
   * @param snippet
   *          The Snippet pane
   */
  private void displaySnippet(Snippet snippet) {
    if (snippet == null) {
      this.snippetPane.setText("No snippet selected");
      this.description.setText("No Description");
      this.parent.setTitle("Snippet Manager");
    }
    else {
      // Update the title bar to show the snippet name
      this.parent.setTitle("Snippet Manager - " + snippet.getName());
      // Set the text in the editor to the snippet content
      this.snippetPane.setText(snippet.getSnippet());
      // Change the current language
      this.languages.setSelectedItem(snippet.getLanguage());
      // Set the description label
      if (snippet.getDescription().isEmpty()) {
        this.description.setText("No Description");
      }
      else {
        this.description.setText(snippet.getDescription());
      }
    }
  }

  /**
   * Expand all the nodes inside the tree view
   */
  private void expandTree() {
    for (int i = 0; i < this.tree.getRowCount(); i++) {
      this.tree.expandRow(i);
    }
  }

  /**
   * Get the currently selected Snippet if one has been selected
   * 
   * @return The currently selected Snippet, null if none has been selected
   */
  private Snippet getCurrentlySelectedSnippet() {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();

    if (node == null) {
      return null;
    }

    // Get the object associated with the node
    Object nodeInfo = node.getUserObject();

    // If the node is a snippet object return it
    if (nodeInfo instanceof Snippet) {
      Snippet snippet = (Snippet) nodeInfo;
      return snippet;
    }
    // Otherwise return null
    else {
      return null;
    }
  }

  /**
   * Called when a find/replace action has occurred in the find replace dialog. This frame is the observer to the dialog which is
   * the observable
   * 
   * @param observable
   *          The observable where the action occurred
   * 
   * @see uk.co.ryanharrison.snippetmanager.FindReplaceObserver#onFindReplaceAction(uk.co.ryanharrison.snippetmanager.FindReplaceObservable)
   */
  @Override
  public void onFindReplaceAction(FindReplaceObservable observable) {
    // If the observable is a find replace dialog
    if (observable instanceof FindReplace) {
      FindReplace fr = (FindReplace) observable;
      // Reset the text to take into account a replace action
      this.snippetPane.setText(fr.getText());
      // If a match has been found
      if (fr.getStart() != fr.getEnd()) {
        // Select/highlight the match in the editor
        this.snippetPane.select(fr.getStart(), fr.getEnd());
      }
    }
  }

  /**
   * Called when the snippet set in the snippet manager has been changed in some way. This frame is the observer to the snippet
   * manager which is the observable.
   * 
   * @param subject
   *          The observable whose snippet set has changed
   * @param type
   *          The type of the change to the snippet set
   * 
   * @see uk.co.ryanharrison.snippetmanager.SnippetSetChangedListener#onSnippetSetChanged(uk.co.ryanharrison.snippetmanager.SnippetSetObservable,
   *      uk.co.ryanharrison.snippetmanager.ChangeType)
   */
  @Override
  public void onSnippetSetChanged(SnippetSetObservable subject, ChangeType type) {
    // Refresh the contents of the tree to show newly added/removed snippets
    this.refreshSnippetTree();
    // Depending on the type of change, show a message to the user
    switch (type) {
      case Add:
        JOptionPane.showMessageDialog(this, "Snippet successfully added", "Snippet added", JOptionPane.INFORMATION_MESSAGE);
        break;
      case Delete:
        JOptionPane.showMessageDialog(this, "Snippet successfully deleted", "Snippet deleted", JOptionPane.INFORMATION_MESSAGE);
        this.snippetPane.setText("");
        this.snippet = null;
        break;
      case Update:
        JOptionPane.showMessageDialog(this, "Snippet successfully updated", "Snippet updated", JOptionPane.INFORMATION_MESSAGE);
        break;
      case Save:
        JOptionPane.showMessageDialog(this, "Snippets successfully saved", "Snippet updated", JOptionPane.INFORMATION_MESSAGE);
        break;
      case Load:
        this.snippetPane.setText("No snippet selected");
        JOptionPane.showMessageDialog(this, "Snippets successfully loaded", "Snippet updated", JOptionPane.INFORMATION_MESSAGE);
        break;
    }
    this.hasSavedToFile = false;
    this.description.setText("No Description");
  }

  /**
   * Prompt the user to save to a file if they have not done so yet and if something has been modified
   * 
   * @return The result of the prompt to the user
   */
  private int promptToSave() {
    return JOptionPane.showConfirmDialog(this.parent, "Would you like to save changes to your snippets?", "Save Changes",
        JOptionPane.YES_NO_CANCEL_OPTION);
  }

  /**
   * Refresh the snippet tree view with the new snippets from the manager data model
   */
  private void refreshSnippetTree() {
    TreeNode node = this.manager.createTreeNodeFromSnippets();
    TreeModel model = new FilteredTreeModel(new DefaultTreeModel(node));
    this.tree.setModel(model);
    this.expandTree();
  }

  /**
   * Save the snippets to a file, prompting the user for a path to save to
   */
  private void saveToFile() {
    this.updateCurrentSnippet();
    // Show the file chooser
    if (this.fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      String path = this.fc.getSelectedFile().getAbsolutePath();
      if (!path.endsWith(".xml")) {
        path = path + ".xml";
      }
      try {
        // Save the snippets to the file the user chose
        this.manager.saveSnippetsToFile(path);
        this.hasSavedToFile = true;
      }
      // If there was an error, prompt the user
      catch (ParserConfigurationException | TransformerException e1) {
        JOptionPane.showMessageDialog(this, "Unable to save snippets to file", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Set focus to the snippet text pane when the application is started
   */
  public void setDefaultFocus() {
    this.snippetPane.requestFocus();
  }

  /** Update the current snippet that is being viewed with the new text entered by the user from the editor */
  private void updateCurrentSnippet() {
    if (this.snippet != null) {
      Snippet newSnippet = new Snippet(this.snippet.getName(), this.snippetPane.getText(), this.snippet.getDescription(),
          this.snippet.getKeywords(), this.snippet.getLanguage());
      // Update the snippet in the data model with the new text
      this.manager.updateSnippetData(this.snippet, newSnippet);
      // Update the tree
      this.tree.getModel().valueForPathChanged(this.treePath, newSnippet);
    }
  }

  /**
   * Method called when the user clicks on a different node in the tree. Handler displays the corresponding Snippet to the selected
   * node.
   * 
   * @param e
   *          Event information
   * 
   * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
   */
  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (!this.hasSavedToFile) {
      this.updateCurrentSnippet();
    }
    // Get the new selected node
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();

    if (node == null) {
      return;
    }

    // Get the object associated with the node
    Object nodeInfo = node.getUserObject();

    // If the node is a leaf node (no children) then display it
    if (node.isLeaf() && nodeInfo instanceof Snippet) {
      Snippet snippet = (Snippet) nodeInfo;
      this.snippet = snippet;
      this.treePath = e.getPath();
      this.displaySnippet(snippet);
    }
    // Otherwise display alternate message to user
    else {
      this.displaySnippet(null);
      this.snippet = null;
    }
  }

  /**
   * Create the GUI, setting a custom look and feel, and show the window
   */
  private static void createAndShowGUI() {
    // Attempt to set a custom look and feel of the current operating system.
    // This makes the frame look much more native.
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e) {
      System.err.println("Couldn't use system look and feel.");
    }

    // Create and set up the frame
    JFrame frame = new JFrame("Snippet Manager");
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    // Add content to the window.
    MainFrame panel = new MainFrame(frame);
    frame.setJMenuBar(panel.createMenuBar());
    frame.add(panel);

    // Display the window.
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    panel.setDefaultFocus();
  }

  /**
   * Main entry point of the program. Create and show the main window
   * 
   * @param args
   *          Command line arguments
   */
  public static void main(String[] args) {
    // Create and display the frame
    javax.swing.SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        createAndShowGUI();
      }
    });
  }
}