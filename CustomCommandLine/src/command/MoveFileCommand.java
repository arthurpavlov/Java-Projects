package command;

import java.util.Collections;
import java.util.Set;

import driver.WorkingDirectoryHandler;
import file.File;
import file.Filesystem;

/**
 * This class is used to move files in the file system. The user is able to give
 * full paths and/or paths relative to the current directory as inputs.
 */
public class MoveFileCommand implements Command {

  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param fileHandler the file handler to be used to resolve the files to
   *                    be moved.
   */
  public MoveFileCommand(WorkingDirectoryHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  /**
   * This method checks whether newPath is in a sub-directory of oldPath. Note:
   * This is a private utility method that should only be used inside of this
   * class.
   */
  private boolean isFileParent(String oldPath, String newPath) {
    String a = Filesystem.makeCanonical(fileHandler.makePathAbsolute(oldPath));
    String b = Filesystem.makeCanonical(fileHandler.makePathAbsolute(newPath));
    return b.startsWith(a);
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() != 2) {
      return false;
    }
    String oldPath = data.getParameters().get(0);
    String newPath = data.getParameters().get(1);
    File oldPathFile = fileHandler.getFile(oldPath);
    String oldPathFileName = fileHandler.parseFilename(oldPath);
    File oldPathFileParent = fileHandler.getFileDirectory(oldPath);
    File newPathFile = fileHandler.getFile(newPath);
    String newPathFileName = fileHandler.parseFilename(newPath);
    File newPathFileParent = fileHandler.getFileDirectory(newPath);
    if (oldPathFile == null) {
      data.getErrorOutputLocation()
          .writeln("File at " + oldPath + " cannot be found.");
      return true;
    }
    if (newPathFile == null) {
      if (newPathFileParent != null && !isFileParent(oldPath, newPath)) {
        newPathFileParent.addFile(newPathFileName, oldPathFile);
        oldPathFileParent.removeFile(oldPathFileName);
        return true;
      }
      data.getErrorOutputLocation().writeln(newPath + " is not a valid path");
      return true;
    }
    if (isFileParent(oldPath, newPath)) {
      data.getErrorOutputLocation()
          .writeln("Cannot move a directory into a subdirectory of itself");
      return true;
    }
    oldPathFileParent.removeFile(oldPathFileName);
    if (newPathFile.isDirectory()) {
      //add it inside the directory, don't overwrite the directory
      newPathFile.addFile(oldPathFileName, oldPathFile);
    } else {
      //otherwise just overwrite what is there
      newPathFileParent.addFile(newPathFileName, oldPathFile);
    }
    return true;
  }

  @Override
  public Set<String> knownFlags() {
    return Collections.emptySet();
  }

  public String[] getHelpReference() {
    return new String[]{"mv OLDPATH NEWPATH",
                        "Changes the location of a file or directory at "
                        + "OLDPATH to NEWPATH.",
                        "Both OLDPATH and NEWPATH can be relative paths to "
                        + "the current working"
                        + " directory or can be full paths."};
  }
}


