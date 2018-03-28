package test;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import command.Command;
import command.CommandData;
import driver.JShell;
import driver.WorkingDirectoryHandler;
import file.Filesystem;
import outputlocation.OutputLocationCollect;
import outputlocation.OutputLocationNop;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link JShell} class.
 */
public class JShellTest {

  private WorkingDirectoryHandler wdh;

  /**
   * Set up a new, empty filesystem every time.
   * The shell isn't initialized here, because its constructor is different
   * in almost every test.
   */
  @Before
  public void setUp() {
    wdh = new WorkingDirectoryHandler(new Filesystem());
  }

  /**
   * Ensure that command history is stored properly, ignoring whitespace.
   */
  @Test
  public void testCommandHistory() {
    JShell shell =
        new JShell(wdh, new StringReader("one\r\n \r\ntwo\r\n"),
                   new OutputLocationNop(), new OutputLocationNop());
    shell.run();
    //ignore whitespace
    assertEquals(Arrays.asList("one", "two"), shell.getCommandHistory());
  }

  /**
   * Ensure that the directory stack works in a LIFO method.
   */
  @Test
  public void testDirectoryStack() {
    JShell shell =
        new JShell(wdh, new StringReader("one\r\n \r\ntwo\r\n"),
                   new OutputLocationNop(), new OutputLocationNop());
    shell.pushDirectory("/dir1");
    shell.pushDirectory("/dir2");
    shell.pushDirectory("/dir3");
    //ignore whitespace
    assertEquals("/dir3", shell.popDirectory());
    assertEquals("/dir2", shell.popDirectory());
    assertEquals("/dir1", shell.popDirectory());
  }

  /**
   * Ensure that pushing a relative directory to the stack causes an exception
   */
  @Test(expected = IllegalArgumentException.class)
  public void testRelativeDirectoryStackException() {
    JShell shell =
        new JShell(wdh, new StringReader("one\r\n \r\ntwo\r\n"),
                   new OutputLocationNop(), new OutputLocationNop());
    shell.pushDirectory("dir1");
  }

  /**
   * Ensure that command execution happens properly.
   */
  @Test
  public void testCommandExecution() {
    OutputLocationCollect output = new OutputLocationCollect();
    JShell shell =
        new JShell(wdh, new StringReader("cmd1\r\n \r\ncmd2\r\n"),
                   output, new OutputLocationNop());
    shell.registerSimpleCommand("cmd1", new Command() {
      @Override
      public boolean execute(CommandData data) {
        data.getOutputLocation().writeln("cmd1");
        return true;
      }

      @Override
      public Set<String> knownFlags() {
        return Collections.emptySet();
      }

      @Override
      public String[] getHelpReference() {
        return new String[0];
      }
    });
    shell.registerSimpleCommand("cmd2", new Command() {
      @Override
      public boolean execute(CommandData data) {
        data.getOutputLocation().writeln("cmd2");
        return true;
      }

      @Override
      public Set<String> knownFlags() {
        return Collections.emptySet();
      }

      @Override
      public String[] getHelpReference() {
        return new String[0];
      }
    });
    shell.setShowPrompt(false); //don't test prompt
    shell.run();
    //ignore whitespace
    assertEquals(Arrays.asList("cmd1", "cmd2"), output.getBuffer());
  }
}
