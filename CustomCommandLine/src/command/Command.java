package command;

import java.util.Set;

/**
 * An interface which represents a stateless command.
 * These commands will be called given stateful information stored in a
 * {@link CommandData} object.
 */
public interface Command {

  /**
   * Executes this command with the given parameters
   *
   * @param data a {@link CommandData} object containing the parameters and
   *             other contextual data for this command to execute.
   * @return false if there are an invalid number of arguments, or an
   * unrecoverably broken argument list is provided, true otherwise.
   */
  boolean execute(CommandData data);

  /**
   * Return a set of lowercase flags, for which this command knows what
   * to do with. At execution time, any flags which are passed which
   * aren't in this set will throw an error.
   *
   * @return a set of flags for which this command knows what to do with.
   * For example, ls knows what the "-r" flag does.
   */
  Set<String> knownFlags();

  /**
   * @return a list of lines as a reference for this command
   */
  String[] getHelpReference();
}
