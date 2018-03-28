package command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import driver.WorkingDirectoryHandler;
import file.File;
import file.Filesystem;
import outputlocation.OutputLocation;

public class SearchCommand implements Command {

  private static final Set<String> KNOWN_FLAGS
      = Collections.unmodifiableSet(Collections.singleton("r"));

  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param wdh the working directory handler that this command should access.
   */
  public SearchCommand(WorkingDirectoryHandler wdh) {
    this.fileHandler = wdh;
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() < 2) {
      return false;
    }
    Pattern regex;
    try {
      regex = Pattern.compile(data.getParameters().get(0));
    } catch (PatternSyntaxException e) {
      data.getErrorOutputLocation().writeln(
          "Pattern failed to compile: " + data.getParameters().get(0));
      return true;
    }
    for (int x = 1; x < data.getParameters().size(); x++) {
      String s = data.getParameters().get(x);
      File f = fileHandler.getFile(s);
      if (f == null) {
        data.getErrorOutputLocation().writeln("File does not exist: " + s);
        continue;
      }
      s = Filesystem.makeCanonical(fileHandler.makePathAbsolute(s));
      if (f.isDirectory()) {
        writeDirectory(data.getOutputLocation(), s, regex,
                       data.getToggleFlags().contains("r"));
      }
      if (f.isFile()) {
        writeFile(data.getOutputLocation(), s, regex);
      }
    }
    return true;
  }

  private void writeDirectory(OutputLocation out, String path, Pattern regex,
                              boolean recursive) {
    File dir = fileHandler.getFile(path);
    if (!path.endsWith("/")) {
      path = path + "/";
    }
    for (Map.Entry<String, File> e : dir.getDirectoryContents().entrySet()) {
      if (e.getValue().isDirectory()) {
        if (recursive) {
          writeDirectory(out, path + e.getKey(), regex, true);
        }
      }
      if (e.getValue().isFile()) {
        writeFile(out, path + e.getKey(), regex);
      }
    }
  }

  private void writeFile(OutputLocation out, String path, Pattern regex) {
    List<String> lines = new ArrayList<>();
    File f = fileHandler.getFile(path);
    for (String line : f.read()) {
      if (regex.matcher(line).find()) {
        lines.add(line);
      }
    }
    // print output
    if (!lines.isEmpty()) {
      out.writeln(path + " :");
      for (String line : lines) {
        out.writeln(line);
      }
    }
  }

  @Override
  public Set<String> knownFlags() {
    return KNOWN_FLAGS;
  }

  @Override
  public String[] getHelpReference() {
    return new String[]{
        "grep [-R] REGEX PATH ...",
        "If PATH is a file, print the lines containing REGEX in that file",
        "If PATH is a directory, search the files only in that directory ",
        "If -R is not supplied, search for lines containing REGEX in PATH, "
        + "which may be either a file or directory.",
        "If -R is supplied, each PATH that is a directory will be recursively "
        + "searched for files that match the REGEX."
    };
  }

}
