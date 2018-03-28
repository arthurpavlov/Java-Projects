package extracter;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a first three publication titles extracter.
 */
public class FirstThreePublicationTitlesExtracter implements Extracter {
    private String extractionfile;
    
    /**
     * @param htmlfile    a string representation of an HTML file from with the
     *                    extraction needs to be made
     */  
    public FirstThreePublicationTitlesExtracter(String htmlfile){
      extractionfile = htmlfile;
    }
    
    /** Extracts the first three publication titles from extractionfile.
     * 
     * @return an array list of strings representing the first three publication
     *         titles
     */
    public ArrayList<String> extract() {
      ArrayList<String> list = new ArrayList<String>();
      String reFirst3PubExtraction = "cit-dark-large-link\">(.*?)</a>";
      
      Pattern patternObject = Pattern.compile(reFirst3PubExtraction);
      Matcher matcherObject = patternObject.matcher(extractionfile);
      Integer itemcount = 0;
      while (matcherObject.find() && itemcount != 3) {
        list.add(matcherObject.group(1));
        itemcount++;
      }
      return list;
    }
}
