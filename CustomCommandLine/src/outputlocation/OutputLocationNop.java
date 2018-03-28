package outputlocation;

import java.util.Collection;

/**
 * This output location simply discards data sent to it.<br>
 * So far only used for testing, usually I'd use Mockquito or similar to do
 * something like this,
 * but I knew I was already stretching it by including a lib/ directory.
 */
public final class OutputLocationNop implements OutputLocation {

  @Override
  public void writeln(String... data) {
  }

  @Override
  public void writeln(Collection<String> data) {
  }

  @Override
  public void write(String... data) {
  }

  @Override
  public void write(Collection<String> data) {
  }

  @Override
  public void flush() {

  }

  @Override
  public boolean equals(Object other) {
    return other instanceof OutputLocationNop;
  }

  @Override
  public String toString() {
    return "[>null]";
  }
}
