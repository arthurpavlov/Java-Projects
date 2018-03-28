package command;

import java.util.Collections;
import java.util.Set;

import driver.WorkingDirectoryHandler;

/**
 * Class responsible for printing the current working directory.
 */
public class PrintWorkingDirectoryCommand implements Command {

  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param fileHandler the working directory handler whose
   *                    {@link WorkingDirectoryHandler#getCurrentDir()}
   *                    is called.
   */
  public PrintWorkingDirectoryCommand(
      WorkingDirectoryHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() != 0) {
      return false;
    }
    data.getOutputLocation().writeln(fileHandler.getCurrentDir());
    return true;
  }

  @Override
  public Set<String> knownFlags() {
    return Collections.emptySet();
  }

  @Override
  public String[] getHelpReference() {
    return new String[]{
        "pwd",
        "Print the current working directory",
        "Example:",
        "# pwd",
        "/csc207/"
    };
  }
}
