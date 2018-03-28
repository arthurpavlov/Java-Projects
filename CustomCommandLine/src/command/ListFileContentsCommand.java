package command;

import java.util.Collections;
import java.util.Set;

import driver.WorkingDirectoryHandler;
import file.File;

/**
 * This class is used to read from one or more files using relative or
 * absolute paths. It calls relevant methods from WorkingDirectoryHandler and
 * File to locate and read from the specified files.
 */
public class ListFileContentsCommand implements Command {

  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param fileHandler the file handler to be used to resolve and get files
   *                    to read from.
   */
  public ListFileContentsCommand(WorkingDirectoryHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() < 1) {
      return false;
    }

    // Checks is the file exists, and if it a file
    boolean first = true;
    for (String fileName : data.getParameters()) {
      if (!this.fileHandler.checkFile(fileName)) {
        data.getErrorOutputLocation().writeln("The file " + fileName
                                              + " does not exist");
        continue;
      }
      File fileToRead = this.fileHandler.getFile((fileName));
      if (fileToRead.isDirectory()) {
        data.getErrorOutputLocation().writeln(fileName +
                                              " is a directory, not a file");
        continue;
      }
      // If there are multiple files to read from, adds spacing between them
      if (first) {
        first = false;
      } else {
        data.getOutputLocation().writeln();
        data.getOutputLocation().writeln();
        data.getOutputLocation().writeln(); //three line breaks
      }
      data.getOutputLocation().writeln(fileToRead.read());
    }
    return true;
  }

  @Override
  public Set<String> knownFlags() {
    return Collections.emptySet();
  }

  @Override
  public String[] getHelpReference() {
    return new String[]{
        "cat FILE1 [FILE2...]",
        "List the contents of one or more files using relative or absolute"
        + "paths, separated by three blank lines.",
        "Example:",
        "# cat a",
        "this is the data in the file"
    };
  }
}
