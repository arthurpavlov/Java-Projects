package driver;

import file.File;

import file.Filesystem;

/**
 * This class stores both a filesystem and a working directory, and provides
 * utility methods to retrieve files at paths relative to that working
 * directory.
 */
public class WorkingDirectoryHandler {

  private final Filesystem filesystem;
  private String currentDir;
  //ensure this always has a trailing slash!
  //and is always canonical.

  /**
   * @param filesystem the filesystem which backs this handler's file retrieval.
   */
  public WorkingDirectoryHandler(Filesystem filesystem) {
    this.filesystem = filesystem;
    currentDir = "/";
  }

  /**
   * returns the current working directory
   *
   * @return the current working directory
   */
  public String getCurrentDir() {
    return currentDir;
  }

  /**
   * Sets the current working directory. If the target path either doesn't
   * exist or is not a directory, this will not make any changes and return
   * false
   *
   * @param path a path, either absolute or relative, that the current working
   *             directory is being changed to
   * @return true if the current dir was set correctly,
   * false if it wasn't.
   */
  public boolean setCurrentDir(String path) {
    path = Filesystem.makeCanonical(makePathAbsolute(path));
    if (!checkDirectory(path)) {
      return false;
    }
    if (!path.endsWith("/")) {
      path = path + "/";
    }
    currentDir = path;
    return true;
  }

  /**
   * Checks if the given path is a directory.
   *
   * @param path the path to check if it is a directory
   * @return true if the given path points to a directory,
   * false otherwise
   */
  public boolean checkDirectory(String path) {
    path = Filesystem.makeCanonical(makePathAbsolute(path));
    File f = getFile(path);
    if (f == null) {
      return false;
    }
    return f.isDirectory();
  }

  /**
   * Checks if the given path is a file.
   *
   * @param path the path to check if it is a file
   * @return true if the given path points to a file,
   * false if otherwise
   */
  public boolean checkFile(String path) {
    path = Filesystem.makeCanonical(makePathAbsolute(path));
    File f = getFile(path);
    if (f == null) {
      return false;
    }
    return f.isFile();
  }

  /**
   * Takes a path that can be either absolute or relative and makes it
   * absolute.  Note that this does not mean that the path is resolved.
   *
   * @param path the path to make into an absolute path
   * @return the absolute path of the original path
   */
  public String makePathAbsolute(String path) {
    if (path.startsWith("/")) {
      return path;
    }
    return currentDir + path;
  }

  /**
   * Retrieves the file at the specified path.
   * Note that this can be a relative path,
   * but it will be resolved from the current working directory
   *
   * @param path the path to be resolved
   * @return The resolved file,
   * or null if the file path couldn't be resolved
   */
  public File getFile(String path) {
    return filesystem.getFile(makePathAbsolute(path));
  }

  /**
   * Gets or creates the file specified, see {@link File#getOrAddFile(String)}
   *
   * @param path The path of the file to retrieve
   * @return The File that already existed, or a new file if it didn't
   */
  public File getOrAddFile(String path) {
    File r = filesystem.getFile(makePathAbsolute(path), true);
    String filename = parseFilename(path);
    if (r == null) {
      return null;
    }
    return r.getOrAddFile(filename);
  }

  /**
   * Strips the filename out of a relative path
   *
   * @param path The path to retrieve the filename from
   * @return the filename of the file that this path points to
   */
  public String parseFilename(String path) {
    return Filesystem.makeCanonical(makePathAbsolute(path))
        .replaceAll(".*/", "");
  }

  /**
   * Retrieves the parent of the file at the specified path.
   * Note that this can be a relative path,
   * but it will be resolved from the current working directory
   *
   * @param path the path to be resolved
   * @return The resolved file,
   * or null if the file path couldn't be resolved
   */
  public File getFileDirectory(String path) {
    return filesystem.getFile(makePathAbsolute(path), true);
  }
}
