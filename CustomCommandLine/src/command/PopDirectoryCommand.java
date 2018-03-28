package command;

import java.util.Collections;
import java.util.Set;

import driver.JShell;
import driver.WorkingDirectoryHandler;
import file.File;

/**
 * Class is in charge of popping the current working directory from a directory
 * stack and changing the working directory to a specified directory.
 */
public class PopDirectoryCommand implements Command {

  private final JShell shell;
  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param shell       the shell whose {@link JShell#popDirectory()} will be
   *                    called.
   * @param fileHandler the file handler that resolves and sets the path of the
   *                    current directory and the directory to be popped.
   */
  public PopDirectoryCommand(JShell shell,
                             WorkingDirectoryHandler fileHandler) {
    this.shell = shell;
    this.fileHandler = fileHandler;
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() != 0) {
      return false;
    }
    String targetDir = shell.popDirectory();
    if (targetDir == null) {
      data.getErrorOutputLocation().writeln(
          "There are no more directories in the directory stack to pop",
          "No changes were made.");
      return true;
    }
    File file = fileHandler.getFile(targetDir);
    if (file == null) {
      data.getErrorOutputLocation().writeln(
          "A directory in the directory stack no longer exists: " + targetDir);
      return true;
    }
    if (!file.isDirectory()) {
      data.getErrorOutputLocation().writeln(
          "The directory in the directory stack was no longer a directory: "
          + targetDir);
    }

    if (!fileHandler.setCurrentDir(targetDir)) {
      data.getErrorOutputLocation().writeln(
          "An unknown error occurred while setting the current directory.",
          targetDir);
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
        "popd",
        "Removes the top entry from the directory stack and cd-s into it",
        "If there are no entries in the directory stack, instead it prints "
        + "an error and does nothing.",
        "However, if the directory in the stack no longer exists or was "
        + "made into a file, this command will still remove the topmost entry "
        + "even though it will not change the current working directory."
    };
  }

}
