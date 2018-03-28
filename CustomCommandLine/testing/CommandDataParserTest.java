package test;

import org.junit.Before;
import org.junit.Test;

import command.CommandData;
import driver.CommandDataParser;
import driver.WorkingDirectoryHandler;
import exception.InvalidParametersException;
import file.Filesystem;
import outputlocation.OutputLocationFile;
import outputlocation.OutputLocationNop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link CommandDataParser} class.
 */
public class CommandDataParserTest {

  private WorkingDirectoryHandler fileHandler;
  private Filesystem filesystem;
  private CommandDataParser parser;

  /**
   * Sets up a simple filesystem and discards output from the data parser
   */
  @Before
  public void setUp() {
    filesystem = new Filesystem();
    filesystem.getFile("/").addDirectory("folder1");
    filesystem.getFile("/").addDirectory("folder2");
    filesystem.getFile("/folder1").addFile("file1");
    filesystem.getFile("/folder2").addFile("file2");
    filesystem.getFile("/folder2").addFile("file3");
    fileHandler = new WorkingDirectoryHandler(filesystem);
    parser = new CommandDataParser(fileHandler,
                                   new OutputLocationNop(),
                                   new OutputLocationNop());
  }

  /**
   * Ensures that piping output to a nonexistent folder throws an exception
   *
   * @throws InvalidParametersException when the redirect to nonexistent file
   *                                    is parsed
   */
  @Test(expected = InvalidParametersException.class)
  public void testWriteNonExistent() throws InvalidParametersException {
    parser.parse("cmd someargument >/nonexistant/file");
  }

  /**
   * Ensures that piping output to a folder causes an error
   *
   * @throws InvalidParametersException when the command is evaulated to be
   *                                    writing to a folder
   */
  @Test(expected = InvalidParametersException.class)
  public void testWriteFolder() throws InvalidParametersException {
    parser.parse("cmd someargument >/folder1");
  }

  /**
   * Ensures that writing to files works correctly otherwise
   *
   * @throws InvalidParametersException if there is a problem parsing file
   *                                    output redirects
   */
  @Test
  public void testFileOutput() throws InvalidParametersException {
    CommandData data = parser.parse("cmd someargument >/folder1/file1");
    assertEquals(new OutputLocationFile(fileHandler, "/folder1/file1"),
                 data.getOutputLocation());
  }

  /**
   * Ensures that appending to files works correctly otherwise
   *
   * @throws InvalidParametersException if there is a problem parsing
   *                                    appending file output redirects
   */
  @Test
  public void testFileAppending() throws InvalidParametersException {
    CommandData data = parser.parse("cmd someargument >> /folder1/file1");
    assertEquals(
        new OutputLocationFile(fileHandler, "/folder1/file1", true),
        data.getOutputLocation());
  }

  /**
   * Ensures that attempting to redirect output to a folder throws an exception.
   *
   * @throws InvalidParametersException when the parser tries to redirect
   *                                    output to a folder
   */
  @Test(expected = InvalidParametersException.class)
  public void testFolderOutputError() throws InvalidParametersException {
    parser.parse("cmd someargument >/folder1");
  }

  /**
   * Ensures that an empty flag list throws an exception
   *
   * @throws InvalidParametersException when the parser tries to parse an
   *                                    empty flag list
   */
  @Test(expected = InvalidParametersException.class)
  public void testEmptyFlagListError() throws InvalidParametersException {
    parser.parse("cmd - arguments"); //empty flag list
  }

  /**
   * Ensures that repeated flags throw an exception
   *
   * @throws InvalidParametersException when the parser tries to parse the
   *                                    same flag twice
   */
  @Test(expected = InvalidParametersException.class)
  public void testRepeatedFlagError() throws InvalidParametersException {
    parser.parse("cmd -aa arguments");
  }

  /**
   * Ensures that flags are parsed properly
   *
   * @throws InvalidParametersException if parsing of valid flags fails
   */
  @Test
  public void testValidFlags() throws InvalidParametersException {
    CommandData data = parser.parse("cmd -x -v -zf arguments");
    assertEquals(1, data.getParameters().size());
    //ensure arguments are still properly parsed
    assertEquals("arguments", data.getParameters().get(0));
    assertEquals(4, data.getToggleFlags().size());
    assertTrue(data.getToggleFlags().contains("x"));
    assertTrue(data.getToggleFlags().contains("v"));
    assertTrue(data.getToggleFlags().contains("z"));
    assertTrue(data.getToggleFlags().contains("f"));
  }
}
