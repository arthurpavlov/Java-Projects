package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import driver.HTMLToStringConverter;
import extracter.FirstThreePublicationTitlesExtracter;

public class FirstThreePublicationTitlesExtracterTest {
  private String file1string;
  private ArrayList<String> correcttitles = new ArrayList(Arrays.asList(
      "Bioclipse: an open source workbench for chemo-and bioinformatics",
      "The LCB data warehouse",
      "XMPP for cloud computing in bioinformatics supporting discovery and"
      + " invocation of asynchronous web services"));

  private FirstThreePublicationTitlesExtracter titleex;
  private ArrayList<String> testoutput;

  /**
   * Set up an instance of FirstThreePublicationTitlesExtracter that runs on
   * sample1.html.
   */
  @Before
  public void setUp() {
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    if (file1.checkHTML()) {
      file1string = file1.getString();
    }
    titleex = new FirstThreePublicationTitlesExtracter(file1string);
  }

  /**
   * Test that the extract method returns the correct set of publication titles.
   */
  @Test
  public void testExtract() {
    testoutput = titleex.extract();
    assertEquals(testoutput, correcttitles);
  }
}
