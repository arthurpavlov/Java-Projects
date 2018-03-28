package test;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.MakeDirectoryCommand;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link MakeDirectoryCommand} class
 */
public class MakeDirectoryCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private Filesystem filesystem;
  private WorkingDirectoryHandler fileHandler;
  private MakeDirectoryCommand command;

  private CommandData getData(String... params) {
    return new CommandData("mkdir", Arrays.asList(params), out, err,
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
    fileHandler.setCurrentDir("dir1");
    command = new MakeDirectoryCommand(fileHandler);
  }

  /**
   * Ensure that making a directory with no name is handled correctly
   */
  @Test
  public void testEmptyInput() {
    assertFalse(command.execute(getData())); //invalid input
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that overwriting directories and files fails.
   */
  @Test
  public void testOverwriteDirectories() {
    assertTrue(command.execute(getData("../dir1", "../file1")));
    assertFalse(err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that making multiple directories works.
   */
  @Test
  public void testMakeDirectories() {
    assertTrue(command.execute(getData("../dir2/", "../dir2/dir3/")));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
    assertNotNull(filesystem.getFile("/dir2/dir3"));
    assertTrue(filesystem.getFile("/dir2/dir3").isDirectory());
  }
}
