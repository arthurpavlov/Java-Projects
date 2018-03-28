package test;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.ListFileContentsCommand;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link ListFileContentsCommand} class
 */
public class ListFileContentsTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private Filesystem filesystem;
  private WorkingDirectoryHandler fileHandler;
  private ListFileContentsCommand command;

  private CommandData getData(String... params) {
    return new CommandData("cat", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
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
    filesystem.getFile("/dir1").addFile("file1");
    filesystem.getFile("/dir1/file1").write(Arrays.asList("1", "2", "data"));
    filesystem.getFile("/dir1").addFile("file2");
    filesystem.getFile("/dir1/file2").write(Arrays.asList("3", "4", "data"));
    filesystem.getFile("/dir2").addFile("file3");
    filesystem.getFile("/dir2/file3").write(Arrays.asList("5", "6", "data"));
    filesystem.getFile("/dir3").addFile("file4");
    filesystem.getFile("/dir3/file4").write(Arrays.asList("7", "8", "data"));
    fileHandler = new WorkingDirectoryHandler(filesystem);
    fileHandler.setCurrentDir("dir1");
    command = new ListFileContentsCommand(fileHandler);
  }

  /**
   * Ensure that listing files in the current directory is handled correctly
   */
  @Test
  public void testEmptyInput() {
    assertFalse(command.execute(getData())); //invalid input
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that listing the contents of a directory fails.
   */
  @Test
  public void testListDirectoryContents() {
    assertTrue(command.execute(getData("../dir2", "../dir3")));
    assertFalse(err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that listing the contents of files works and are separated
   * by three line breaks as per spec.
   */
  @Test
  public void testListFileContents() {
    assertTrue(command.execute(getData("../dir2/file3", "../dir3/file4")));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertEquals(Arrays.asList("5", "6", "data", "", "", "", "7", "8", "data"),
                 out.getBuffer());
  }
}
