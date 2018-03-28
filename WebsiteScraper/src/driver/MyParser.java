//**********************************************************
//Assignment3:




//CDF user_name: c5pavlov
//
//Author: Arturs Pavlovs
//
//
//Honor Code: I pledge that this program represents my own
//program code and that I have coded on my own. I received
//help from no one in designing and debugging my program.
//*********************************************************
package driver;

import java.util.ArrayList;


import extracter.AuthorNameExtracter;
import extracter.CitationCountExtracter;
import extracter.Extracter;
import extracter.FirstThreePublicationTitlesExtracter;
import extracter.FirstFivePublicationCitationCountExtracter;
import extracter.FrontPageCoauthorCountExtracter;
import extracter.PostTwoKNineITenIndexCountExtracter;
import extracter.FrontPageCoauthorsExtracter;

import driver.HTMLToStringConverter;
import driver.FileSpecificationsSystem;
import driver.OutputFormatter;

import outputlocation.OutputLocation;
import outputlocation.OutputLocationConsole;
import outputlocation.OutputLocationFile;

/**
 * This class represents the main raw argument parsing unit.
 */
public class MyParser {

public static void main(String[] args){
    String inputFiles[] = args[0].split(","); 
    FileSpecificationsSystem fss = new FileSpecificationsSystem();
    //look through each input HTML file
    for (String htmlfile: inputFiles){
      HTMLToStringConverter converter = new HTMLToStringConverter(htmlfile);
      //check if HTML file is valid
      if (!converter.checkHTML()){
        System.out.println("malformed URL or cannot open connection to "
       + htmlfile);
      }
      //convert HTML file into a string
      String htmlstring = converter.getString();
      //extract all the needed raw specifications from the HTML string
      ArrayList<ArrayList<String>> filespecs = process(htmlstring);
      //add the unique specifications to FileSpecificationsSystem instance
      fss.addFile(filespecs);
    }
    OutputFormatter of = new OutputFormatter(fss);
    //get the final formatted string 
    String rstring = of.getFormattedString();
    OutputLocation ol;
    //if output file is given
    try{
     String outputfilename = args[1];
     ol = new OutputLocationFile(rstring, outputfilename);
    }
    //if output file is not given case
    catch(Exception e){
      ol = new OutputLocationConsole(rstring);
    }
    ol.write();  
}

/** Retrieves the raw specifications for a given HTML string representation.
 * 
 * @param htmlstring    the strings representation of a given HTML file
 * @return the list of lists of strings with all the needed raw specifications
 *         from htmlstring
 */
public static ArrayList<ArrayList<String>> process(String htmlstring){
  //create a list of extracters
  ArrayList<Extracter> extracters = new ArrayList<Extracter>();
  //add all extracters to the list
  extracters.add(new AuthorNameExtracter(htmlstring));
  extracters.add(new CitationCountExtracter(htmlstring));
  extracters.add(new PostTwoKNineITenIndexCountExtracter(htmlstring));
  extracters.add(new FirstThreePublicationTitlesExtracter(htmlstring));
  extracters.add(new FirstFivePublicationCitationCountExtracter(htmlstring));
  extracters.add(new FrontPageCoauthorCountExtracter(htmlstring));
  extracters.add(new FrontPageCoauthorsExtracter(htmlstring));
  //create an filespec list
  ArrayList<ArrayList<String>> filespecs = new ArrayList<>();
  //add all the extracted specification to filespec
  for (Extracter spec: extracters){
    filespecs.add(spec.extract());
  }
  return filespecs;
}
}


