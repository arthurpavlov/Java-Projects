package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import driver.HTMLToStringConverter;
import extracter.FrontPageCoauthorsExtracter;

public class FrontPageCoauthorsExtracterTest {
  private String file1string;
  private ArrayList<String> correctauthorslist =
      new ArrayList(Arrays.asList("Egon Willighagen", "Jonathan Alvarsson",
          "Christoph Steinbeck", "Nina Jeliazkova", "Rajarshi Guha",
          "Sam Adams", "Janna Hastings", "Samuel Lampa", "Valentin Georgiev",
          "Adam Ameur", "Komorowski Jan", "gilleain torrance",
          "Antony John Williams", "Noel M. O'Boyle", "Sean Ekins"));
  private FrontPageCoauthorsExtracter coauthex;
  private ArrayList<String> tesoutput;

  /**
   * Set up an instance of FrontPageCoauthorsExtracter that runs on
   * sample1.html.
   */
  @Before
  public void setUp() {
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    if (file1.checkHTML()) {
      file1string = file1.getString();
    }
    coauthex = new FrontPageCoauthorsExtracter(file1string);
  }

  /**
   * Test that the extract method returns the correct list of coauthors from 
   * the front page.
   */
  @Test
  public void testExtract() {
    tesoutput = coauthex.extract();
    assertEquals(tesoutput, correctauthorslist);
  }
}
