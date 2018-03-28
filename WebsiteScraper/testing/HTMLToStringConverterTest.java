package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import driver.HTMLToStringConverter;

public class HTMLToStringConverterTest {
  private String validfilename = "sample1.html";
  private HTMLToStringConverter validconverter;
  private String invalidfilename = "sample3.html";
  private HTMLToStringConverter invalidconverter;

  /**
   * Set up two instances of HTMLToStringConverter. One that runs on a 
   * valid input and one that runs on an invalid input.
   */
  @Before
  public void setUp() {
    validconverter =
        new HTMLToStringConverter(validfilename);
    invalidconverter =
        new HTMLToStringConverter(invalidfilename);
  }
  
  /**
   * Assert that checkHTML returns true if it is run on a valid input.
   */
  @Test
  public void testCheckHTMLValidInput() {
    assertTrue(validconverter.checkHTML());
  }

  /**
   * Assert that checkHTML returns false if it is run on an invalid input.
   */
  @Test
  public void testCheckHTMLInvalidInput() {
    assertFalse(invalidconverter.checkHTML());
  }

  /**
   * Assert that if getString is run on a valid input, the htmlstring attribute
   * will be overwritten by the string representation of the HTML file.
   */
  @Test
  public void testGetStringValidInput() {
    validconverter.checkHTML();
    String rstring = validconverter.getString();
    assertTrue(rstring != "");
  }
  
  /**
   * Assert that if getString is run on an invalid input, the htmlstring 
   * attribute will not be overwritten and will remain the empty string "".
   */
  @Test
  public void testGetStringInvalidInput() {
    invalidconverter.checkHTML();
    String rstring = validconverter.getString();
    assertTrue(rstring == "");
  }
}
