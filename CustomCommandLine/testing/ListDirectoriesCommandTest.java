package test;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import command.CommandData;
import command.ListDirectoriesCommand;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link ListDirectoriesCommand} class
 */
public class ListDirectoriesCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private Filesystem filesystem;
  private WorkingDirectoryHandler fileHandler;
  private ListDirectoriesCommand command;

  private CommandData getData(String... params) {
    return new CommandData("ls", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  private CommandData getData(boolean recursive, String... params) {
    if (!recursive) {
      return getData(params);
    }
    return new CommandData("ls", Arrays.asList(params), out, err,
                           new HashSet<>(Collections.singleton("r")));
  }


  /**
   * Set up a filesystem and working directory handler, then the command.
   */
  @Before
  public void setUp() {
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();
    filesystem = new Filesystem();
    filesystem.getFile("/").addDirectory("dir1");
    filesystem.getFile("/").addDirectory("dir2");
    filesystem.getFile("/").addDirectory("dir3");
    filesystem.getFile("/dir3").addDirectory("dir4");
    filesystem.getFile("/dir1").addFile("file1");
    filesystem.getFile("/dir1").addFile("file2");
    filesystem.getFile("/dir2").addFile("file3");
    filesystem.getFile("/dir3").addFile("file4");
    filesystem.getFile("/dir3/dir4").addFile("file5");
    fileHandler = new WorkingDirectoryHandler(filesystem);
    fileHandler.setCurrentDir("dir1");
    command = new ListDirectoriesCommand(fileHandler);
  }

  /**
   * Ensure that listing files in the current directory is handled correctly
   */
  @Test
  public void testEmptyInput() {
    assertTrue(command.execute(getData())); //successfully ran
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //we're currently in dir1, sets because ls doesn't have guaranteed output
    // order
    assertEquals(
        new HashSet<>(Arrays.asList("file1",
                                    "file2")),
        new HashSet<>(out.getBuffer()));
  }

  /**
   * Ensure that listing files in relative directories is handled correctly
   */
  @Test
  public void testRelativeInput() {
    assertTrue(command.execute(getData("../dir2", "../dir3")));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //we're currently in /dir1../dir2 or just /dir2
    assertEquals(
        Arrays.asList("../dir2:", "file3", "", "../dir3:", "dir4", "file4"),
        out.getBuffer());
  }

  /**
   * Ensure that listing files in relative directories is handled correctly
   */
  @Test
  public void testAbsoluteInput() {
    assertTrue(command.execute(getData("/dir2", "/dir3")));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertEquals(
        Arrays.asList("/dir2:", "file3", "", "/dir3:", "dir4", "file4"),
        out.getBuffer());
  }

  /**
   * Ensure that listing files in relative directories is handled correctly
   */
  @Test
  public void testRecursiveListing() {
    //recursive call
    assertTrue(command.execute(getData(true, "/dir2", "/dir3")));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertEquals(
        Arrays.asList("/dir2:", "file3", "", "/dir3:", "dir4:", "file5", "",
                      "file4"),
        out.getBuffer());
  }
}
