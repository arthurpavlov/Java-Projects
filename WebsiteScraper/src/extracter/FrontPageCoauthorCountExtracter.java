package extracter;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a front page coauthor count extracter.
 */
public class FrontPageCoauthorCountExtracter implements Extracter {
    private String extractionfile;
    
    /**
     * @param htmlfile    a string representation of an HTML file from which the
     *                    extraction needs to be made
     */ 
    public FrontPageCoauthorCountExtracter(String htmlfile){
      extractionfile = htmlfile;
    }
    
    /** Extracts the front page coauthor count from extractionfile.
     * 
     * @return an array list of strings representing the front page coauthor
     *         count
     */
    public ArrayList<String> extract() {
      ArrayList<String> list = new ArrayList<String>();
      String reCoautherCountExtraction = "l=en\" title=\"(.*?)\">";
      Pattern patternObject = Pattern.compile(reCoautherCountExtraction);
      Matcher matcherObject = patternObject.matcher(extractionfile);
      Integer rauthorcount = 0;
      while (matcherObject.find()) {
        rauthorcount = rauthorcount + 1;
      }
      list.add(rauthorcount.toString());
      return list;
    }
}
