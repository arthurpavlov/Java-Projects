package test;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.ExitCommand;
import driver.JShell;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link ExitCommand} class
 */
public class ExitCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private JShell shell;
  private ExitCommand command;
  private boolean exited = false;

  private CommandData getData(String... params) {
    return new CommandData("exit", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * Set up a JShell and an exit command for that shell.
   */
  @Before
  public void setUp() {
    //this monstrosity is because we haven't learned mocking yet, and
    //interfacing out an exit method seems excessive.
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();
    shell =
        new JShell(new WorkingDirectoryHandler(new Filesystem()),
                   new InputStreamReader(new ByteArrayInputStream(new byte[0])),
                   out, err) {
          @Override
          public void exit() {
            exited = true;
          }
        };
    command = new ExitCommand(shell);
  }

  /**
   * Ensure that writing empty output is handled correctly
   */
  @Test
  public void testEmptyInput() {
    command.execute(getData());
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
    assertTrue(exited);
  }

  /**
   * Ensure that writing multiple outputs is handled correctly.
   * The exit command should just discard any arguments passed to it.
   */
  @Test
  public void testMultipleInput() {
    command.execute(getData("one two three"));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertEquals(out.getBuffer().size(), 0);
    assertEquals(err.getBuffer().size(), 0);
    assertTrue(exited);
  }
}
