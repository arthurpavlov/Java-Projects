package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import driver.FileSpecificationsSystem;
import driver.HTMLToStringConverter;
import driver.MyParser;

public class FileSpecificationsSystemTest {
  private String file1string;
  private String file2string;
  private FileSpecificationsSystem fss;
  private String correctcoauthorcount = "29";
  private ArrayList<String> sortedcoauthorlist =
      new ArrayList(Arrays.asList("Abigail Sellen", "Adam Ameur",
          "Andrew D Miller", "Antony John Williams", "Blair MacIntyre",
          "Christoph Steinbeck", "Deepak Jagdish", "E.D. Mynatt",
          "Egon Willighagen", "Elsa Eiriksdottir", "Erika Shehan Poole",
          "Greg Turk", "Iulian Radu", "Janna Hastings", "John Stasko",
          "Jonathan Alvarsson", "Komorowski Jan", "Kurt Luther",
          "Nina Jeliazkova", "Noel M. O'Boyle", "Rajarshi Guha", "Sam Adams",
          "Samuel Lampa", "Sean Ekins", "Thore Graepel", "Valentin Georgiev",
          "Xiang Cao", "Youn-ah Kang", "gilleain torrance"));
  
  /**
   * Set up an instance of FileSpecificationsSystem to which the specifications
   * of sample1.html and sample2.html were added.
   */
  @Before
  public void setUp() {
    fss = new FileSpecificationsSystem();
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
  }

  /**
   * Test that totalCoauthorCount method returns the correct summation of
   * coauthors in the fss instance.
   */
  @Test
  public void testtotalCoauthorCount() {
    assertEquals(fss.totalCoauthorCount(), correctcoauthorcount);
  }

  /**
   * Test that getAlphabeticallySortedCoauthorsList method returns a properly
   * sorted list.
   */
  @Test
  public void testgetAlphabeticallySortedCoauthorsList() {
    assertEquals(fss.getAlphabeticallySortedCoauthorsList(),
        sortedcoauthorlist);
  }
}
