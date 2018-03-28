package command;

import java.util.Collections;
import java.util.Set;

import driver.WorkingDirectoryHandler;
import file.File;

/**
 * This class is used to change the current working directory to one specified
 * by the user. It calls the relevant methods in WorkingDirectoryHandler to
 * set a new current directory.
 */
public class ChangeDirectoryCommand implements Command {

  private final WorkingDirectoryHandler fileHandler;

  /**
   * Creates a new ChangeDirectoryCommand
   *
   * @param fileHandler the working directory handler which this command
   *                    will use to resolve paths and change directories.
   */
  public ChangeDirectoryCommand(WorkingDirectoryHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() != 1) {
      return false;
    }
    String fileName = data.getParameters().get(0);
    if (fileName.contains(" ")) {
      data.getOutputLocation()
          .writeln("Directory name can not contain spaces.");
      return true;
    }
    File directory = fileHandler.getFile(fileName);
    if (directory == null) {
      data.getOutputLocation().writeln("Non-existent directory: " + fileName);
      return true;
    }
    if (!directory.isDirectory()) {
      data.getOutputLocation()
          .writeln("Requested directory is a file: " + fileName);
      return true;
    }
    if (!fileHandler.setCurrentDir(fileName)) {
      data.getOutputLocation()
          .writeln("Failed to change directory to " + fileName + ".");
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
        "Change directory to DIR, which may be relative to the current"
        + " directory or may be a full path.",
        "The root of the file system is a single slash: /."
    };
  }
}
