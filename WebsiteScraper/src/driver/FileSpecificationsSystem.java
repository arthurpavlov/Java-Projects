package driver;

import java.util.ArrayList;


import java.util.Collections;

/**
 * This class represents collection system for the specifications of multiple
 * files and provides functionality for specification collection over multiple
 * files.
 */
public class FileSpecificationsSystem {
  private  ArrayList<ArrayList<ArrayList<String>>> filecollection =
      new ArrayList<ArrayList<ArrayList<String>>>();

  /** Adds the unique file specifications of a file to the filecollection.
   * 
   * @param filespecs    the required raw specifications for a specific file
   */
  public void addFile(ArrayList<ArrayList<String>> filespecs) {
    filecollection.add(filespecs);
  }

  /** Calculates the sum of all coauthors in filecollection.
   * 
   * @return the String representation of the sum of all coauthors in 
   *         filecollection
   */
  public String totalCoauthorCount() {
    Integer rcount = 0;
    // look through every file in filecollection
    for (ArrayList<ArrayList<String>> file : filecollection) {
      // find the coauthor count for the file
      String filecoauthorcount = file.get(5).get(0);
      // add it to the total count
      rcount = rcount + Integer.parseInt(filecoauthorcount);
    }
    return rcount.toString();
  }

  /** Creates and returns an alphabetically sorted arraylist of strings
   * representing all the coauthors found in filecollection.
   * 
   * @return an alphabetically sorted arraylist of strings
   */
  public ArrayList<String> getAlphabeticallySortedCoauthorsList() {
    ArrayList<String> authors = getAllAuthors();
    Collections.sort(authors);
    return authors;
  }

  /** Creates and returns an arraylist of strings representing all the 
   * coauthors found in filecollection.
   * 
   * Note: this is private helper method that should only be use by the
   * getAlphabeticallySortedCoauthorsList method.
   * 
   * @return an arraylist of strings
   */
  private  ArrayList<String> getAllAuthors() {
    ArrayList<String> allcoauthors = new ArrayList<String>();
    //look through every file in filecollection
    for (ArrayList<ArrayList<String>> file : filecollection) {
      //access the list of coauthors for the file
      ArrayList<String> filecoauthors = file.get(6);
      // add each coauthor to the list of allcoauthors
      for (String author : filecoauthors) {
        allcoauthors.add(author);
      }
    }
    return allcoauthors;
  }
  
  /** Get the filecollection.
   * 
   * @return an arraylist of arraylist of arraylists of strings representing
   *         the file collection
   */
  public ArrayList<ArrayList<ArrayList<String>>> getFileCollection() {
    return filecollection;
  }


}
