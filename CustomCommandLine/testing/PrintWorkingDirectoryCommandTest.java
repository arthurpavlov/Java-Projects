package test;

<<<<<<< .mine
import static org.junit.Assert.*;



import java.util.Arrays;

=======
>>>>>>> .r238
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import command.Command;
import command.CommandData;
import command.PrintWorkingDirectoryCommand;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for the {@link PrintWorkingDirectoryCommand}
 */
public class PrintWorkingDirectoryCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private WorkingDirectoryHandler wdh;
  private Command command;

  private CommandData getData(String... params) {
    return new CommandData("pwd", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * Create an empty filesystem and initialize new output and error buffers,
   * and then create a command which uses these.
   */
  @Before
  public void setUp() {

    out = new OutputLocationCollect();
    err = new OutputLocationCollect();

    wdh = new WorkingDirectoryHandler(new Filesystem());

    command = new PrintWorkingDirectoryCommand(wdh);
  }

  /**
   * Ensure command works properly with no parameters
   */
  @Test
  public void testEmpty() {
    assertTrue(command.execute(getData()));
    assertEquals(1, out.getBuffer().size());
    assertEquals("/", out.getBuffer().get(0));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
  }

  /**
   * Ensure command errors properly with parameters
   */
  @Test
  public void testMultiInput() {
    assertFalse(command.execute(getData("one", "two", "three")));
    assertTrue(out.getBuffer().toString(), out.getBuffer().isEmpty());
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());

  }

}
