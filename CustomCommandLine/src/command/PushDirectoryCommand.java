package command;

import java.util.Collections;
import java.util.Set;

import driver.JShell;
import driver.WorkingDirectoryHandler;

/**
 * Class is in charge of pushing the current working directory onto a directory
 * stack and changing the working directory to a specified directory.
 */
public class PushDirectoryCommand implements Command {

  private final JShell shell;
  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param shell       the shell whose {@link JShell#pushDirectory(String)}
   *                    will be called.
   * @param fileHandler the file handler that resolves and sets the path of the
   *                    current directory and the directory that is
   *                    being pushed.
   */
  public PushDirectoryCommand(JShell shell,
                              WorkingDirectoryHandler fileHandler) {
    this.shell = shell;
    this.fileHandler = fileHandler;
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() != 1) {
      return false;
    }
    String cwd = fileHandler.getCurrentDir();
    String directory = data.getParameters().get(0);
    if (fileHandler.getFile(directory) == null) {
      data.getErrorOutputLocation()
          .writeln("Tried to push a directory that didn't exist: " + directory);
      return true;
    }
    if (!fileHandler.getFile(directory).isDirectory()) {
      data.getErrorOutputLocation()
          .writeln(
              "Tried to push a file which wasn't a directory: " + directory);
      return true;
    }
    shell.pushDirectory(cwd);
    if (!fileHandler.setCurrentDir(directory)) {
      data.getErrorOutputLocation().writeln(
          "Something went wrong while changing directory");
      return true;
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
        "pushd DIR",
        "Saves the current working directory by pushing it onto the directory "
        + "stack and then changes the new current working directory to DIR.",
        "If DIR does not exist or isn't a directory this command instead "
        + "prints an error and doesn't make any changes."
    };
  }

}
