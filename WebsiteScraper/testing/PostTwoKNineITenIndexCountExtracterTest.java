package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import driver.HTMLToStringConverter;
import extracter.PostTwoKNineITenIndexCountExtracter;

public class PostTwoKNineITenIndexCountExtracterTest {
  private String file1string;
  private ArrayList<String> correctindexcount =
      new ArrayList(Arrays.asList("12"));
  private PostTwoKNineITenIndexCountExtracter indexex;
  private ArrayList<String> testoutput;

  /**
   * Set up an instance of PostTwoKNineITenIndexCountExtracter that runs  on 
   * sample1.html.

   */
  @Before
  public void setUp(){
    HTMLToStringConverter file1 = new HTMLToStringConverter("sample1.html");
    if (file1.checkHTML()) {
      file1string = file1.getString();
    }
    indexex = new PostTwoKNineITenIndexCountExtracter(file1string);
  }

  /**
   * Test that the extract methods returns the correct post 2009 index count.
   */
  @Test
  public void testExtract() {
    testoutput = indexex.extract();
    assertEquals(testoutput, correctindexcount);
  }

}
