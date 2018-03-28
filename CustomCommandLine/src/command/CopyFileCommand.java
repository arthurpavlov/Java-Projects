package command;

import java.util.Collections;
import java.util.Set;

import driver.WorkingDirectoryHandler;
import file.File;
import file.Filesystem;

/**
 * This class is used to copy a file or directory into another directory.
 * It calls the relevant methods from WorkingDirectoryHandler and File
 * to create a copy of the selected file or directory and place it in a
 * chosen directory.
 */

public class CopyFileCommand implements Command {

  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param fileHandler the file handler to be used to resolve the files to
   *                    be copied.
   */
  public CopyFileCommand(WorkingDirectoryHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  /**
   * @param data a {@link CommandData} object containing the parameters and
   *             other contextual data for this command to execute.
   *             In this case, CommandData should contain two file paths, one
   *             to copy from and the other to copy to.
   * @return boolean true if the command executes properly, false if the
   * arguments provided are invalid.
   */

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() != 2) {
      return false;
    }
    String oldPath = data.getParameters().get(0);
    String newPath = data.getParameters().get(1);

    File toFile = this.fileHandler.getFile(newPath);
    File fromFile = this.fileHandler.getFile(oldPath);

    if (fromFile == null) {
      data.getErrorOutputLocation().writeln("The file/directory to copy "
                                            + oldPath + " does not exist");
      return true;
    }
    if (toFile == null) {
      //the new output doesn't exist, so it obviously isn't a folder
      File parent = fileHandler.getFileDirectory(newPath);
      if (parent == null || !parent.isDirectory()) {
        data.getErrorOutputLocation()
            .writeln("Invalid output location: " + newPath);
        return true;
      }
      //so we know the parent of the new output is a directory
      String newFileName = Filesystem.getFileName(newPath);
      //and we add the requested file name there
      parent.addFile(newFileName, new File(fromFile));
      return true;
    }
    if (toFile.isFile()) {
      //overwrite the contents of toFile with the contents of the file we are
      //copying
      File parent = fileHandler.getFileDirectory(newPath);
      String newFileName = Filesystem.getFileName(newPath);
      parent.addFile(newFileName, new File(fromFile));
      return true;
    }
    if (toFile.isDirectory()) {
      //infer the name from the old name of the file
      String oldName = Filesystem.getFileName(oldPath);
      //add it to toFile, which is a directory
      toFile.addFile(oldName, new File(fromFile));
      return true;
    }
    data.getErrorOutputLocation().writeln("Unknown output file type.");
    return true;
  }

  @Override
  public Set<String> knownFlags() {
    return Collections.emptySet();
  }

  @Override
  public String[] getHelpReference() {
    return new String[]{
        "cp OLDPATH NEWPATH",
        "Copies a file or directory at OLDPATH into the directory at NEWPATH.",
        "Example:",
        "# cp fileA directoryB"
    };
  }
}
