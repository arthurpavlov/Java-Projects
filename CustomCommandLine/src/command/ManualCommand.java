package command;

import java.util.Collections;
import java.util.Set;

import driver.JShell;

/**
 * A class that provides documentation for a command specified by the user.
 */
public class ManualCommand implements Command {

  private final JShell shell;

  /**
   * @param shell the shell whose
   *              {@link JShell#getSimpleCommand(String)} ()} and
   *              {@link JShell#getExpressionCommand(String)} ()}
   *              methods are called to get an instance a chosen command
   */
  public ManualCommand(JShell shell) {
    this.shell = shell;
  }

  @Override
  public boolean execute(CommandData data) {
    if (data.getParameters().size() != 1) {
      return false;
    } else {
      String commandName = data.getParameters().get(0);
      Command command = this.shell.getSimpleCommand(commandName);
      if (command == null) {
        command = shell.getExpressionCommand(commandName);
      }
      if (command == null) {
        data.getErrorOutputLocation()
            .writeln("Invalid command: " + commandName);
        return true;
      }
      data.getOutputLocation().writeln(command.getHelpReference());
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
        "man COMMAND",
        "This command displays the documentation for a provided command",
        "Example:",
        "# man exit",
        "exit",
        "Exit the shell"
    };
  }
}
