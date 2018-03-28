package test;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import file.File;
import file.Filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * A series of tests for the Filesystem class.
 */
public class FilesystemTest {

  /**
   * Ensure that the root file is created with the filesystem.
   */
  @Test
  public void filesystemInitializationTest() {
    Filesystem fs = new Filesystem();
    File root = fs.getFile("/");
    assertNotNull(root);
    assertEquals(0, root.getDirectoryContents().size());
  }

  /**
   * Test to ensure that files created exist properly in the
   * file system.
   */
  @Test
  public void fileCreationTest() {
    Filesystem fs = new Filesystem();
    File root = fs.getFile("/");
    //the previous value should be null in a new filesystem
    assertNull(root.getFile("file"));

    File file = root.addFile("file");
    assertNotNull(file);
    assertEquals(file, root.getFile("file"));

    File fsFile = fs.getFile("/file");
    assertEquals(fsFile, file);

    File dir = root.addDirectory("dir");
    assertNotNull(dir);
    assertEquals(dir, root.getFile("dir"));
    assertEquals(dir, fs.getFile("/dir"));

    File innerFile = dir.addFile("innerfile");
    assertNotNull(innerFile);
    assertEquals(innerFile, dir.getFile("innerfile"));
    assertEquals(innerFile, fs.getFile("/dir/innerfile"));
  }

  /**
   * Ensure that getting the parent file works.
   */
  @Test
  public void getParentTest() {
    Filesystem fs = new Filesystem();
    File root = fs.getFile("/");
    File dir = root.addFile("directory");
    assertEquals(dir, fs.getFile("/directory/file", true));
  }

  /**
   * Ensure that resolving uncanonicalized paths works.
   */
  @Test
  public void uncanonicalizedTest() {
    Filesystem fs = new Filesystem();
    File root = fs.getFile("/");
    //you can traverse directories that don't exist, as a design choice.
    assertEquals(root, fs.getFile("/nonexistent/../"));
    assertEquals(root, fs.getFile("/."));
    assertEquals(root, fs.getFile("/./."));
    //you can't go up any more levels from the root, any subsequent attempts
    //will just return the root repeatedly.
    assertEquals(root, fs.getFile("/.."));
    assertEquals(root, fs.getFile("/../.."));
  }

  /**
   * Ensure that the static makeCanonical method works.
   */
  @Test
  public void canonicalizeTest() {
    String path = "/an/absolute/path/../../with/canonical/elements";
    String canonicalized = Filesystem.makeCanonical(path);
    assertEquals("/an/with/canonical/elements", canonicalized);
  }

  /**
   * Ensure that the root directory is canonicalized back into itself.
   */
  @Test
  public void emptyCanonicalizeTest() {
    String path = "/";
    String canonicalized = Filesystem.makeCanonical(path);
    assertEquals(path, canonicalized);
  }

  /**
   * Ensure that going up beyond the root directory is canonicalized properly.
   */
  @Test
  public void beyondRootcanonicalizeTest() {
    String path = "/../../../";
    String canonicalized = Filesystem.makeCanonical(path);
    assertEquals("/", canonicalized);
  }

  /**
   * Ensure that listing directory contents works.
   */
  @Test
  public void listFilesTest() {
    Filesystem fs = new Filesystem();
    File root = fs.getFile("/");
    File dir = root.addDirectory("directory");
    File file1 = dir.addFile("file1");
    File file2 = dir.addFile("file2");
    File file3 = dir.addFile("file3");
    Map<String, File> contents = dir.getDirectoryContents();
    assertEquals(3, contents.size());
    assertEquals(file1, contents.get("file1"));
    assertEquals(file2, contents.get("file2"));
    assertEquals(file3, contents.get("file3"));
  }

  /**
   * Ensure that writing and reading file data works.
   */
  @Test
  public void fileDataTest() {
    Filesystem fs = new Filesystem();
    File file = fs.getFile("/").addFile("file");
    file.write(Arrays.asList("some", "data with spaces", "and multiple lines"));
    List<String> data = file.read();
    assertEquals(3, data.size());
    assertEquals("some", data.get(0));
    assertEquals("data with spaces", data.get(1));
    assertEquals("and multiple lines", data.get(2));
  }
}
