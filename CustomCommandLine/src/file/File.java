package file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A file is the data at a specific relative path.
 * It can be either a directory or a file proper with data, but not both, and
 * not neither.
 */
public class File {

  private static final Pattern INVALID_CHARACTER_PATTERN
      = Pattern.compile("[^a-zA-Z0-9._]");

  private Map<String, File> directoryContents;
  //Data is stored as a List<String> because binary data and encodings and stuff
  //are an enormous pain.
  private List<String> fileContents;
  private final Type type;

  File(Type type) {
    if (type == null) {
      throw new IllegalArgumentException("type");
    }
    this.type = type;
    if (type == Type.FILE) {
      fileContents = new ArrayList<>();
    } else if (type == Type.DIRECTORY) {
      directoryContents = new HashMap<>();
    }
  }

  /**
   * Copy constructor to create a new file from an existing one.
   *
   * @param copyFrom the file whose data should be copied.
   */
  public File(File copyFrom) {
    this.type = copyFrom.type;
    if (type == Type.FILE) {
      fileContents = new ArrayList<>(copyFrom.fileContents);
    } else {
      directoryContents = new HashMap<>();
      for (Map.Entry<String, File> entry : directoryContents.entrySet()) {
        //deep copy all children
        directoryContents.put(entry.getKey(), new File(entry.getValue()));
      }
    }
  }

  private void ensureFile() {
    if (type != Type.FILE) {
      throw new IllegalStateException("Unable to do that on a directory.");
    }
  }

  private void ensureDirectory() {
    if (type != Type.DIRECTORY) {
      throw new IllegalStateException("Unable to do that on a file.");
    }
  }

  private void ensureValidFileName(String proposedName) {
    if (!isValidFileName(proposedName)) {
      throw new IllegalArgumentException("File name invalid: " + proposedName);
    }
  }

  /**
   * @param fileName the name to be checked for validity
   * @return whether or not the proposed file name is free of special
   * characters and can be used.
   */
  public static boolean isValidFileName(String fileName) {
    if (fileName == null) {
      return false;
    }
    if (fileName.equals(".")) {
      return false;
    }
    if (fileName.equals("..")) {
      return false;
    }
    return !INVALID_CHARACTER_PATTERN.matcher(fileName).find();
  }

  /**
   * @return whether or not this File is a simple, data containing file.
   */
  public boolean isFile() {
    return type == Type.FILE;
  }

  /**
   * @return whether or not this File is a directory
   */
  public boolean isDirectory() {
    return type == Type.DIRECTORY;
  }

  /**
   * Overwrites the data in this file with the given data.
   *
   * @param data the data to be written
   */
  public void write(Collection<String> data) {
    ensureFile();
    fileContents = new ArrayList<>(data);
  }

  /**
   * Appends the specified data to the end of this file's data.
   *
   * @param data the data to be written
   */
  public void append(String data) {
    ensureFile();
    fileContents.add(data);
  }

  /**
   * Reads the data from this data containing file.
   *
   * @return an immutable snapshot of the data in this file.
   */
  public List<String> read() {
    ensureFile();
    return Collections.unmodifiableList(fileContents);
  }

  /**
   * Adds a file to this directory, deleting any files already existing with
   * name conflicts.
   *
   * @param name the name of the file to be added
   * @param file the file to be added
   */
  public void addFile(String name, File file) {
    ensureDirectory();
    ensureValidFileName(name);
    directoryContents.put(name, file);
  }

  /**
   * Convenience method to call {@link #addFile(String, File)}
   * on a newly created empty file.
   *
   * @param fileName the name of the file to be created
   * @return the file which was created
   */
  public File addFile(String fileName) {
    ensureDirectory();
    ensureValidFileName(fileName);
    File file = new File(Type.FILE);
    directoryContents.put(fileName, file);
    return file;
  }

  /**
   * Convenience method to call {@link #addFile(String, File)}
   * on a newly created empty directory.
   *
   * @param directoryName the name of the directory to be created
   * @return the directory which was created
   */
  public File addDirectory(String directoryName) {
    ensureDirectory();
    ensureValidFileName(directoryName);
    File file = new File(Type.DIRECTORY);
    directoryContents.put(directoryName, file);
    return file;
  }

  /**
   * Deletes the specified file in this directory.
   *
   * @param name the name of the file to be deleted
   * @return the file removed, or null if such a file does not exist.
   */
  public File removeFile(String name) {
    ensureDirectory();
    return directoryContents.remove(name);
  }

  /**
   * @param name the name of the file to be retrieved from this directory
   * @return the file mapped to by the name specified, or null if such a file
   * does not exist.
   */
  public File getFile(String name) {
    ensureDirectory();
    return directoryContents.get(name);
  }

  /**
   * A helper method to create a file if it does not yet exist, or return
   * it if it does exist.
   *
   * @param fileName the name of the file to retrieve or create
   * @return the file specified by the name given, which was optionally created.
   */
  public File getOrAddFile(String fileName) {
    ensureDirectory();
    ensureValidFileName(fileName);
    File file = getFile(fileName);
    if (file == null) {
      return addFile(fileName);
    }
    return file;
  }

  /**
   * @return a map of the names of the files in this directory to their
   * respective file objects
   */
  public Map<String, File> getDirectoryContents() {
    ensureDirectory();
    //defensive copy
    return new HashMap<>(directoryContents);
  }

  protected enum Type {
    DIRECTORY,
    FILE
  }
}
