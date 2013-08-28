/**
 * SnippetManager.java
 */

package uk.co.ryanharrison.snippetmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Snippet Manager class the underlying data model class and is responsible for holding and managing the data of the
 * application. Snippets can be loaded from a file, saved from a file, loaded into a TreeView and edited or deleted.
 * 
 * @author Ryan Harrison
 */
public class SnippetManager implements SnippetSetObservable {

  /**
   * A custom iterator that iterates through each snippet in the set. This is a convinient iterator as looping through a map of sets
   * requires a lot more code
   * 
   * @author Ryan Harrison (rh00148)
   */
  private class SnippetIterator implements Iterator<Snippet> {

    /** Outer iterator goes through a collection of sets of snippets */
    private Iterator<SortedSet<Snippet>> outerIterator;

    /** Inner iterator goes through each set of snippets */
    private Iterator<Snippet>            innerIterator;

    /**
     * The constructor takes as its argument the data structure that it wants to iterate over
     */
    public SnippetIterator(Map<Language, SortedSet<Snippet>> snippets) {
      super();
      // The outer iterator points to the values of the map
      this.outerIterator = snippets.values().iterator();

      // Initially the inner iterator goes through the first element of the outer iterator
      if (this.outerIterator.hasNext()) {
        this.innerIterator = this.outerIterator.next().iterator();
      }

    }

    /**
     * Determines if another values exists in this iterator
     * 
     * @return True if another value exists, otherwise false
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      // While the inner iterator does not have a next element
      while (!this.innerIterator.hasNext()) {
        // Move on to the next outer element if there is one
        if (this.outerIterator.hasNext()) {
          this.innerIterator = this.outerIterator.next().iterator();
        }
        // If there isn't a next outer iterator, return false
        else {
          this.innerIterator = null;
          return false;
        }

      }
      // There must be a next element so return true
      return true;
    }

    /**
     * Get the next element from this iterator
     * 
     * @return The next element
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public Snippet next() {
      // If the inner iterator is not null there must be another snippet so return it
      if (!(this.innerIterator == null)) {
        return this.innerIterator.next();
      }
      // Otherwise return null
      else {
        return null;
      }
    }

    /**
     * Remove the current snippet from the structure
     * 
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
      if (!(this.innerIterator == null)) {
        this.innerIterator.remove();
      }
    }
  }

  /** The actual snippets that are being managed. This is stored as a map of languages to a set of snippets written in that language */
  private Map<Language, SortedSet<Snippet>> snippets;

  /** A list of listeners that will be notified when the snippet set is modified */
  private List<SnippetSetChangedListener>   listeners;

  /**
   * Create a new snippet manager.
   */
  public SnippetManager() {
    super();
    this.snippets = new LinkedHashMap<Language, SortedSet<Snippet>>();
    this.listeners = new ArrayList<SnippetSetChangedListener>();
  }

  /**
   * Add a new snippet to the current set of snippets
   * 
   * @param snippet
   *          The snippet to add
   * @throws DuplicateSnippetException
   *           If the snippet already exists
   * @throws NullPointerException
   *           If the snippet is null
   */
  public void addSnippet(Snippet snippet) throws DuplicateSnippetException, NullPointerException {
    if (snippet == null) {
      throw new NullPointerException("Snippet cannot be null");
    }
    this.addSnippetHelper(snippet);

    // Notify observers that a new snippet has been added
    this.notifyObservers(ChangeType.Add);
  }

  /**
   * Helper method to add a new snippet to the current set
   * 
   * @param snippet
   *          The snippet to add
   * @throws DuplicateSnippetException
   *           If the snippet already exists in the set
   */
  private void addSnippetHelper(Snippet snippet) throws DuplicateSnippetException {
    // Throw an exception if the snippet already exists
    if (this.isDuplicateName(snippet.getName())) {
      throw new DuplicateSnippetException("Snippet with name '" + snippet.getName() + "' already exists");
    }
    // If a snippet of that language already exists
    if (this.snippets.containsKey(snippet.getLanguage())) {
      // Add the new snippet to the set for that language
      Set<Snippet> set = this.snippets.get(snippet.getLanguage());
      set.add(snippet);
    }
    // Otherwise create a new set, add the snippet to it and add the set with the language that it represents
    else {
      SortedSet<Snippet> list = new TreeSet<Snippet>();
      list.add(snippet);
      this.snippets.put(snippet.getLanguage(), list);
    }
  }

  /**
   * Add a listener to the current set of listeners that are notified when the snippet set changes
   * 
   * @param o
   *          The listener to add
   * 
   * @see uk.co.ryanharrison.snippetmanager.SnippetSetObservable#addSnippetSetChangedListener(uk.co.ryanharrison.snippetmanager.SnippetSetChangedListener)
   */
  @Override
  public void addSnippetSetChangedListener(SnippetSetChangedListener o) {
    this.listeners.add(o);
  }

  /**
   * Fill a tree node with the snippets held in the current set. The snippet are grouped by languages. There are branches for each
   * language and then leafs for each snippet written in that language
   * 
   * @return The root node of a tree of snippets
   */
  public TreeNode createTreeNodeFromSnippets() {
    // Create the root node
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Snippets");

    // Loop through each snippet in the currrent set
    for (Entry<Language, SortedSet<Snippet>> entry : this.snippets.entrySet()) {
      // Create a branch node for each language
      DefaultMutableTreeNode category = new DefaultMutableTreeNode(entry.getKey().getValue() + " Snippets");
      top.add(category);
      // Add each snippet to the language branch node as a leaf node
      for (Snippet s : entry.getValue()) {
        category.add(new DefaultMutableTreeNode(s));
      }
    }

    // Return the top root node
    return top;
  }

  /**
   * Delete a snippet from the current collection
   * 
   * @param snippet
   *          The snippet to delete
   * @throws NullPointerException
   *           If the snippet is null
   */
  public void deleteSnippet(Snippet snippet) throws NullPointerException {
    if (snippet == null) {
      throw new NullPointerException("Snippet cannot be null");
    }
    this.deleteSnippetHelper(snippet);

    // Notify all observers that a snippet has been deleted
    this.notifyObservers(ChangeType.Delete);
  }

  /**
   * Helper method to delete a snippet from the current set
   * 
   * @param snippet
   *          The snippet to delete
   */
  private void deleteSnippetHelper(Snippet snippet) {
    SnippetIterator iterator = new SnippetIterator(this.snippets);
    // Iterate through each snippet in the current set using the custom iterator
    while (iterator.hasNext()) {
      Snippet s = iterator.next();
      // If the two snippets match, remove it
      if (s.compareTo(snippet) == 0) {
        iterator.remove();
      }
    }
  }

  /**
   * Get the number of snippets currently in the data model
   * 
   * @return The number of snippets in the data model
   */
  public int getSnippetCount() {
    if (this.snippets.isEmpty()) {
      return 0;
    }

    int count = 0;
    SnippetIterator iterator = new SnippetIterator(this.snippets);
    // Iterate through each snippet in the current set using the custom iterator
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    return count;
  }

  /**
   * Get a snippet in the snippet set with a specified name
   * 
   * @param name
   *          The name of the snippet to retrieve
   * @return The snippet with a name of name, null if a snippet with name does not exist
   * @throws NullPointerException
   *           If the name is null
   */
  public Snippet getSnippetFromName(String name) {
    if (name == null) {
      throw new NullPointerException("Name is null");
    }
    if (this.snippets.isEmpty()) {
      return null;
    }

    SnippetIterator iterator = new SnippetIterator(this.snippets);
    // Iterate through each snippet in the current set using the custom iterator
    while (iterator.hasNext()) {
      Snippet s = iterator.next();
      // If the names match return the snippet
      if (s.getName().equalsIgnoreCase(name)) {
        return s;
      }
    }
    return null;
  }

  /**
   * Determines whether a snippet with a name already exists in the current snippet set
   * 
   * @param name
   *          The name to check
   * @return True if a snippet with the name already exits, otherwise false
   */
  private boolean isDuplicateName(String name) {
    if (this.snippets.isEmpty()) {
      return false;
    }
    SnippetIterator iterator = new SnippetIterator(this.snippets);
    // Iterate through each snippet in the current set using the custom iterator
    while (iterator.hasNext()) {
      Snippet s = iterator.next();
      // If the names match return true
      if (s.getName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Load snippets from an xml file at path
   * 
   * @param path
   *          The path to the file of snippets
   * @throws ParserConfigurationException
   *           If there was an error parsing the xml file
   * @throws IOException
   *           If the was an error reading the file
   * @throws SAXException
   *           If there was an error parsing the xml file
   */
  public void loadFromFile(String path) throws ParserConfigurationException, SAXException, IOException {
    this.snippets = new LinkedHashMap<Language, SortedSet<Snippet>>();
    File file = new File(path);

    // Create an xml document from the file
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(file);
    doc.getDocumentElement().normalize();

    // Get a list of every node in the document
    NodeList langs = doc.getDocumentElement().getChildNodes();
    for (int i = 0; i < langs.getLength(); i++) {
      Node n = langs.item(i);

      // If the node is an element node (corresponding to a language node in this case)
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        Element e = (Element) n;
        Language lang = Language.valueOf(e.getNodeName());
        SortedSet<Snippet> snippets = new TreeSet<Snippet>();
        // Get each snippet node from the language node
        NodeList snippetNodes = e.getElementsByTagName("snippet");
        for (int j = 0; j < snippetNodes.getLength(); j++) {
          // Parse the snippet element into a new snippet and add it to the set for that language
          snippets.add(Snippet.getFromXMLElement((Element) snippetNodes.item(j), lang));
        }
        // If there is a snippet for the current language, add it to the overall result
        if (snippets.size() > 0) {
          this.snippets.put(lang, snippets);
        }
      }
    }

    // Notify that observers that new snippets have been loaded
    this.notifyObservers(ChangeType.Load);
    System.out.println("In snippet manager data model: Loading snippets from file at: " + path);
  }

  /**
   * Notify all snippet set listeners that a change has occurred to the set
   * 
   * @param type
   *          The type of change that has occurred to the snippet set
   * @see uk.co.ryanharrison.snippetmanager.SnippetSetObservable#notifyObservers()
   */
  @Override
  public void notifyObservers(ChangeType type) {
    for (SnippetSetChangedListener observer : this.listeners) {
      observer.onSnippetSetChanged(this, type);
    }
  }

  /**
   * Remove a listener from the current set of observers
   * 
   * @param o
   *          The listener to remove
   * 
   * @see uk.co.ryanharrison.snippetmanager.SnippetSetObservable#removeSnippetSetChangedListener(uk.co.ryanharrison.snippetmanager.SnippetSetChangedListener)
   */
  @Override
  public void removeSnippetSetChangedListener(SnippetSetChangedListener o) {
    this.listeners.remove(o);
  }

  /**
   * Save all snippets to an xml file at path
   * 
   * @param path
   *          The path to the file to save to
   * @throws ParserConfigurationException
   *           If there was an error making the xml document
   * @throws TransformerException
   *           If there was an error creating the xml document
   */
  public void saveSnippetsToFile(String path) throws ParserConfigurationException, TransformerException {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

    // Create the root element
    Document doc = docBuilder.newDocument();
    Element rootElement = doc.createElement("snippets");
    doc.appendChild(rootElement);

    // Loop through each entry in the map
    for (Entry<Language, SortedSet<Snippet>> entry : this.snippets.entrySet()) {
      // Create a new xml element from the language key from each entry
      Element lang = doc.createElement(entry.getKey().name());
      // For every snippet in the language group, add an xml element representing the snippet to the language xml element
      for (Snippet s : entry.getValue()) {
        lang.appendChild(s.toXMLElement(doc));
      }
      rootElement.appendChild(lang);
    }

    // Write the content into the xml file
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    // Enable indentation
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(new File(path));

    transformer.transform(source, result);

    // Notify that observers that the snippets have been saved
    this.notifyObservers(ChangeType.Save);
    System.out.println("In snippet manager data model: Saving snippets to file at: " + path);
  }

  /**
   * Update an existing snippet to a new version
   * 
   * @param old
   *          The old snippet to update
   * @param newSnippet
   *          The new snippet to replace the old one with
   * @throws NullPointerException
   *           If either snippet is null
   */
  public void updateSnippet(Snippet old, Snippet newSnippet) {
    if (old == null) {
      throw new NullPointerException("Snippet to update is null");
    }
    if (newSnippet == null) {
      throw new NullPointerException("Snippet to update to is null");
    }
    this.updateSnippetHelper(old, newSnippet);

    // Notify all observers that a snippet has been updated
    this.notifyObservers(ChangeType.Update);
  }

  /**
   * Update the data of a snippet with that held in another
   * 
   * @param old
   *          The snippet to update the data of
   * @param newSnippet
   *          The snippet to update the data to
   * @throws NullPointerException
   *           If either snippet is null
   */
  public void updateSnippetData(Snippet old, Snippet newSnippet) {
    if (old == null) {
      throw new NullPointerException("Snippet to update is null");
    }
    if (newSnippet == null) {
      throw new NullPointerException("Snippet to update to is null");
    }
    this.updateSnippetHelper(old, newSnippet);
  }

  /**
   * Helper method to update an old snippet with a newer version
   * 
   * @param old
   *          The snippet to update
   * @param newSnippet
   *          The new snippet to replace the old one with
   */
  private void updateSnippetHelper(Snippet old, Snippet newSnippet) {
    // Only update if the old snippet exists in the set
    // This prevents the new snippet from being added when the old one isn't present
    if (this.getSnippetFromName(old.getName()) != null) {
      // Delete the old snippet
      this.deleteSnippetHelper(old);
      try {
        // Add the new one again
        this.addSnippetHelper(newSnippet);
      }
      catch (DuplicateSnippetException e) {
        e.printStackTrace();
      }
    }
  }
}
