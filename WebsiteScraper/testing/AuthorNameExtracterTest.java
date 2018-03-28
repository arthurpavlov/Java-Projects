package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import driver.HTMLToStringConverter;
import extracter.AuthorNameExtracter;

public class AuthorNameExtracterTest {
  private String file1string;
  private ArrayList<String> correctauthorname =
      new ArrayList(Arrays.asList("Ola Spjuth"));
  private AuthorNameExtracter aex;
  private ArrayList<String> testoutput;

  /**
   * Set up an instance of AuthorNameExtracter that runs  on sample1.html.
   */
  @Before
  public void setUp(){
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    if (file1.checkHTML()) {
      file1string = file1.getString();
    }
    aex = new AuthorNameExtracter(file1string);
  }

  /**
   * Test that the extract method returns the correct author name.
   */
  @Test
  public void testExtract() {
    testoutput = aex.extract();
    assertEquals(testoutput, correctauthorname);
  }
}
