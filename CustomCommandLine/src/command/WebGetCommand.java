package command;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import driver.WorkingDirectoryHandler;
import file.File;
import file.Filesystem;

public class WebGetCommand implements Command {

  private final WorkingDirectoryHandler fileHandler;

  /**
   * @param wdh The working directory handler that this command should access.
   */
  public WebGetCommand(WorkingDirectoryHandler wdh) {
    this.fileHandler = wdh;
  }

  private void readFileFromURL(URL from, File to) throws IOException {
    Scanner r = new Scanner(new BufferedInputStream(from.openStream()));
    List<String> readData = new ArrayList<>();
    while (r.hasNextLine()) {
      readData.add(r.nextLine());
    }
    r.close();
    to.write(readData);
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() == 0 ||
        data.getParameters().size() > 2) {
      return false;
    }
    String filename;
    if (data.getParameters().size() == 2) {
      filename = data.getParameters().get(1);
    } else {
      //trim all of the url before the part after the final slash (the name)
      filename = data.getParameters().get(0).replaceAll(".*/", "");
    }
    filename = Filesystem.makeCanonical(fileHandler.makePathAbsolute(filename));
    String urlString = data.getParameters().get(0);
    if (!urlString.matches("^\\w+://.*")) {
      //no protocol specified, guess http
      urlString = "http://" + urlString;
    }
    if (!File.isValidFileName(filename.replaceAll(".*/", ""))) {
      data.getErrorOutputLocation().writeln("The output filename is invalid");
      return true;
    }
    File output = fileHandler.getOrAddFile(filename);
    if (output == null) {
      data.getErrorOutputLocation()
          .writeln("Invalid output location: " + filename);
      return true;
    }
    try {
      URL url = new URL(urlString);
      if (!(url.getProtocol().equals("http") ||
            url.getProtocol().equals("https"))) {
        data.getErrorOutputLocation().writeln("Protocol not supported.");
        return true;
      }
      readFileFromURL(url, output);
    } catch (MalformedURLException e) {
      data.getErrorOutputLocation().writeln("The URL is malformed.");
      return true;
    } catch (IOException e) {
      data.getErrorOutputLocation().writeln("Error reading file from URL.");
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
        "get URL [FILENAME]",
        "Retrieves the file at the specified URL and adds it to the current "
        + "working directory.",
        "If FILENAME is not specified, the file will be named the same as the "
        + "file is in the URL.",
        "If FILENAME is specified, the file will be named FILENAME instead.",
        "This command will only accept URLs with either the http, https, or "
        + "file protocol, any other protocol will return an error."
    };
  }

}
