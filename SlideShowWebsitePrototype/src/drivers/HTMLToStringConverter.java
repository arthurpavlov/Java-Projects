package drivers;


import java.io.BufferedReader;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * This class represents a converting unit that takes in a HTML file and
 * converts it into a String.
 */
public class HTMLToStringConverter {
  private String htmlstring = "";
  private String rawhtml;

  /**
   * @param htmlfile the input HTML file
   */
  public HTMLToStringConverter(String htmlfile) {
    rawhtml = htmlfile;
  }

  /**
   * Checks whether the input HTML file is valid.
   * 
   * @return true or false depending on the validity of rawhtml
   */
  public boolean checkHTML() {
    try {
      // create object to store html source text as it is being collected
      StringBuilder html = new StringBuilder();
      // open connection to given html file
      URL url = new File(rawhtml).toURI().toURL();
      // create BufferedReader to buffer the given url's HTML source
      BufferedReader htmlbr =
          new BufferedReader(new InputStreamReader(url.openStream()));
      String line;
      // read each line of HTML code and store in StringBuilder
      while ((line = htmlbr.readLine()) != null) {
        html.append(line);
      }
      htmlbr.close();
      // convert StringBuilder into a String and return it
      htmlstring = html.toString();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Get the converted HTML file. Note: This method should be called only after
   * the HTML file has been checked.
   * 
   * @return the string representation of converted HTML file.
   */
  public String getString() {
    return htmlstring;
  }
}
