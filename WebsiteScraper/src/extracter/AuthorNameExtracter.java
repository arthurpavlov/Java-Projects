package extracter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an author name extracter.
 */
public class AuthorNameExtracter implements Extracter {
  private String extractionfile;
  
  /**
   * @param htmlfile    a string representation of an HTML file from which the
   *                    extraction needs to be made
   */
  public AuthorNameExtracter(String htmlfile){
    extractionfile = htmlfile;
  }
  
  /** Extracts the author name from extractionfile.
   * 
   * @return an array list of strings representing the author name
   */
  public ArrayList<String> extract() {
    ArrayList<String> list = new ArrayList<String>();
    String reForAuthorExtraction =
          "<span id=\"cit-name-display\" "
              + "class=\"cit-in-place-nohover\">(.*?)</span>";
    Pattern patternObject = Pattern.compile(reForAuthorExtraction);
    Matcher matcherObject = patternObject.matcher(extractionfile);
    while (matcherObject.find()) {
      list.add(matcherObject.group(1));
    }
    return list;
  }
}
