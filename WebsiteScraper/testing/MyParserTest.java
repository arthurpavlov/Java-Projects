package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import driver.MyParser;
import driver.HTMLToStringConverter;

import org.junit.Before;
import org.junit.Test;

public class MyParserTest {
  private String file1string;
  private String file2string;
  private ArrayList<ArrayList<String>> file1validspecs =
      new ArrayList(Arrays.asList(Arrays.asList("Ola Spjuth"),
          Arrays.asList("437"), Arrays.asList("12"),
          Arrays.asList(
              "Bioclipse: an open source workbench for chemo-and bioinforma"
                  + "tics",
              "The LCB data warehouse",
              "XMPP for cloud computing in bioinformatics supporting discovery "
                  + "and invocation of asynchronous web services"),
          Arrays.asList("239"), Arrays.asList("15"),
          Arrays.asList("Egon Willighagen", "Jonathan Alvarsson",
              "Christoph Steinbeck", "Nina Jeliazkova", "Rajarshi Guha",
              "Sam Adams", "Janna Hastings", "Samuel Lampa",
              "Valentin Georgiev", "Adam Ameur", "Komorowski Jan",
              "gilleain torrance", "Antony John Williams", "Noel M. O'Boyle",
              "Sean Ekins")));
  private ArrayList<ArrayList<String>> file2validspecs =
      new ArrayList(Arrays.asList(Arrays.asList("Yan Xu"), Arrays.asList("263"),
          Arrays.asList("9"),
          Arrays.asList(
              "Face-tracking as an augmented input in video games: enhancing"
              + " presence, role-playing and control",
              "Art of defense: a collaborative handheld augmented reality board"
              + " game",
              "Sociable killers: understanding social relationships in an "
              + "online first-person shooter game"),
          Arrays.asList("158"), Arrays.asList("14"),
          Arrays.asList("Blair MacIntyre", "E.D. Mynatt", "Erika Shehan Poole",
              "Andrew D Miller", "Elsa Eiriksdottir", "Iulian Radu",
              "Abigail Sellen", "Xiang Cao", "Thore Graepel", "John Stasko",
              "Youn-ah Kang", "Kurt Luther", "Deepak Jagdish", "Greg Turk")));

  /**
   * Set up two converted files, sample1.html and sample2.html.
   */
  @Before
  public void setUp() {
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    HTMLToStringConverter file2 = new HTMLToStringConverter("sample2.html");
    if (file1.checkHTML() && file2.checkHTML()) {
      file1string = file1.getString();
      file2string = file2.getString();
    }
  }

  /**
   * Assert the if the process method of MyParser is run on a valid htmlstring,
   * file1string, it will return the correct arraylist of file specifications
   */
  @Test
  public void testProcessFileOne() {
    ArrayList<ArrayList<String>> file1specs = MyParser.process(file1string);
    assertEquals(file1specs, file1validspecs);
  }

  /**
   * Assert the if the process method of MyParser is run on a valid htmlstring,
   * file2string, it will return the correct arraylist of file specifications
   */
  @Test
  public void testProcessFileTwo() {
    ArrayList<ArrayList<String>> file2specs = MyParser.process(file2string);
    assertEquals(file2specs, file2validspecs);
  }

}
