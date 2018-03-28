package test;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import command.Command;
import command.CommandData;
import command.WebGetCommand;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Contains tests for the {@link WebGetCommand}
 */
public class WebGetCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private Filesystem fs;
  private WorkingDirectoryHandler wdh;
  private Command command;

  private CommandData getData(String... params) {
    return new CommandData("pwd", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * Set up a filesystem with a file and a directory before each test.
   */
  @Before
  public void setUp() {
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();

    fs = new Filesystem();
    fs.getFile("/").addDirectory("dir1");
    fs.getFile("/").addFile("file1");
    wdh = new WorkingDirectoryHandler(fs);
    command = new WebGetCommand(wdh);
  }

  /**
   * Tests how the command reacts with not enough parameters
   */
  @Test
  public void testParamTooShort() {
    assertFalse(command.execute(getData()));
    assertEquals(0, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
  }

  /**
   * Tests how the command reacts with too many parameters
   */
  @Test
  public void testParamTooLong() {
    assertFalse(command.execute(getData("a", "b", "c")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
  }

  /**
   * Tests how the command reacts to invalid filenames both in the url and in
   * the alternate filename
   */
  @Test
  public void testFilenameValidation() {
    assertTrue(command.execute(
        getData("http://www.google.com/robots.txt", "invalid filename!!!")));
    assertNull(fs.getFile("invalid filename!!!"));
    assertEquals(0, out.getBuffer().size());
    assertEquals(1, err.getBuffer().size());
    assertEquals("The output filename is invalid", err.getBuffer().get(0));

    assertTrue(command.execute(getData("http://www.google.com/xml?")));
    assertNull(fs.getFile("invalid filename!!!"));
    assertEquals(0, out.getBuffer().size());
    assertEquals(2, err.getBuffer().size());
    assertEquals("The output filename is invalid", err.getBuffer().get(1));
  }

  /**
   * Test how the command reacts to the file already existing.
   */
  @Test
  public void testFileAlreadyExists() {
    assertTrue(
        command.execute(getData("http://www.google.com/robots.txt", "file1")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
  }

  /**
   * Test how the command reacts to a directory that doesn't exist.
   */
  @Test
  public void testNonexistantDirectory() {
    assertTrue(command
                   .execute(getData("http://www.google.com/robots.txt",
                                    "dir3/file1")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(1, err.getBuffer().size());
  }

  /**
   * Test that the command successfully uses alternate filenames.
   */
  @Test
  public void testAlternateFilename() {
    assertTrue(command.execute(
        getData("http://www.google.com/robots.txt", "alternate_filename")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
    assertNotNull(fs.getFile("/alternate_filename"));
  }

  /**
   * Test each valid protocol
   */
  @Test
  public void testValidProtocols() {
    assertTrue(command
                   .execute(getData("http://www.google.com/robots.txt",
                                    "http_test")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
    assertNotNull(fs.getFile("/http_test"));

    assertTrue(command
                   .execute(getData("https://www.google.com/robots.txt",
                                    "https_test")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
    assertNotNull(fs.getFile("/https_test"));
  }

  /**
   * Test how the command reacts to invalid protocols.
   */
  @Test
  public void testInvalidProtocols() {
    assertTrue(
        command.execute(getData("ftp://www.notAsite.com/nope", "ftp_test")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(1, err.getBuffer().size());
  }

  /**
   * Tests how the command handles Malformed URLs.
   */
  @Test
  public void testMalformedURL() {
    assertTrue(command.execute(getData("This isn't a URL", "malformed_url")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(1, err.getBuffer().size());
  }

  /**
   * Test how the command handles connection errors.
   */
  @Test
  public void testConnectionErrors() {
    assertTrue(command.execute(
        getData("http://www.aviaygdasogibhdajuiag.com/file.txt", "file")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(1, err.getBuffer().size());
  }
}
