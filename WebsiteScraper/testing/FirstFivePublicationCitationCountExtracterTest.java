package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import driver.HTMLToStringConverter;
import extracter.FirstFivePublicationCitationCountExtracter;

public class FirstFivePublicationCitationCountExtracterTest {
  private String file1string;
  private ArrayList<String> correctcitationcount =
      new ArrayList(Arrays.asList("239"));
  private FirstFivePublicationCitationCountExtracter citex;
  private ArrayList<String> testoutput;

  /**
   * Set up an instance of FirstFivePublicationCitationCountExtracter that runs
   * on sample1.html.
   */
  @Before
  public void setUp() {
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    if (file1.checkHTML()) {
      file1string = file1.getString();
    }
    citex = new FirstFivePublicationCitationCountExtracter(file1string);
  }

  /**
   * Test that the extract method returns the correct summation of citations
   * from the front page.
   */
  @Test
  public void testExtract() {
    testoutput = citex.extract();
    assertEquals(testoutput, correctcitationcount);
  }

}
