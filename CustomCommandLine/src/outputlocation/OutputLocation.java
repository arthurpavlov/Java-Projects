package outputlocation;

import java.util.Collection;

/**
 * Represents a location in which the output from a command can be written.
 */
public interface OutputLocation {

  /**
   * Writes the specified data to this output location, and then a newline.
   *
   * @param data the data to be written to this output location.
   * @see {@link #writeln(Collection)}
   */
  void writeln(String... data);

  /**
   * Writes the specified data to this output location, and then a newline.
   *
   * @param data the data to be written to this output location.
   * @see {@link #writeln(String...)}
   */
  void writeln(Collection<String> data);

  /**
   * Writes the specified data to this output location and continues on the
   * same line
   *
   * @param data the data to be written to this output location
   */
  void write(String... data);

  /**
   * Writes the specified data to this output location and continues on the
   * same line
   *
   * @param data the data to be written to this output location
   */
  void write(Collection<String> data);

  /**
   * Flush the data in this buffered output location to the output itself.
   */
  void flush();
}
