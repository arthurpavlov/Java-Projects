package outputlocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import driver.WorkingDirectoryHandler;
import file.File;
import file.Filesystem;

/**
 * Represents an output location of a file.
 * The file will be created when data is first written to it,
 * otherwise it will stay nonexistent.
 */
public class OutputLocationFile implements OutputLocation {

  private final List<String> buffer = new ArrayList<>();

  private final WorkingDirectoryHandler fileHandler;
  private final String requestedFile;
  private final boolean append;

  /**
   * Creates an output location file which writes to the specified file,
   * overwriting, appending to, or creating it on the first write.
   *
   * @param fileHandler   the workingdirectoryhandler to use to retrieve the
   *                      file.
   * @param requestedFile the file which will be written to
   * @param append        whether or not to append to the data already in the
   *                      file.
   */
  public OutputLocationFile(WorkingDirectoryHandler fileHandler,
                            String requestedFile, boolean append) {
    this.fileHandler = fileHandler;
    this.requestedFile = requestedFile;
    this.append = append;
  }

  /**
   * Creates an output location file which writes to the specified file,
   * overwriting (or creating) it on the first write.
   *
   * @param fileHandler   the workingdirectoryhandler to use to retrieve the
   *                      file.
   * @param requestedFile the file which will be written to
   */
  public OutputLocationFile(WorkingDirectoryHandler fileHandler,
                            String requestedFile) {
    this(fileHandler, requestedFile, false);
  }

  @Override
  public void writeln(String... data) {
    buffer.addAll(Arrays.asList(data));
  }

  @Override
  public void writeln(Collection<String> data) {
    buffer.addAll(data);
  }

  @Override
  public void write(String... data) {
    StringBuilder sb = new StringBuilder();
    for (String s : data) {
      sb.append(s);
    }
    if (buffer.isEmpty()) {
      buffer.add(sb.toString());
    } else {
      buffer.add(buffer.remove(buffer.size() - 1) + sb.toString());
    }
  }

  @Override
  public void write(Collection<String> data) {
    StringBuilder sb = new StringBuilder();
    for (String s : data) {
      sb.append(s);
    }
    if (buffer.isEmpty()) {
      buffer.add(sb.toString());
    } else {
      buffer.add(buffer.remove(buffer.size() - 1) + sb.toString());
    }
  }

  @Override
  public void flush() {
    File output = fileHandler.getFile(requestedFile);
    if (output != null && !output.isFile()) {
      throw new IllegalStateException(
          "Invalid output file given: " + requestedFile);
    }
    //create file if it doesn't exist and we have data to write
    if (!buffer.isEmpty()) {
      File directory = fileHandler.getFileDirectory(requestedFile);
      if (directory == null) {
        throw new IllegalStateException(
            "Invalid parent directory: " + requestedFile);
      }
      output = directory.getOrAddFile(Filesystem.getFileName(requestedFile));
      if (append) {
        for (String s : buffer) {
          output.append(s);
        }
      } else {
        output.write(buffer);
      }
      buffer.clear();
    }
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof OutputLocationFile) &&
           ((OutputLocationFile) other).fileHandler.equals(fileHandler) &&
           ((OutputLocationFile) other).requestedFile.equals(requestedFile) &&
           ((OutputLocationFile) other).append == append;
  }

  @Override
  public String toString() {
    return "[>" + requestedFile + "]";
  }
}
