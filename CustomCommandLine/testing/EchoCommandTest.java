package test;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import command.CommandData;
import command.EchoCommand;
import outputlocation.OutputLocationCollect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link EchoCommand} class
 */
public class EchoCommandTest {

  private OutputLocationCollect out;
  private OutputLocationCollect err;
  private EchoCommand command;

  private CommandData getData(String... params) {
    return new CommandData("echo", Arrays.asList(params), out, err,
                           Collections.<String>emptySet());
  }

  /**
   * Set up a simple command with {@link OutputLocationCollect} outputs.
   */
  @Before
  public void setUp() {
    command = new EchoCommand();
    out = new OutputLocationCollect();
    err = new OutputLocationCollect();
  }

  /**
   * Ensure that writing empty output is handled correctly
   */
  @Test
  public void testEmptyInput() {
    command.execute(getData());
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertEquals(out.getBuffer().size(), 1);
    assertEquals("", out.getBuffer().get(0));
  }

  /**
   * Ensure that writing a single output is handled correctly
   */
  @Test
  public void testSingleInput() {
    command.execute(getData("one"));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertEquals(out.getBuffer().size(), 1);
    assertEquals("one", out.getBuffer().get(0));
  }

  /**
   * Ensure that writing multiple outputs is handled correctly
   */
  @Test
  public void testMultipleInput() {
    command.execute(getData("one two three"));
    assertTrue(err.getBuffer().toString(), err.getBuffer().isEmpty());
    assertEquals(out.getBuffer().size(), 1);
    assertEquals("one two three", out.getBuffer().get(0));
  }
}
