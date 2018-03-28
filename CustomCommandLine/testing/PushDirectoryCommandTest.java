package test;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.MakeDirectoryCommand;
import command.PushDirectoryCommand;
import driver.JShell;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;
import outputlocation.OutputLocationNop;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link PushDirectoryCommand}
 */
public class PushDirectoryCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private Filesystem fs;
  private WorkingDirectoryHandler wdh;
  private JShell shell;
  private PushDirectoryCommand pushCommand;
  private MakeDirectoryCommand makedir;

  private CommandData getData(String... params) {
    return new CommandData("pushd", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * Initialize out and err as collect output locations, and create a simple
   * filesystem with a file and two directories.
   * Also initialize an empty shell to use for storing command history.
   */
  @Before
  public void setUp() {
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();
    fs = new Filesystem();
    //set the default directory to contain a file
    fs.getFile("/").addFile("file1");
    wdh = new WorkingDirectoryHandler(fs);
    makedir = new MakeDirectoryCommand(wdh);
    //set the default directory to contain two sub-directories
    fs.getFile("/").addDirectory("dir1");
    fs.getFile("/").addDirectory("dir2");

    shell = new JShell(wdh,
                       new InputStreamReader(
                           new ByteArrayInputStream(new byte[0])),
                       new OutputLocationNop(), new OutputLocationNop());
    pushCommand = new PushDirectoryCommand(shell, wdh);
  }

  /**
   * Assert that if execute runs on a valid directory, the default directory
   * will be pushed onto an empty stack, and the current directory will be
   * updated to the new directory.
   */
  @Test
  public void testEmptyStack() {
    assertEquals(shell.popDirectory(), null); // check if stack is empty
    assertEquals(wdh.getCurrentDir(), "/");
    pushCommand.execute(getData("dir1"));
    // check if root directory gets pushed onto stack
    assertEquals(shell.popDirectory(), "/");
    // check if current directory gets updated
    assertEquals(wdh.getCurrentDir(), "/dir1/");
  }

  /**
   * Assert if execute runs on a valid directory,the current directory will be
   * pushed onto a non-empty stack, and the current directory will be updated
   * to the new directory.
   */
  @Test
  public void testLoadedStack() {
    pushCommand.execute(getData("dir1"));
    makedir.execute(getData("dir3", "dir4")); //make two sub-directories
    pushCommand.execute(getData("dir3"));
    // check if dir1 gets pushed onto the stack
    assertEquals(shell.popDirectory(), "/dir1/");
    // check if current directory gets updated
    assertEquals(wdh.getCurrentDir(), "/dir1/dir3/");
  }

  /**
   * Assert that execute doesn't run on a non-existent directory.
   */
  @Test
  public void testNonExistingDirectory() {
    pushCommand.execute(getData("dir3"));
    assertEquals(shell.popDirectory(), null); // check if stack is empty
    // check if current directory is still default
    assertEquals(wdh.getCurrentDir(), "/");
  }

  /**
   * Assert that execute doesn't run on a non-directory file.
   */
  @Test
  public void testExecuteOnFile() {
    pushCommand.execute(getData("file1"));
    assertEquals(shell.popDirectory(), null); // check if stack is empty
    // check if current directory is still default
    assertEquals(wdh.getCurrentDir(), "/");
  }
}
