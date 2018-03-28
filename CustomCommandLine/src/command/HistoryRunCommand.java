package command;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import driver.JShell;

/**
 * This command executes a command from the command history,
 * i.e !5 would execute the 5th command in the command history.
 */
public class HistoryRunCommand implements Command {

  //ignore everything to the left of the numeric part that we care about.
  private static final Pattern HISTORY_PATTERN = Pattern.compile(".*?([0-9]+)");

  private final JShell shell;

  /**
   * @param shell the shell whose command history will be retrieved, and
   *              which will handle the execution of the retrieved command.
   */
  public HistoryRunCommand(JShell shell) {
    this.shell = shell;
  }

  @Override
  public boolean execute(CommandData data) {
    if (!data.getParameters().isEmpty()) {
      return false;
    }
    Matcher m = HISTORY_PATTERN.matcher(data.getCommandName());
    if (!m.matches()) {
      return false;
    }
    //this is safe because we sanitized it in the regex
    int num;
    try {
      num = Integer.parseInt(m.group(1));
    } catch (NumberFormatException ex) {
      data.getErrorOutputLocation()
          .write("Invalid history size: " + m.group(1));
      return true;
    }
    int historySize = shell.getCommandHistory().size();
    //if this was just >historySize, you could enter an infinite loop
    //by referencing this command which is currently executing.
    if (num < 1 || num >= historySize) {
      data.getErrorOutputLocation().writeln("Invalid history number: " + num);
      return true;
    }
    String command = shell.getCommandHistory().get(num - 1);
    if (command.startsWith("!")) { //kinda hacky, but works in all cases.
      //no re-running history commands. This is to avoid infinite loops and
      //other bad things.
      data.getErrorOutputLocation()
          .writeln("Can't re-run history run commands: (" + command + ")");
      return true;
    }
    shell.runCommand(command); //off-by-one
    return true;
  }

  @Override
  public Set<String> knownFlags() {
    return Collections.emptySet();
  }

  @Override
  public String[] getHelpReference() {
    return new String[]{
        "![number]",
        "If number is a valid entry in the command history, then re-run that "
        + "command.",
        "For example, if your command history looks like this:",
        "1. mkdir hello",
        "2. echo \"hello!\" > hello/file",
        "3. cat hello/file",
        "and you execute !3, then the command \"cat hello/file\" would be "
        + "executed again."
    };
  }
}
