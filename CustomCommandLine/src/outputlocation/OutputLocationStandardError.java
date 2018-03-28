package outputlocation;

import java.util.Collection;

/**
 * Represents an output location which is the standard error
 * I.E "echo hello" will be equivalent to System.err.println("hello").
 */
public class OutputLocationStandardError implements OutputLocation {

  @Override
  public void writeln(String... data) {
    for (String s : data) {
      System.err.println(s);
    }
  }

  @Override
  public void writeln(Collection<String> data) {
    for (String s : data) {
      System.err.println(s);
    }
  }

  @Override
  public void write(String... data) {
    for (String s : data) {
      System.err.print(s);
    }
  }

  @Override
  public void write(Collection<String> data) {
    for (String s : data) {
      System.err.print(s);
    }
  }

  @Override
  public void flush() {
    System.err.flush();
  }
}
