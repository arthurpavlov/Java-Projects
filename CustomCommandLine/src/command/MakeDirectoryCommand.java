package command;

import java.util.Collections;
import java.util.Set;

import driver.WorkingDirectoryHandler;
import file.File;
import file.Filesystem;

/**
 * This class is used to create new directories in the file system using
 * relative or absolute paths provided by the user.
 * It calls the relevant methods from WorkingDirectoryHandler and File
 * to create a new directory
 */
public class MakeDirectoryCommand implements Command {

  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param fileHandler the file handler to be used to resolve the paths at
   *                    which new directories should be created.
   */
  public MakeDirectoryCommand(WorkingDirectoryHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  /**
   * @param data a {@link CommandData} object containing the parameters and
   *             other contextual data for this command to execute.
   *             In this case, CommandData should contain one or more relative
   *             or absolute file paths that are used to make the directories.
   * @return boolean true if the command executes properly, false if the
   * arguments provided are invalid.
   */

  @Override
  public boolean execute(CommandData data) {

    if (data.getParameters().size() == 0) {
      return false;
    }
    for (String dirName : data.getParameters()) {
      File parentDir = this.fileHandler.getFileDirectory(dirName);

      // Checks if the parent directory exists or is a data file
      if (parentDir == null) {
        data.getErrorOutputLocation().writeln("The parent folder for " + dirName
                                              + " does not exist.");
        continue;
      }
      if (!parentDir.isDirectory()) {
        data.getErrorOutputLocation().writeln("The parent folder for " + dirName
                                              + " is not a directory.");
        continue;
      }
      String fileName = Filesystem.getFileName(dirName);
      if (!File.isValidFileName(fileName)) {
        data.getErrorOutputLocation()
            .writeln("Invalid directory name: " + dirName);
        continue;
      }
      if (parentDir.getFile(fileName) != null) {
        data.getErrorOutputLocation()
            .writeln("A file already exists at " + dirName);
        continue;
      }
      parentDir.addDirectory(fileName);
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
        "mkdir DIR1 [DIR2...]",
        "This command creates one or more directories using either relative "
        + "or absolute paths.",
        "Example:",
        "# mkdir a b",
        "# mkdir b/csc207",
        "# mkdir a/csc207",
        "The parent folder for a/csc207 does not exist."
    };
  }
}
