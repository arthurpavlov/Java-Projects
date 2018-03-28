package extracter;

import java.util.ArrayList;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a post two thousand nine i ten index count extracter.
 */
public class PostTwoKNineITenIndexCountExtracter implements Extracter {
  private String extractionfile;
  
  /**
   * @param htmlfile    a string representation of an HTML file from which the
   *                    extraction needs to be made
   */
  public PostTwoKNineITenIndexCountExtracter(String htmlfile){
    extractionfile = htmlfile;
  }
  
  /** Extracts the post two thousand nine i ten index count from extractionfile.
   * 
   * @return an array list of strings representing the post two thousand nine i 
   *         ten index count
   */
  public ArrayList<String> extract() {
    ArrayList<String> list = new ArrayList<String>();
    String reI10IndexCountExtraction = "cit-data\">(.*?)</td>";
    Pattern patternObject = Pattern.compile(reI10IndexCountExtraction);
    Matcher matcherObject = patternObject.matcher(extractionfile);
    Integer itemcount = 1;
    while (matcherObject.find()) {
      if (itemcount == 6){
        list.add(matcherObject.group(1));
      }
      itemcount++;
    }
    return list;
  }
}
