package command;

import java.util.Collections;
import java.util.Set;

/**
 * This command is used to write a string to a file or the shell.
 * If a file is specified, but does not exist, it is created.
 * If a file is not specified, the string is printed on the shell.
 * If a file is specified, and it already exists, then:
 * If the user enters the command with the format: "echo STRING > FILE"
 * then the data in FILE is erased before STRING is written to it.
 *
 * If the user enters the command with the format: "echo STRING >> FILE"
 * then STRING is appended to any data that already exists in FILE.
 */
public class EchoCommand implements Command {

  @Override
  public boolean execute(CommandData data) {
    StringBuilder sb = new StringBuilder();
    for (String s : data.getParameters()) {
      sb.append(" ").append(s); //space separated arguments
    }
    if (sb.length() == 0) {
      data.getOutputLocation().writeln(""); //just a newline
    } else {
      data.getOutputLocation().writeln(sb.substring(1));
    }
    return true;
  }

  @Override
  public Set<String> knownFlags() {
    return Collections.emptySet();
  }

  @Override
  public String[] getHelpReference() {
    return new String[]{
        "echo STRING [>OUTFILE]",
        "If OUTFILE is not provided, print STRING on the shell.",
        "Otherwise, put STRING into file OUTFILE.",
        "STRING is a string of characters surrounded by double quotation "
        + "marks.",
        "This creates a new file if OUTFILE does not exist, and erases the "
        + "old contents if OUTFILE already exists.",
        "In either case, the only thing in OUTFILE should be STRING. ",
        "echo STRING >> OUTFILE",
        "Like the previous command, but appends instead of overwrites.",
        "Example:",
        "# echo I like programming.",
        "I like programming.",
        "# echo I dislike debugging > csc207",
        "# cat csc207",
        "I dislike debugging"
    };
  }
}
