package uk.co.ryanharrison.snippetmanager;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * A custom file chooser that only lets users pick XML files
 * 
 * @author Ryan Harrison
 */
public class XMLFileChooser extends JFileChooser {

  /** Serialisation identifier */
  private static final long serialVersionUID = -2581469599043395177L;

  /**
   * Create a new xml file chooser
   */
  public XMLFileChooser() {
    super();
    // Create the filter that only shows xml files
    this.setFileFilter(new FileFilter() {

      // Determines which files are accepted from this file chooser
      @Override
      public boolean accept(File f) {
        // We can accept directories (folders)
        if (f.isDirectory()) {
          return true;
        }
        // Get the extension
        String extension = this.getExtension(f);
        if (extension != null) {
          // Make sure the extension is xml
          return extension.equalsIgnoreCase("xml");
        }
        return false;
      }

      // Get a description of what files this chooser accepts
      @Override
      public String getDescription() {
        return "XML files";
      }

      /*
       * Get the extension of a file.
       */
      public String getExtension(File f) {
        String s = f.getName();
        int i = s.lastIndexOf('.');

        // If the index of the dot is inside the bounds of the file name
        if (i > 0 && i < s.length() - 1) {
          // Extract the extension
          return s.substring(i + 1);
        }
        return null;
      }
    });
  }
}
