package uk.co.ryanharrison.snippetmanager;

import java.util.regex.Pattern;

/**
 * Enumeration specifying all supported languages of snippets. Each language has a String value used to display the language in the
 * user interface along with a regular expression pattern that can be used to highlight keywords.
 * 
 * @author Ryan Harrison
 */
public enum Language {
  Java(
      "Java",
      "abstract|continue|for|new|switch|default|package|null|synchronized|boolean|do|if|private|this|throws|break|double|implements|protected|throw|else|import|public|case|instanceof|return|catch|extends|int|try|char|final|interface|static|void|class|finally|long|float|super|while"), CSharp(
      "C#",
      "abstract|event|new|struct|as|null|switch|base|this|bool|false|operator|throw|break|finally|out|true|override|try|case|catch|for|private|foreach|protected|public|class|if|readonly|const|ref|continue|in|return|using|int|virtual|default|interface|sealed|delegate|internal|void|do|is|while|double|else|static|namespace|string"), PHP(
      "PHP",
      "abstract|and|array|as|break|case|catch|class|const|continue|declare|default|die|do|echo|else|elseif|enddeclare|endfor|endforeach|endif|endswitch|endwhile|extends|final|for|foreach|function|global|if|implements|include|interface|new|or|private|protected|public|require|return|static|switch|throw|try|var|while"), CPlusPlus(
      "Pascal",
      "and|array|as|begin|case|class|const|constructor|destructor|do|downto|else|end|except|file|finally|for|function|if|implementation|in|inherited|interface|is|mod|not|object|of|on|or|procedure|program|property|raise|record|repeat|set|then|to|try|type|unit|until|uses|var|while|with|xor"), Python(
      "Python",
      "and|as|assert|break|class|continue|def|del|elif|else|except|exec|finally|for|from|global|if|import|in|is|lambda|not|or|pass|print|raise|return|try|while|with|yield"), Javascript(
      "Javascript",
      "break|case|catch|continue|debugger|default|delete|do|else|finally|for|function|if|in|instanceof|new|return|switch|this|throw|try|typeof|var|void|while|with"), PlainText(
      "Plain Text", "");

  /** A human readable form of the language */
  private String  value;

  /** A regular expression that can be used to match the keywords of the language */
  private String  pattern;

  /** A regular expression pattern that can be used to match the language's keywords */
  private Pattern regex;

  /**
   * Construct a new language with specified value and regular expression pattern
   * 
   * @param value
   *          The human readable form of the language
   * @param pattern
   *          The regular expression of the keywords of the language
   */
  private Language(String value, String pattern) {
    this.value = value;
    this.pattern = pattern;
    this.regex = Pattern.compile(pattern, Pattern.UNIX_LINES);
  }

  /**
   * Get the regular expression of the keywords of the language
   * 
   * @return The regular expression of the keywords of the language
   */
  public String getPattern() {
    return this.pattern;
  }

  /**
   * Get the regular expression pattern for this language
   * 
   * @return The regular expression pattern for this language
   */
  public Pattern getRegex() {
    return this.regex;
  }

  /**
   * Get a SyntaxHighlighter object associated with the current language. The highlighter can be used to highlight the keywords of
   * this language
   * 
   * @return A new SyntaxHighlighter object associated with the current language
   */
  public SyntaxHighlighter getSyntaxHighlighter() {
    return new SyntaxHighlighter(this);
  }

  /**
   * Get the human readable version of the language
   * 
   * @return The human readable version of the language
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Provide a new implementation of the toString method to make sure that the language is displayed in a human readable form in gui
   * elements
   * 
   * @return A human readable form of the current language
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.value;
  }
}
