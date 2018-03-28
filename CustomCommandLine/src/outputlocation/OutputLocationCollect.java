package outputlocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This output location stores data written to it as a list of strings.
 */
public final class OutputLocationCollect implements OutputLocation {

  private final List<String> buffer = new ArrayList<>();

  @Override
  public void writeln(String... data) {
    writeln(Arrays.asList(data));
  }

  @Override
  public void writeln(Collection<String> data) {
    if (data.isEmpty()) {
      buffer.add(""); //newline
    } else {
      buffer.addAll(data);
    }
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

  }

  @Override
  public boolean equals(Object other) {
    return other instanceof OutputLocationCollect;
  }

  @Override
  public String toString() {
    return "[>buffer]";
  }

  /**
   * @return the list of strings which contains all of the input collected so
   * far into this collecting output location.
   */
  public List<String> getBuffer() {
    return Collections.unmodifiableList(buffer);
  }
}
