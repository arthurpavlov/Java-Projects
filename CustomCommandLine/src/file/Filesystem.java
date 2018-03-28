package file;

import java.util.List;
import java.util.Stack;

/**
 * This class keeps track of a file system from an arbitrary root directory.
 * It handles relative path resolution in the unix .././../ way.
 * <br>
 * This approach fixes most of the weird edge cases, I.E deleting the folder
 * you're currently in.  This way it will just say "file does not exist" and
 * fail gracefully.
 */
public class Filesystem {

  private final File rootDirectory = new File(File.Type.DIRECTORY);

  private static String trimSlashes(String input) {
    if (input.endsWith("/")) {
      input = input.substring(0, input.length() - 1);
    }
    if (input.startsWith("/")) {
      input = input.substring(1);
    }
    return input;
  }

  private static Stack<String> getAbsolutePath(String path) {
    if (path == null) {
      throw new IllegalArgumentException("Path can not be null.");
    }
    path = trimSlashes(path);
    //store the final path as a stack, i.e {usr, local, bin}
    Stack<String> absolutePathStack = new Stack<>();
    //if the relative is .., then pop the stack [but only if we aren't already
    //at the root]
    for (String rel : path.split("/")) {
      if ("".equals(rel)) {
        continue;
      }
      if ("..".equals(rel)) {
        if (!absolutePathStack.isEmpty()) {
          absolutePathStack.pop();
        }
      } else if (".".equals(rel)) {
        //ignore
      } else {
        absolutePathStack.push(rel);
      }
    }
    return absolutePathStack;
  }

  /**
   * This makes a string canonical.  This means that it
   * resolves all the '.' and '..' directories.  It his highly recommended
   * to use absolute paths, because a relative path starting with '../' will
   * not get converted correctly otherwise
   *
   * @param path The path to resolve references to
   * @return the canonical version of the path parameter.
   */
  public static String makeCanonical(String path) {
    if (path.isEmpty()) {
      throw new IllegalArgumentException("can't canonicalize an empty path.");
    }
    if (!path.startsWith("/")) {
      throw new IllegalArgumentException("path isn't absolute.");
    }
    List<String> absolutePaths = getAbsolutePath(path);
    StringBuilder s = new StringBuilder();
    for (String pathPart : absolutePaths) {
      s.append("/").append(pathPart);
    }
    String newPath = s.toString();
    if (!path.startsWith("/")) {
      newPath = newPath.substring(1);
    }

    if (path.endsWith("/")) {
      return newPath + "/";
    }
    return newPath;
  }

  private File getFileFromList(List<String> absolutePaths) {
    File iterativeFile = rootDirectory; //start in the root directory
    for (String file : absolutePaths) {
      if (!iterativeFile.isDirectory()) { //part of path isn't directory
        return null;
      }
      iterativeFile = iterativeFile.getFile(file);
      if (iterativeFile == null) { //file doesn't exist
        return null;
      }
    }
    return iterativeFile;
  }

  /**
   * Retrieves the file at the specific path.
   * Note that this can be a relative path,
   * but it will be resolved from the origin (/).
   *
   * @param filePath The path [relative to /] to be resolved
   * @return The resolved file,
   * or null if the file path couldn't be resolved
   * @see {@link #getFile(String, boolean)}
   */
  public File getFile(String filePath) {
    return getFile(filePath, false);
  }

  /**
   * Retrieves the file at the specific path.
   * Note that this can be a relative path,
   * but it will be resolved from the origin (/).
   *
   * @param filePath  The path [relative to /] to be resolved
   * @param getParent whether or not to exclude the rightmost element in
   *                  the path.  I.E "/my/file" with this flag set to true
   *                  would just return the parent folder, "/my/".
   *                  This is useful to create the file in the parent folder
   *                  if it does not yet exist.
   * @return The resolved file,
   * or null if the file path couldn't be resolved
   * @see {@link #getFile(String)}
   */
  public File getFile(String filePath, boolean getParent) {
    Stack<String> absolutePathList = getAbsolutePath(filePath);
    if (getParent) {
      if (!absolutePathList.isEmpty()) {
        absolutePathList.pop();
      }
    }
    return getFileFromList(absolutePathList);
  }

  /**
   * Retrieves the resolved file name from a path.
   * E.G /my/file/.. would return "my"<br>
   * Note that this doesn't look at the file structure at all,
   * and should still be sanitized!
   *
   * @param filePath the path to use to resolve the file name to be copied
   * @return the resolved file name of the last element in the path if it can
   * be reached, or null otherwise.
   */
  public static String getFileName(String filePath) {
    Stack<String> absolutePathList = getAbsolutePath(filePath);
    if (absolutePathList.isEmpty()) {
      return null;
    }
    return absolutePathList.pop();
  }
}
