package test;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import driver.HTMLToStringConverter;
import extracter.FrontPageCoauthorCountExtracter;

public class FrontPageCoauthorCountExtracterTest {
  private String file1string;
  private ArrayList<String> correctcoauthcount =
      new ArrayList(Arrays.asList("15"));
  private FrontPageCoauthorCountExtracter coauthcountex;
  private ArrayList<String> testoutput;

  /**
   * Set up an instance of FrontPageCoauthorCountExtracter that runs on
   * sample1.html.
   */
  @Before
  public void setUp(){
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    if (file1.checkHTML()) {
      file1string = file1.getString();
    }
    coauthcountex = new FrontPageCoauthorCountExtracter(file1string);
  }

  /**
   * Test that extract returns the correct coauthor count from the front page.
   */
  @Test
  public void testExtract() {
    testoutput = coauthcountex.extract();
    assertEquals(testoutput, correctcoauthcount);
  }
}
