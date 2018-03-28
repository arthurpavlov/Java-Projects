package test;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import command.Command;
import command.CommandData;
import command.SearchCommand;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Contains tests for the {@link SearchCommand}
 */
public class SearchCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private Filesystem fs;
  private WorkingDirectoryHandler wdh;
  private Command command;

  private final static Set<String> RECURSIVE_FLAG =
      Collections.singleton("r");

  private CommandData getData(boolean recursive, String... params) {
    return new CommandData("pwd", Arrays.asList(params), out, err,
                           recursive ? RECURSIVE_FLAG
                                     : Collections.<String>emptySet());
  }

  /**
   * Create a simple filesystem with two files with mock data, and a
   * directory before each test.
   */
  @Before
  public void setUp() {
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();

    fs = new Filesystem();
    wdh = new WorkingDirectoryHandler(fs);
    command = new SearchCommand(wdh);
    fs.getFile("/").addFile("file1");
    fs.getFile("/file1").write(
        Arrays.asList("one fish", "two fish", "red fish", "blue fish"));
    fs.getFile("/").addDirectory("dir1");
    fs.getFile("/dir1").addFile("file2");
    fs.getFile("/dir1/file2").write(
        Arrays.asList("ROYGBIV", "Red", "Orange", "Yellow", "Green",
                      "Blue", "Indigo", "Violet"));
  }

  /**
   * Test how the command reacts to not having enough parameters
   */
  @Test
  public void testNotEnoughParams() {
    assertFalse(command.execute(getData(false)));
  }

  /**
   * Test how the command handles invalid expressions
   */
  @Test
  public void testInvalidExpression() {
    assertTrue(command.execute(getData(false, "\\", "/")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(1, err.getBuffer().size());
    assertTrue(err.getBuffer().get(0).contains("Pattern failed to compile"));
  }

  /**
   * Test to make sure the command is case sensitive
   */
  @Test
  public void testCaseSensitive() {
    assertTrue(command.execute(getData(true, "R", "/")));
    assertEquals(3, out.getBuffer().size());
    assertTrue(out.getBuffer().get(0).contains("/dir1/file2"));
    assertEquals(0, err.getBuffer().size());
  }

  /**
   * Test to make sure the recursive flag functions
   */
  public void testRecursiveFlag() {
    assertTrue(command.execute(getData(false, "R", "/")));
    assertEquals(0, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());

    assertTrue(command.execute(getData(true, "R", "/")));
    assertEquals(3, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
    assertTrue(out.getBuffer().get(0).contains("/dir1/file2"));
  }

  /**
   * Test to make sure the command works with multiple parameters
   */
  @Test
  public void testMultipleParameters() {
    assertTrue(command.execute(getData(false, "red", "/", "/", "/dir1")));
    assertEquals(4, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
  }

  /**
   * Test to make sure the command handles non-catching groups
   */
  @Test
  public void testNonCatchingGroups() {
    assertTrue(command.execute(getData(true, "(?:.*?)ed", "/")));
    assertEquals(4, out.getBuffer().size());
    assertEquals(0, err.getBuffer().size());
  }
}
