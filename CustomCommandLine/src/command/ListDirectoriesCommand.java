package command;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import driver.WorkingDirectoryHandler;
import file.File;
import file.Filesystem;
import outputlocation.OutputLocation;

/**
 * A class which lists the directories relative to a given working directory.
 * Optionally, the user may provide a list of relative or absolute paths
 * which will have their file contents listed instead.<br><br>
 * A single error will not cause the entire command to fail, each directory
 * will have its files listed in turn, regardless of whether previous errors
 * were generated.
 * If the -r flag is passed, directories will be listed recursively.
 */
public class ListDirectoriesCommand implements Command {

  private static final Set<String> KNOWN_FLAGS
      = Collections.unmodifiableSet(Collections.singleton("r"));

  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param fileHandler the file handler to be used to resolve directories
   *                    and list files.
   */
  public ListDirectoriesCommand(WorkingDirectoryHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() == 0) {
      File currentDirectory = fileHandler.getFile(".");
      if (currentDirectory == null) {
        data.getErrorOutputLocation()
            .writeln("Current working directory doesn't exist.");
        return true;
      }
      if (!currentDirectory.isDirectory()) {
        data.getErrorOutputLocation()
            .writeln("Current working directory isn't a directory.");
        return true;
      }
      //don't print the .: message, by setting the "file name" to empty string
      printFile(data.getOutputLocation(), data.getErrorOutputLocation(),
                data.getToggleFlags().contains("r"), "", currentDirectory);
    } else {
      boolean first = true;
      for (String fileName : data.getParameters()) {
        if (first) {
          first = false;
        } else {
          data.getOutputLocation().writeln(""); //newline between directories
        }
        //write the file, directory, or recursive directory
        printFile(data.getOutputLocation(), data.getErrorOutputLocation(),
                  data.getToggleFlags().contains("r"), fileName,
                  fileHandler.getFile(fileName));
      }
    }
    return true;
  }

  @Override
  public Set<String> knownFlags() {
    return KNOWN_FLAGS;
  }

  private void printFile(OutputLocation out, OutputLocation err,
                         boolean recursive, String fileName, File file) {
    if (file == null) {
      err.writeln(fileName + " does not exist.");
    } else if (!file.isDirectory()) {
      out.writeln(Filesystem.getFileName(fileName));
    } else {
      boolean first = true;
      if (!fileName.isEmpty()) {
        out.writeln(fileName + ":");
      }
      if (recursive) {
        for (Map.Entry<String, File> subfile : file.getDirectoryContents()
            .entrySet()) {
          if (first) {
            first = false;
          } else {
            out.writeln("");
          }
          printFile(out, err, true, subfile.getKey(), subfile.getValue());
        }
      } else {
        out.writeln(file.getDirectoryContents().keySet());
      }
    }
  }

  @Override
  public String[] getHelpReference() {
    return new String[]{
        "ls (-r) [PATH...]",
        "If no paths are given, prints the contents (file or directory) of the "
        + "current directory, with a new line following each of the content "
        + "(file or directory).",
        "Otherwise, for each path p, the order listed:",
        "  - If p specifies a file, prints p",
        "  - If p specifies a directory, prints p, a colon, then the contents "
        + "of that directory, then an extra new line.",
        "  - If p does not exist, prints p does not exist",
        "If the -r flag is specified, then the contents of the directories, "
        + "if they themselves are directories, will be listed in the same way.",
        "Example:",
        "# ls",
        "file_in_current_directory",
        "# ls /a",
        "a:",
        "file_in_dir_a",
        ""
    };
  }
}
