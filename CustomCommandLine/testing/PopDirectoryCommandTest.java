package test;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.PopDirectoryCommand;
import driver.JShell;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link command.PushDirectoryCommand} class
 */
public class PopDirectoryCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private JShell shell;
  private Filesystem fs;
  private WorkingDirectoryHandler wdh;
  private PopDirectoryCommand popCommand;

  private CommandData getData(String... params) {
    return new CommandData("popd", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * Set up a JShell
   */
  @Before
  public void setUp() {
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();
    fs = new Filesystem();
    wdh = new WorkingDirectoryHandler(fs);
    shell = new JShell(wdh,
                       new InputStreamReader(
                           new ByteArrayInputStream(new byte[0])), out, err);
    shell.registerDefaultCommands();
    popCommand = new PopDirectoryCommand(shell, wdh);

    //set the default directory to contain two sub-directories
    fs.getFile("/").addDirectory("dir1");
    fs.getFile("/").addDirectory("dir2");
  }

  /**
   * Ensure that if a user attempts to execute on an empty directory stack, the
   * current working directory isn't changed.
   */
  @Test
  public void testEmptyStack() {
    popCommand.execute(getData());
    assertEquals(wdh.getCurrentDir(), "/");
  }

  /**
   * Ensure that if a user attempts to execute on a stack containing one
   * directory it changes the current working directory to the directory that
   * was just popped.
   */
  @Test
  public void testOneDirStack() {
    assertEquals(wdh.getCurrentDir(), "/");
    shell.pushDirectory("/dir1");
    popCommand.execute(getData());
    assertEquals(wdh.getCurrentDir(), "/dir1/");
  }

  /**
   * Ensure that if a user attempts to execute on a stack containing more than
   * one directory, it changes the current working directory to the directory
   * that was just popped, while the stack still contains the rest
   * of the directories.
   */
  @Test
  public void testMultiDirStack() {
    assertEquals(wdh.getCurrentDir(), "/");
    shell.pushDirectory("/dir1");
    shell.pushDirectory("/dir2");
    popCommand.execute(getData());
    assertEquals(wdh.getCurrentDir(), "/dir2/");
    popCommand.execute(getData());
    assertEquals("/dir1/", wdh.getCurrentDir());
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
  }
}
