package test;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.CopyFileCommand;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link CopyFileCommand} class
 */
public class CopyFileCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private Filesystem filesystem;
  private WorkingDirectoryHandler fileHandler;
  private CopyFileCommand command;

  private CommandData getData(String... params) {
    return new CommandData("cp", Arrays.asList(params), out, err,
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
    filesystem.getFile("/").addFile("file1");
    fileHandler = new WorkingDirectoryHandler(filesystem);
    fileHandler.getFile("/file1").append("test string");
    command = new CopyFileCommand(fileHandler);
  }

  /**
   * Ensure that "cp " fails, there must be two arguments.
   */
  @Test
  public void testEmptyInput() {
    assertFalse(command.execute(getData())); //invalid input
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that copying a file which doesn't exist causes an error.
   */
  @Test
  public void testOldPathDNE() {
    assertTrue(command.execute(getData("file2", "dir1")));
    assertFalse(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that copying a valid file to a directory doesn't cause an error
   */
  @Test
  public void testFileCopyToDirectory() {
    assertTrue(command.execute(getData("file1", "dir1")));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertNotNull(fileHandler.getFile("/dir1/file1"));
    assertEquals("test string",
                 fileHandler.getFile("/dir1/file1").read().get(0));
  }

  /**
   * Ensure that copying a valid file to a directory doesn't cause an error
   */
  @Test
  public void testFileCopyToFile() {
    assertTrue(command.execute(getData("file1", "/dir1/file2")));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertNotNull(fileHandler.getFile("/dir1/file2"));
    assertEquals("test string",
                 fileHandler.getFile("/dir1/file2").read().get(0));
  }
}
