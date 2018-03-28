package test;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.MoveFileCommand;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link MoveFileCommand} class
 */
public class MoveFileCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private Filesystem fs;
  private WorkingDirectoryHandler wdh;
  private MoveFileCommand moveCommand;

  private CommandData getData(String... params) {
    return new CommandData("mv", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * Set up MoveFileCommand for testing.
   */
  @Before
  public void setUp() {
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();
    fs = new Filesystem();
    fs.getFile("/").addDirectory("dir1");
    fs.getFile("/").addFile("file1");
    fs.getFile("/").addFile("file2");
    wdh = new WorkingDirectoryHandler(fs);
    moveCommand = new MoveFileCommand(wdh);
  }

  /**
   * Ensure that if the relative oldPath doesn't lead to a valid file, an error
   * message is printed and nothing in the file system is altered.
   */
  @Test
  public void testOldPathFileMissingRelative() {
    moveCommand.execute(getData("file3", "dir1"));
    //check that error message gets printed
    assertTrue(!err.getBuffer().isEmpty());
    //check that nothing is altered in the file system
    assertEquals(wdh.getFile("/").getDirectoryContents().size(), 3);
    assertEquals(wdh.getFile("/dir1/").getDirectoryContents().size(), 0);
  }

  /**
   * Ensure that if the full oldPath doesn't lead to a valid file, an error
   * message is printed and nothing in the file system is altered.
   */
  @Test
  public void testOldPathFileMissingFull() {
    moveCommand.execute(getData("/file3", "dir1"));
    //check that error message gets printed
    assertTrue(!err.getBuffer().isEmpty());
    //check that nothing is altered in the file system
    assertEquals(wdh.getFile("/").getDirectoryContents().size(), 3);
    assertEquals(wdh.getFile("/dir1").getDirectoryContents().size(), 0);
  }

  /**
   * Ensure that if the file at newPath doesn't exist and doesn't have a valid
   * parent, an error message is printed and nothing in the file system is
   * altered.
   */
  @Test
  public void testNewPathMissingParentFull() {
    moveCommand.execute(getData("file1", "/dir2/file2"));
    //check that error message gets printed
    assertTrue(!err.getBuffer().isEmpty());
    //check that nothing is altered in the file system
    assertEquals(wdh.getFile("/").getDirectoryContents().size(), 3);
    assertEquals(wdh.getFile("/dir1").getDirectoryContents().size(), 0);
  }

  /**
   * Ensure that if a newPath exists in oldPath's sub-directories, an error
   * message is printed and nothing in the file system is altered.
   */
  @Test
  public void testMoveFileIntoSubdirectory() {
    moveCommand.execute(getData("/", "/dir1"));
    //check that error message gets printed
    assertTrue(!err.getBuffer().isEmpty());
    //check that nothing is altered in the file system
    assertEquals(wdh.getFile("/").getDirectoryContents().size(), 3);
    assertEquals(wdh.getFile("/dir1").getDirectoryContents().size(), 0);
  }

  /**
   * Ensure that if the file at newPath doesn't exist, but its parent is a
   * valid directory, the file from oldPath is moved into the newPath's
   * parent directory.
   */
  @Test
  public void testMoveWithValidParentDirectory() {
    moveCommand.execute(getData("/file1", "/dir1/file1"));
    //check that no error messages get printed
    assertTrue(err.getBuffer().isEmpty());
    //check that file gets removed from original directory
    assertTrue(!wdh.getFile("/").getDirectoryContents().containsKey("file1"));
    //check that file gets moved into the new directory
    assertTrue(
        wdh.getFile("/dir1").getDirectoryContents().containsKey("file1"));
  }

  /**
   * Ensure that if the file at newPath doesn't exist, but its parent is a
   * valid directory, the file from oldPath is moved into the newPath's
   * parent directory and changes the name in respect to newPath.
   */
  @Test
  public void testMoveWithValidParentDirectoryRename() {
    moveCommand.execute(getData("/file1", "/dir1/file5"));
    //check that no error messages get printed
    assertTrue(err.getBuffer().isEmpty());
    //check that file gets removed from original directory
    assertTrue(!wdh.getFile("/").getDirectoryContents().containsKey("file1"));
    //check that file gets moved into the new directory
    assertTrue(
        wdh.getFile("/dir1").getDirectoryContents().containsKey("file5"));
  }

  /**
   * Ensure that if newPath is a valid directory, file at oldPath gets moved
   * into it.
   */
  @Test
  public void testMoveIntoValidDirectory() {
    moveCommand.execute(getData("/file1", "/dir1"));
    //check that no error messages get printed
    assertTrue(err.getBuffer().isEmpty());
    //check that file gets removed from original directory
    assertTrue(!wdh.getFile("/").getDirectoryContents().containsKey("file1"));
    //check that file gets moved into the new directory
    assertTrue(
        wdh.getFile("/dir1").getDirectoryContents().containsKey("file1"));
  }

  /**
   * Ensure that MoveFileCommand provides functionality for file overriding.
   */
  @Test
  public void testOverrideNewPathFile() {
    moveCommand.execute(getData("/file1", "/file2"));
    //check that no error messages get printed
    assertTrue(err.getBuffer().isEmpty());
    //check that file1 exists in the directory
    assertTrue(wdh.getFile("/").getDirectoryContents().containsKey("file2"));
    //check that file2 gets overridden by file1
    assertTrue(!wdh.getFile("/").getDirectoryContents().containsKey("file1"));
    //check that the size of directory decreased by 1
    assertEquals(wdh.getFile("/").getDirectoryContents().size(), 2);
  }
}
