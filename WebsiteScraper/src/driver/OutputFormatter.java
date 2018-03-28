package driver;

import java.util.ArrayList;

/**
 * This class represents a formatting unit that uses a FileSpecificationsSystem
 * to create a formatted output string.
 */
public class OutputFormatter {
  private ArrayList<ArrayList<ArrayList<String>>> filecollection;
  private String totalcoauthorcount;
  private ArrayList<String> sortedcoauthorlist;

  /**
   * @param fss an instance of FileSpecificationsSystem
   */
  public OutputFormatter(FileSpecificationsSystem fss) {
    filecollection = fss.getFileCollection();
    totalcoauthorcount = fss.totalCoauthorCount();
    sortedcoauthorlist = fss.getAlphabeticallySortedCoauthorsList();
  }

  /**
   * Creates and returns a properly formatted output string.
   * 
   * @return a properly formatted output string
   */
  public String getFormattedString() {
    String rstring = "";
    // add the formatted specifications of each file to rstring
    for (ArrayList<ArrayList<String>> file : filecollection) {
      rstring = rstring
          + "------------------------------------------------------------\n";
      rstring = rstring + "1. Name of Author:\n";
      rstring = rstring + "\t" + file.get(0).get(0) + "\n";
      rstring = rstring + "2. Number of All Citations:\n";
      rstring = rstring + "\t" + file.get(1).get(0) + "\n";
      rstring = rstring + "3. Number of i10-index after 2009:\n";
      rstring = rstring + "\t" + file.get(2).get(0) + "\n";
      rstring = rstring + "4. Title of the first 3 publications:\n";
      Integer titlecount = 1;
      for (String title : file.get(3)) {
        rstring = rstring + "\t" + titlecount.toString() + "-  " + title + "\n";
        titlecount++;
      }
      rstring = rstring + "5. Total paper citation (first 5 papers):\n";
      rstring = rstring + "\t" + file.get(4).get(0) + "\n";
      rstring = rstring + "6. Total Co-Authors:\n";
      rstring = rstring + "\t" + file.get(5).get(0) + "\n";
    }
    rstring = rstring
        + "\n------------------------------------------------------------\n";
    rstring = rstring + "7. Co-Author list sorted (Total: " + totalcoauthorcount
        + "):\n";
    for (String coauthor : sortedcoauthorlist) {
      rstring = rstring + coauthor + "\n";
    }
    // remove the extra \n from the back of the string
    rstring = rstring.substring(0, rstring.length() - 1);
    return rstring;
  }
}
