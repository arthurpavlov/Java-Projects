package extracter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a first five publication citation count extracter.
 */
public class FirstFivePublicationCitationCountExtracter implements Extracter {
  private String extractionfile;

  /**
   * @param htmlfile    a string representation of an HTML file from which the
   *                    extraction needs to be made
   */
  public FirstFivePublicationCitationCountExtracter(String htmlfile) {
    extractionfile = htmlfile;
  }

  /**
   * Extracts the first five publication citation count from extractionfile.
   * 
   * @return an array list of strings representing the first five publication
   *         citation count
   */
  public ArrayList<String> extract() {
    ArrayList<String> list = new ArrayList<String>();
    String reForFirst5CitCountExtraction =
        "[0-9].[0-9][0-9][0-9][0-9]\">(.*?)</a>";
    Pattern patternObject = Pattern.compile(reForFirst5CitCountExtraction);
    Matcher matcherObject = patternObject.matcher(extractionfile);
    int count = 0;
    Integer rnumber = 0;
    while (matcherObject.find() && count != 5) {
      rnumber = rnumber + Integer.parseInt(matcherObject.group(1));
      count++;
    }
    list.add(rnumber.toString());
    return list;
  }
}
