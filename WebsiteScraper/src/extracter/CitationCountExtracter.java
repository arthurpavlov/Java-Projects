package extracter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a citation count extracter.
 */
public class CitationCountExtracter implements Extracter{
  private String extractionfile;
  
  /**
   * @param htmlfile    a string representation of an HTML file from which the
   *                    extraction needs to be made
   */  
  public CitationCountExtracter(String htmlfile){
    extractionfile = htmlfile;
  }
  
  /** Extracts the citation count from extractionfile.
   * 
   * @return an array list of strings representing the citation count
   */
  public ArrayList<String> extract() {
    ArrayList<String> list = new ArrayList<String>();
    String reCitationCountExtraction = "<td class=\"cit-borderleft cit-data\">"
        + "(.*?)</td>";
    Pattern patternObject = Pattern.compile(reCitationCountExtraction);
    Matcher matcherObject = patternObject.matcher(extractionfile);
    if (matcherObject.find()) {
      list.add(matcherObject.group(1));
    }
    return list;
  }
}
