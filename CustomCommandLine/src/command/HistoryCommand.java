package command;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import driver.JShell;

/**
 * This class is used to return a list of commands that have previously been
 * entered by the user. The user can specify how many commands should be in
 * the list. If not specified, the list contains every single command that
 * the user has entered, including the "history" command itself.
 */
public class HistoryCommand implements Command {

  private final JShell shell;

  /**
   * @param shell the shell whose {@link JShell#getCommandHistory()} method
   *              will be called to retrieve the command history.
   */
  public HistoryCommand(JShell shell) {
    this.shell = shell;
  }

  @Override
  public boolean execute(CommandData data) {
    List<String> history = shell.getCommandHistory();
    int numtoprint;
    switch (data.getParameters().size()) {
      case 0:
        numtoprint = history.size();
        break;
      case 1:
        try {
          numtoprint = Integer.parseInt(data.getParameters().get(0));
          if (numtoprint > history.size()) {
            numtoprint = history.size(); //just truncate to entire history
          }
          if (numtoprint < 0) {
            data.getErrorOutputLocation().writeln(
                "Number to print can not be negative");
            return true;
          }
        } catch (NumberFormatException e) {
          data.getErrorOutputLocation().writeln(
              "The argument needs to be a non-negative integer");
          return true;
        }
        break;
      default:
        return false;
    }
    for (int i = history.size() - numtoprint; i < history.size(); i++) {
      data.getOutputLocation()
          .writeln(String.format("%d. %s", i + 1, history.get(i)));
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
        "history [number]",
        "This command will print out recent commands, one command per line",
        "The output from history has two columns.  The first column is "
        + "numbered such that the line with the highest number is the most "
        + "recent command.",
        "The most recent command is this command being called.",
        "The second column contains the actual command.",
        "If a number n (>=0) is specified in the parameters, the output will "
        + "be limited to the last n commands.",
        "Example:",
        "# history 4",
        "1. ls",
        "2. pwd",
        "3. cd csc207",
        "4. history 4"

    };
  }

}
