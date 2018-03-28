package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import driver.HTMLToStringConverter;
import extracter.CitationCountExtracter;

public class CitationCountExtracterTest {
  private String file1string;
  private ArrayList<String> correctcitationcount =
      new ArrayList(Arrays.asList("437"));
  private CitationCountExtracter citex;
  private ArrayList<String> testoutput;

  /**
   * Set up an instance of CitationCountExtracter that runs  on sample1.html.
   */
  @Before
  public void setUp(){
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    if (file1.checkHTML()) {
      file1string = file1.getString();
    }
    citex = new CitationCountExtracter(file1string);
  }

  /**
   * Test that the extract method returns the correct citation count.
   */
  @Test
  public void testExtract() {
    testoutput = citex.extract();
    assertEquals(testoutput, correctcitationcount);
  }

}
