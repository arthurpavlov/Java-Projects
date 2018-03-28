package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import driver.FileSpecificationsSystem;
import driver.HTMLToStringConverter;
import driver.MyParser;
import driver.OutputFormatter;

public class OutputFormatterTest {
  private String file1string;
  private String file2string;
  private FileSpecificationsSystem fss = new FileSpecificationsSystem();
  private OutputFormatter op;
  private String teststring;
  private String correctformattedrstring =
      "------------------------------------------------------------\n"
          + "1. Name of Author:\n" + "\t" + "Ola Spjuth" + "\n"
          + "2. Number of All Citations:\n" + "\t" + "437" + "\n"
          + "3. Number of i10-index after 2009:\n" + "\t" + "12" + "\n"
          + "4. Title of the first 3 publications:\n" + "\t"
          + "1-  Bioclipse: an open source workbench for chemo-and "
          + "bioinformatics"
          + "\n" + "\t" + "2-  The LCB data warehouse" + "\n" + "\t"
          + "3-  XMPP for cloud computing in bioinformatics supporting"
          + " discovery"
          + " and invocation of asynchronous web services"
          + "\n" + "5. Total paper citation (first 5 papers):\n" + "\t" + "239"
          + "\n" + "6. Total Co-Authors:\n" + "\t" + "15" + "\n"
          + "------------------------------------------------------------\n"
          + "1. Name of Author:\n" + "\t" + "Yan Xu" + "\n"
          + "2. Number of All Citations:\n" + "\t" + "263" + "\n"
          + "3. Number of i10-index after 2009:\n" + "\t" + "9" + "\n"
          + "4. Title of the first 3 publications:\n" + "\t"
          + "1-  Face-tracking as an augmented input in video games: enhancing"
          + " presence, role-playing and control"
          + "\n" + "\t"
          + "2-  Art of defense: a collaborative handheld augmented reality"
          + " board game"
          + "\n" + "\t"
          + "3-  Sociable killers: understanding social relationships in an"
          + " online first-person shooter game"
          + "\n" + "5. Total paper citation (first 5 papers):\n" + "\t" + "158"
          + "\n" + "6. Total Co-Authors:\n" + "\t" + "14" + "\n" + "\n"
          + "------------------------------------------------------------\n"
          + "7. Co-Author list sorted (Total: 29):" + "\n" + "Abigail Sellen"
          + "\n" + "Adam Ameur" + "\n" + "Andrew D Miller" + "\n"
          + "Antony John Williams" + "\n" + "Blair MacIntyre" + "\n"
          + "Christoph Steinbeck" + "\n" + "Deepak Jagdish" + "\n"
          + "E.D. Mynatt" + "\n" + "Egon Willighagen" + "\n"
          + "Elsa Eiriksdottir" + "\n" + "Erika Shehan Poole" + "\n"
          + "Greg Turk" + "\n" + "Iulian Radu" + "\n" + "Janna Hastings" + "\n"
          + "John Stasko" + "\n" + "Jonathan Alvarsson" + "\n"
          + "Komorowski Jan" + "\n" + "Kurt Luther" + "\n" + "Nina Jeliazkova"
          + "\n" + "Noel M. O'Boyle" + "\n" + "Rajarshi Guha" + "\n"
          + "Sam Adams" + "\n" + "Samuel Lampa" + "\n" + "Sean Ekins" + "\n"
          + "Thore Graepel" + "\n" + "Valentin Georgiev" + "\n" + "Xiang Cao"
          + "\n" + "Youn-ah Kang" + "\n" + "gilleain torrance";

  /**
   * Set up an instance of OutputFormatter that is instansiated with a 
   * FileSpecificationsSystem instance containing specs of files sample1.html
   * and sample2.html. Then create a test string by running getFormattedString
   * method of the OutPutFormatter instance.
   */
  @Before
  public void setUp() {
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    HTMLToStringConverter file2 = new HTMLToStringConverter("sample2.html");
    if (file1.checkHTML() && file2.checkHTML()) {
      file1string = file1.getString();
      file2string = file2.getString();
    }
    ArrayList<ArrayList<String>> file1specs = MyParser.process(file1string);
    fss.addFile(file1specs);
    ArrayList<ArrayList<String>> file2specs = MyParser.process(file2string);
    fss.addFile(file2specs);
    op = new OutputFormatter(fss);
    teststring = op.getFormattedString();
  }

  /**
   * Test that getFormattedString method returns the correct formatted string
   */
  @Test
  public void testgetFormattedString() {
     assertEquals(teststring, correctformattedrstring);
  }
}
