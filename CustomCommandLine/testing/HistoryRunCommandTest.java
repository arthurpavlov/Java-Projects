package test;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Collections;

import command.CommandData;
import command.HistoryRunCommand;
import driver.JShell;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;
import outputlocation.OutputLocationNop;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link command.HistoryRunCommand} class
 */
public class HistoryRunCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private JShell shell;
  private HistoryRunCommand command;

  private CommandData getData(String name) {
    return new CommandData(name, Collections.<String>emptyList(), out, err,
                           Collections.<String>emptySet());
  }


  /**
   * Set up a filesystem and working directory handler, then the command.
   */
  @Before
  public void setUp() {
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();
    StringReader input = new StringReader("echo hello\r\n" //1
                                          + "cat dir\r\n" //2
                                          + "invalid\r\n" //3
                                          + "!5\r\n"     //4
                                          + "!4\r\n");   //5 -- weird edge case
    shell =
        new JShell(new WorkingDirectoryHandler(new Filesystem()), input,
                   new OutputLocationNop(), new OutputLocationNop());
    //discard shell output
    shell.registerDefaultCommands(); //register sane defaults
    shell.run(); //populate history
    command = new HistoryRunCommand(shell);
  }

  /**
   * Ensure trying to run negative history indices fails.
   */
  @Test
  public void testNegativeHistory() {
    assertTrue(command.execute(getData("!-10"))); //arguments were fine
    //should be an error
    assertFalse(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //output from the command in the history
    //is handled by the shell, we only care about what the history run
    //command printed, which should always be nothing.
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that the only reasonable invalid lower bound, 0, fails.
   */
  @Test
  public void testInvalidLowerBound() {
    assertTrue(command.execute(getData("!0"))); //arguments were fine
    //should be an error
    assertFalse(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //output from the command in the history
    //is handled by the shell, we only care about what the history run
    //command printed, which should always be nothing.
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that unreasonably large upper bounds (larger than long.max_value)
   * don't work
   */
  @Test
  public void testInvalidExceptionalUpperBound() {
    //no exception should be thrown if we give an exceptionally large number
    assertTrue(command.execute(getData("!9" + (Long.MAX_VALUE))));
    //should be an error
    assertFalse(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //output from the command in the history
    //is handled by the shell, we only care about what the history run
    //command printed, which should always be nothing.
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }


  /**
   * Ensure that reasonable upper bounds, i.e ones that are just above
   * the size of the command history don't work
   */
  @Test
  public void testInvalidReasonableUpperBound() {
    //no exception should be thrown if we give an exceptionally large number
    assertTrue(command.execute(getData("!9")));
    //should be an error
    assertFalse(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //output from the command in the history
    //is handled by the shell, we only care about what the history run
    //command printed, which should always be nothing.
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that calling another history run from within a history run command
   * fails.  This is because, for example, "!1" as the first command would
   * be an infinite loop.
   */
  @Test
  public void testRecursiveHistoryRun() {
    //no exception should be thrown if we give an exceptionally large number
    assertTrue(command.execute(getData("!5")));
    //should be an error
    assertFalse(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //output from the command in the history, in this case "!4"
    //is handled by the shell, we only care about what the history run
    //command printed, which should always be nothing.
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }


  /**
   * Ensure that a valid command from the history is executed properly
   */
  @Test
  public void testValidCommandFromHistory() {
    //no exception should be thrown if we give an exceptionally large number
    assertTrue(command.execute(getData("!1")));
    //shouldn't be an error
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //output from the command in the history, in this case "echo hello"
    //is handled by the shell, we only care about what the history run
    //command printed, which should always be nothing.
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }

  /**
   * Ensure that an invalid command from the history is executed properly
   */
  @Test
  public void testInvalidCommandFromHistory() {
    //no exception should be thrown if we give an exceptionally large number
    assertTrue(command.execute(getData("!3")));
    //shouldn't be an error -- at least in history run
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    //output from the command in the history, in this case "echo hello"
    //is handled by the shell, we only care about what the history run
    //command printed, which should always be nothing.
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
  }
}
