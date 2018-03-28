package outputlocation;

import java.util.Collection;

/**
 * Represents an output location which is the standard output stream.
 * I.E "echo hello" will be equivalent to System.out.println("hello")
 */
public class OutputLocationStandardOut implements OutputLocation {

  @Override
  public void writeln(String... data) {
    for (String s : data) {
      System.out.println(s);
    }
  }

  @Override
  public void writeln(Collection<String> data) {
    for (String s : data) {
      System.out.println(s);
    }
  }

  @Override
  public void write(String... data) {
    for (String s : data) {
      System.out.print(s);
    }
  }

  @Override
  public void write(Collection<String> data) {
    for (String s : data) {
      System.out.print(s);
    }
  }

  @Override
  public void flush() {
    System.out.flush();
  }
}
