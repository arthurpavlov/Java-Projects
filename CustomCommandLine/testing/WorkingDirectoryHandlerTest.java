package test;

import org.junit.Before;
import org.junit.Test;

import driver.WorkingDirectoryHandler;
import file.File;
import file.Filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link WorkingDirectoryHandler} class.
 */
public class WorkingDirectoryHandlerTest {

  private Filesystem fs;
  private WorkingDirectoryHandler wdh;

  /**
   * Set up a filesystem and working directory handler before each test,
   * with some dummy directories.
   */
  @Before
  public void setUp() {
    fs = new Filesystem();
    wdh = new WorkingDirectoryHandler(fs);
    fs.getFile("/").addDirectory("a");
    fs.getFile("/").addDirectory("b");
    fs.getFile("/a/").addDirectory("aa");
    fs.getFile("/b/").addDirectory("ba");
    fs.getFile("/a/").addFile("aa_f");
    fs.getFile("/b/").addFile("ba_f");
  }

  /**
   * Ensure the working directory starts in the root by default.
   */
  @Test
  public void testInitialWorkingDirectory() {
    assertEquals("/", wdh.getCurrentDir());
  }

  /**
   * Test working directory absolute retrieving and setting works.
   */
  @Test
  public void testWorkingDirectoryAbsoluteMutation() {
    wdh.setCurrentDir("/a/");
    assertEquals("/a/", wdh.getCurrentDir());
  }

  /**
   * Test setting the current directory to a relative path
   */
  @Test
  public void testWorkingDirectoryRelativeMutation() {
    wdh.setCurrentDir("/a/");

    //set relative
    assertTrue(wdh.setCurrentDir("../b/"));
    assertEquals("/b/", wdh.getCurrentDir());
  }

  /**
   * Tests setting the current directory relative beyond the root directory.
   */
  @Test
  public void testWorkingDirectoryBeyondRoot() {
    assertTrue(wdh.setCurrentDir("../../.."));
    assertEquals("/", wdh.getCurrentDir());
  }

  /**
   * Ensure that setting the working directory to a file fails.
   */
  @Test
  public void testWorkingDirectoryFileFailure() {
    wdh.setCurrentDir("/a/");
    assertFalse(wdh.setCurrentDir("ba_f")); // set to file
    assertEquals("/a/", wdh.getCurrentDir()); //ensure it hasn't changed.
  }

  /**
   * Ensure that setting the working directory to a nonexistent folder fails.
   */
  @Test
  public void testSetWorkingDirectoryNonexistentFailure() {
    wdh.setCurrentDir("/a/");
    assertFalse(wdh.setCurrentDir("notafile")); // set to nonexistant
    assertEquals("/a/", wdh.getCurrentDir()); //ensure it hasn't changed.
  }

  /**
   * Ensure the checkDirectory method performs as expected
   */
  @Test
  public void testCheckDirectory() {
    assertTrue(wdh.checkDirectory("/a/")); // check directory
    assertFalse(wdh.checkDirectory("/a/aa_f")); // check file
    assertTrue(wdh.checkDirectory("/a/../b/ba")); // check relative
    assertFalse(wdh.checkDirectory("not a file")); // check nonexistant
  }

  /**
   * Ensure the checkFile method works as expected
   */
  @Test
  public void testCheckFile() {
    assertFalse(wdh.checkFile("/a/")); // check directory
    assertTrue(wdh.checkFile("/a/aa_f")); // check file
    assertTrue(wdh.checkFile("/a/../b/ba_f")); // check relative
    assertFalse(wdh.checkFile("not a file")); // check nonexistant
  }

  /**
   * Ensure that making a path absolute works as expected
   */
  @Test
  public void testMakePathAbsolute() {
    assertEquals("/b/../../a", wdh.makePathAbsolute("b/../../a"));
    assertEquals("/b/../../a", wdh.makePathAbsolute("/b/../../a"));
    wdh.setCurrentDir("/a/");
    assertEquals("/a/b/../../a", wdh.makePathAbsolute("b/../../a"));
    assertEquals("/a/aa/../../a", wdh.makePathAbsolute("/a/aa/../../a"));
  }

  /**
   * Ensure getting absolute files works as expected
   */
  @Test
  public void testGetFileAbsolute() {
    assertNotNull(wdh.getFile("/a/aa/")); // test absolute existing 
    assertNull(wdh.getFile("/a/Nonexistant")); // test absolute nonexistant
  }

  /**
   * Ensure getting relative files works as expected
   */
  @Test
  public void testGetFileRelative() {
    wdh.setCurrentDir("/b/");

    assertNotNull(wdh.getFile("./../a/aa")); // test relative existing
    assertNull(wdh.getFile("./../a/Nonexistant")); //test relative nonexistant
  }

  /**
   * Test getting absolute file directories
   */
  @Test
  public void testGetFileDirectoryAbsolute() {
    File f = wdh.getFileDirectory("/a/aa/");
    assertNotNull(f); // test absolute existing
    assertNotNull(f.getFile("aa"));
    assertNotNull(wdh.getFileDirectory("/a/Nonexistant"));
  }

  /**
   * Ensure that getting relative files works as intended.
   */
  @Test
  public void testGetFileDirectoryRelative() {
    wdh.setCurrentDir("/b/");

    File f = wdh.getFileDirectory("./../a/aa");
    assertNotNull(f); // test relative existing
    assertNotNull(f.getFile("aa"));
    assertNull(wdh.getFile("./../a/Nonexistant")); //test relative nonexistant
  }

}
