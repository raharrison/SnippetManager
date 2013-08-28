package uk.co.ryanharrison.snippetmanager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class representing a Snippet with a name and content. This is the data backbone of the application
 * 
 * @author Ryan Harrison
 */
public class Snippet implements Comparable<Snippet> {

  /** The name of the snippet */
  private String      name;

  /** The snippet itself */
  private String      snippet;

  /** A short description about the snippet */
  private String      description;

  /** A set of keywords that can be used to search for the snippet */
  private Set<String> keywords;

  /** The language the snippet is written in */
  private Language    language;

  /**
   * Construct a new snippet. The name field cannot be null or empty. The keywords cannot be null.
   * 
   * @param name
   *          The name
   * @param snippet
   *          The content
   * @param description
   *          The description
   * @param keywords
   *          A set of keywords used to describe the snippet
   * @param language
   *          The language of the snippet
   * @throws NullPointerException
   *           If the name or the keywords is null
   */
  public Snippet(String name, String snippet, String description, Set<String> keywords, Language language) {
    super();

    if (name == null) {
      throw new NullPointerException("Name cannot be null");
    }

    if (name.isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }

    if (keywords == null) {
      throw new NullPointerException("Keywords cannot be null");
    }

    this.name = name;
    this.snippet = snippet;
    this.description = description;
    this.keywords = keywords;
    this.language = language;
  }

  /**
   * Override the comparison method to compare the names of the to snippets instead of the whole object
   * 
   * @param o
   *          The other snippet to compare
   * @return A lexical comparison of the names of the snippets
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Snippet o) {
    return this.name.compareTo(o.name);
  }

  /**
   * Get the description of the snippet
   * 
   * @return The description of the snippet
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Get the keywords of the snippet
   * 
   * @return The keywords of the snippet
   */
  public Set<String> getKeywords() {
    return this.keywords;
  }

  /**
   * Get the language of the snippet
   * 
   * @return The language of the snippet
   */
  public Language getLanguage() {
    return this.language;
  }

  /**
   * Get the name of the snippet
   * 
   * @return The name of the snippet
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get the snippet content
   * 
   * @return The snippet content
   */
  public String getSnippet() {
    return this.snippet;
  }

  /**
   * Provide a toString method to make sure Snippets are displayed in a short human readable format in gui elements
   * 
   * @return A short human readable form of the snippet
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getName();
  }

  /**
   * Convert this snippet to an xml element with tags for each field of this snippet
   * 
   * @param doc
   *          The document to create the element from
   * @return An xml element representing the current snippet
   */
  public Element toXMLElement(Document doc) {
    // Create the snippet tag element
    Element snippet = doc.createElement("snippet");

    // Add a name tag and append it to the snippet tag
    Element name = doc.createElement("name");
    name.appendChild(doc.createTextNode(this.name));
    snippet.appendChild(name);

    // Add a data tag and append it to the snippet tag
    Element data = doc.createElement("data");
    data.appendChild(doc.createTextNode(this.snippet.trim()));
    snippet.appendChild(data);

    // Add a description tag and append it to the snippet tag
    Element description = doc.createElement("description");
    description.appendChild(doc.createTextNode(this.description));
    snippet.appendChild(description);

    // Add a keywords tag and append it to the snippet tag
    Element keywords = doc.createElement("keywords");
    keywords.appendChild(doc.createTextNode(this.keywords.toString().replace("[", "").replace("]", "")));
    snippet.appendChild(keywords);

    return snippet;
  }

  /**
   * Create a snippet from an xml element
   * 
   * @param item
   *          The xml element tag to parse and create the snippet from
   * @param lang
   *          The language of this snippet
   * @return A new snippet from the values held in the xml element item
   */
  public static Snippet getFromXMLElement(Element item, Language lang) {
    // Get the name, data and description from their individual tags
    String name = item.getElementsByTagName("name").item(0).getTextContent();
    String data = item.getElementsByTagName("data").item(0).getTextContent().trim();
    String description = item.getElementsByTagName("description").item(0).getTextContent();

    // The keywords are comma separated. Split them and add them to a set
    String[] keywordsString = item.getElementsByTagName("keywords").item(0).getTextContent().split(",");
    for (int i = 0; i < keywordsString.length; i++) {
      keywordsString[i] = keywordsString[i].trim().toLowerCase();
    }
    Set<String> keywords = new HashSet<String>(Arrays.asList(keywordsString));
    // Return this snippet from the data collected from the xml element
    return new Snippet(name, data, description, keywords, lang);
  }
}
