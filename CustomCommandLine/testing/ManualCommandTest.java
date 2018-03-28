package test;

import org.junit.Before;
import org.junit.Test;


import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

import command.Command;
import command.CommandData;
import command.ManualCommand;
import driver.JShell;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains unit tests for the {@link ManualCommand}
 */
public class ManualCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private JShell shell;
  private Command command;

  private CommandData getData(String... params) {
    return new CommandData("man", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * set up a bare manual command with a "mock" JShell, only to be used to
   * retrieve the default command's manuals.
   */
  @Before
  public void setUp() {

    out = new OutputLocationCollect();
    err = new OutputLocationCollect();

    shell = new JShell(new WorkingDirectoryHandler(new Filesystem()),
                       new InputStreamReader(
                           new ByteArrayInputStream(new byte[0])), out, err);

    shell.registerDefaultCommands(); // register sane defaults
    command = new ManualCommand(shell);
  }

  /**
   * Assert that the command fails with not enough parameters
   */
  @Test
  public void testNoParameters() {
    assertFalse(command.execute(getData()));
  }

  /**
   * Assert that the command fails with too many parameters
   */
  @Test
  public void testTooManyParameters() {
    assertFalse(command.execute(getData("One", "Two", "Three")));
  }

  /**
   * Assert that the command prints an error when given an invalid parameter
   */
  @Test
  public void testInvalidParameter() {
    assertTrue(command.execute(getData("NotACommand")));
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
    assertFalse(err.getBuffer().isEmpty());
  }

  /**
   * Assert that the command doesn't error when given a valid parameter
   */
  @Test
  public void testValidParameter() {
    assertTrue(command.execute(getData("ls")));
    assertFalse(out.getBuffer().isEmpty());
    assertArrayEquals(shell.getSimpleCommand("ls").getHelpReference(),
                      out.getBuffer().toArray(new String[0]));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
  }


}
