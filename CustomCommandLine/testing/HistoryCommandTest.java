package test;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.HistoryCommand;
import driver.JShell;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;
import outputlocation.OutputLocationNop;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link HistoryCommand} class
 */
public class HistoryCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private JShell shell;
  private HistoryCommand command;

  private CommandData getData(String... params) {
    return new CommandData("history", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * Set up a JShell and an exit command for that shell.
   */
  @Before
  public void setUp() {
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();
    StringReader input = new StringReader("echo hello\r\n"
                                          + "cat dir\r\n"
                                          + "invalid   with some spaces\r\n"
                                          + "\r\n");
    shell =
        new JShell(new WorkingDirectoryHandler(new Filesystem()), input,
                   new OutputLocationNop(), new OutputLocationNop());
    //discard shell output
    shell.registerDefaultCommands(); //register sane defaults
    shell.run(); //populate history
    command = new HistoryCommand(shell);
  }

  /**
   * Ensure that retrieving all history is handled correctly
   */
  @Test
  public void testEmptyInput() {
    assertTrue(command.execute(getData())); //successfully ran
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertArrayEquals(
        new String[]{"1. echo hello",
                     "2. cat dir",
                     "3. invalid   with some spaces"},
        out.getBuffer().toArray(new String[0]));
  }

  /**
   * Ensure that retrieving a trimmed history is handled correctly
   */
  @Test
  public void testNumericInput() {
    assertTrue(command.execute(getData("2"))); //successfully ran
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertArrayEquals(
        new String[]{"2. cat dir",
                     "3. invalid   with some spaces"},
        out.getBuffer().toArray(new String[0]));
  }

  /**
   * Ensure that giving a number to trim larger than the command history just
   * returns the full command history.
   */
  @Test
  public void testHigherNumericInput() {
    assertTrue(command.execute(getData("100"))); //successfully ran
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertArrayEquals(
        new String[]{"1. echo hello",
                     "2. cat dir",
                     "3. invalid   with some spaces"},
        out.getBuffer().toArray(new String[0]));
  }


  /**
   * Ensure that providing non-numeric input is handled correctly
   */
  @Test
  public void testNonNumericInput() {
    //known error case, print message to output
    assertTrue(command.execute(getData("notanumber")));
    //there was an error message printed
    assertFalse(err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }


  /**
   * Ensure that giving an invalid parameter count is handled correctly
   */
  @Test
  public void testInvalidParameterCount() {
    //invalid parameters case, return false
    assertFalse(command.execute(getData("1", "2", "3")));
    //nothing was printed
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }
}
