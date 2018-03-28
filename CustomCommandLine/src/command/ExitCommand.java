package command;

import java.util.Collections;
import java.util.Set;

import driver.JShell;

/**
 * This class is used to exit the shell. It calls the relevant method
 * from JShell when executed.
 */
public class ExitCommand implements Command {

  private final JShell shell;

  /**
   * @param shell the shell whose {@link JShell#exit()} method will be called
   *              on.
   */
  public ExitCommand(JShell shell) {
    this.shell = shell;
  }

  @Override
  public boolean execute(CommandData data) {
    this.shell.exit();
    return true;
  }

  @Override
  public Set<String> knownFlags() {
    return Collections.emptySet();
  }

  @Override
  public String[] getHelpReference() {
    return new String[]{
        "exit",
        "Exit the shell"
    };
  }
}
