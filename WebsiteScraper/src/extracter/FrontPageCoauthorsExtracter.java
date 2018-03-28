package extracter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Represents a front page coauthors extracter.
 */
public class FrontPageCoauthorsExtracter implements Extracter {
  private String extractionfile;
  
  /**
   * @param htmlfile    a string representation of an HTML file from which the
   *                    extraction needs to be made
   */ 
  public FrontPageCoauthorsExtracter(String htmlfile){
    extractionfile = htmlfile;
  }
  
  /** Extracts the front page coauthors from extractionfile.
   * 
   * @return an array list of strings representing the front page coauthors
   */  
  public ArrayList<String> extract() {
    ArrayList<String> list = new ArrayList<String>();
    String reCoautherCountExtraction = "l=en\" title=\"(.*?)\">";
    Pattern patternObject = Pattern.compile(reCoautherCountExtraction);
    Matcher matcherObject = patternObject.matcher(extractionfile);
    while (matcherObject.find()){
      list.add(StringEscapeUtils.unescapeHtml3(matcherObject.group(1)));
    }
    return list;
  }
}
