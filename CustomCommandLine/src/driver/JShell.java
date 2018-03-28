// **********************************************************
// Assignment2:

// Student1:
// CDF user_name: chartie6
// UT Student #: 1002675447
// Author: Colin Chartier
//
// Student2:
// CDF user_name:c5lamper
// UT Student #:1002089376
// Author:Erik Lamp
//
// Student3:
// CDF user_name: c5dashab
// UT Student #: 1002074907
// Author: Abhishek Dash
//
// Student4: 
// CDF user_name: c5pavlov
// UT Student #: 1001242737
// Author: Arthur Pavlov
//
//
// Honor Code: I pledge that this program represents my own
// program code and that I have coded on my own. I received
// help from no one in designing and debugging my program.
// I have also read the plagiarism section in the course info
// sheet of CSC 207 and understand the consequences.
// *********************************************************
package driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import command.ChangeDirectoryCommand;
import command.Command;
import command.CommandData;
import command.CopyFileCommand;
import command.EchoCommand;
import command.ExitCommand;
import command.HistoryCommand;
import command.HistoryRunCommand;
import command.ListDirectoriesCommand;
import command.ListFileContentsCommand;
import command.MakeDirectoryCommand;
import command.ManualCommand;
import command.MoveFileCommand;
import command.PopDirectoryCommand;
import command.PrintWorkingDirectoryCommand;
import command.PushDirectoryCommand;
import command.SearchCommand;
import command.WebGetCommand;
import exception.InvalidParametersException;
import file.Filesystem;
import outputlocation.OutputLocation;
import outputlocation.OutputLocationStandardError;
import outputlocation.OutputLocationStandardOut;

/**
 * This class represents an entire virtual shell, complete with filesystem,
 * and commands.<br>
 * To register commands, use {@link #registerDefaultCommands()}
 * To start the shell in a blocking manner, call {@link #run()}
 * To request execution of the shell stop, call {@link #exit()}
 * Note that for immediate exit, an {@link Thread#interrupt()} should be
 * utilized on the thread that this shell is running in.
 */
public class JShell {

  private final Map<String, Command> simpleCommandMap;
  private final Map<Pattern, Command> patternCommandMap;
  private boolean running;

  private final WorkingDirectoryHandler dirHandler;
  private final CommandDataParser commandDataParser;
  private boolean showPrompt = true;
  private final String prompt;

  private final List<String> commandHistory;
  private final Stack<String> directoryStack;

  private final Reader input;

  private final OutputLocation standardOutput;
  private final OutputLocation standardError;

  /**
   * @param dirHandler the working directory handler used to resolve paths
   *                   and keep track of the working directory.
   * @param input      the input to be read from to parse commands
   * @param out        where the standard outputs for the prompt and command
   *                   output should be sent
   * @param err        where any exceptions from command input should be sent
   */
  public JShell(WorkingDirectoryHandler dirHandler, Reader input,
                OutputLocation out, OutputLocation err) {
    //makeshift dependency injection for ease of testing.
    //don't worry if you don't know what this is, but look it up if you
    //are curious some time, it is a very useful design pattern.
    this.dirHandler = dirHandler;
    this.standardOutput = out;
    this.standardError = err;
    commandDataParser = new CommandDataParser(dirHandler,
                                              out, err);
    this.input = input;

    simpleCommandMap = new HashMap<>();
    patternCommandMap = new HashMap<>();

    prompt = "#";
    commandHistory = new ArrayList<>();
    directoryStack = new Stack<>();
  }

  /**
   * Retrieves the given command from the stored command instances.
   *
   * @param commandName the string name of the command to be retrieved
   * @return the Command object requested if it exists,
   * or null if it doesn't
   */
  public Command getSimpleCommand(String commandName) {
    if (simpleCommandMap.containsKey(commandName)) {
      return simpleCommandMap.get(commandName);
    }
    return null;
  }

  /**
   * Retrieves an expression command which matches the expression provided.
   *
   * @param expression the expression which matches the command to be retrieved.
   * @return the {@link Command} object requested if it exists,
   * or null otherwise.
   */
  public Command getExpressionCommand(String expression) {
    //this is slow, but i can't think of a better way to do it.
    for (Map.Entry<Pattern, Command> e : patternCommandMap.entrySet()) {
      if (e.getKey().matcher(expression).matches()) {
        return e.getValue();
      }
    }
    return null;
  }

  /**
   * Runs a command specified by the given command data.
   * Any errors thrown while this command is being run will be redirected to
   * {@link CommandData#getErrorOutputLocation()}.
   *
   * @param data the data specifying the command to be run.
   */
  public void runCommand(CommandData data) {
    //try simple commands first, they are cheap to look up
    Command command = getSimpleCommand(data.getCommandName());
    if (command == null) {
      //if that fails, try the slow expression commands
      command = getExpressionCommand(data.getCommandName());
    }
    if (command == null) {
      //otherwise it isn't valid.
      data.getErrorOutputLocation()
          .writeln("Unknown command: " + data.getCommandName());
      data.getErrorOutputLocation().flush();
      return;
    }
    if (!command.knownFlags().containsAll(data.getToggleFlags())) {
      //we have some invalid flags, print the ones which weren't recognized.
      Set<String> invalidFlags = new HashSet<>(data.getToggleFlags());
      invalidFlags.removeAll(command.knownFlags());
      data.getErrorOutputLocation().writeln("Unknown flag(s): " + invalidFlags);
      data.getErrorOutputLocation().flush();
      return;
    }
    if (!command.execute(data)) {
      data.getErrorOutputLocation().write("Invalid usage.");
    }
    data.getOutputLocation().flush();
    data.getErrorOutputLocation().flush();

  }

  /**
   * Parse and run the command and arguments specified by the given string.
   *
   * @param command the command string to be executed, i.e "ls -r / >file"
   */
  public void runCommand(String command) {
    command = command.trim();
    if (command.isEmpty()) {
      return; //ignore whitespace
    }
    commandHistory.add(command);
    try {
      CommandData data = commandDataParser.parse(command);
      runCommand(data);
    } catch (InvalidParametersException e) {
      standardError.writeln(e.getMessage());
    }
  }

  /**
   * Causes this shell to run indefinitely, reading user input.
   * It will eventually return once one of the commands calls
   * {@link #exit()}, or the end of the input is reached.
   */
  public void run() {
    running = true;
    try (BufferedReader reader = new BufferedReader(input)) {
      while (running) {
        if (showPrompt) {
          standardOutput.write(dirHandler.getCurrentDir(), prompt, " ");
        }
        String line = reader.readLine();
        if (line == null) {
          return; //reached end of input
        }
        runCommand(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Registers a name in the command map
   *
   * @param name    the name of the command to be registered.
   * @param command the command to be registered.
   */
  public void registerSimpleCommand(String name, Command command) {
    if (simpleCommandMap.containsKey(name)) {
      throw new IllegalStateException(
          "Command already registered by the name of '" + name + "'");
    }
    simpleCommandMap.put(name, command);
  }

  /**
   * Register a special command in the command map.
   * Please note that expressions aren't checked for overlapping. There is
   * undefined behavior if multiple expression commands are added with
   * overlapping patterns.
   * The command is responsible for taking any stateful information from the
   * expression through the {@link CommandData#getCommandName()} method.
   *
   * @param expression the regular expression which defines this command.
   * @param command    the command to be executed when the given pattern
   *                   matches.
   */
  public void registerExpressionCommand(String expression, Command command) {
    Pattern pattern = Pattern.compile(expression);
    //we can't check for duplicates because it doesn't make sense to check
    //for duplicate patterns
    patternCommandMap.put(pattern, command);
  }

  /**
   * Register sane default commands for this shell.
   */
  public void registerDefaultCommands() {
    registerSimpleCommand("echo", new EchoCommand());
    registerSimpleCommand("exit", new ExitCommand(this));
    registerSimpleCommand("cd", new ChangeDirectoryCommand(dirHandler));
    registerSimpleCommand("pwd", new PrintWorkingDirectoryCommand(dirHandler));
    registerSimpleCommand("history", new HistoryCommand(this));
    registerSimpleCommand("pushd", new PushDirectoryCommand(this, dirHandler));
    registerSimpleCommand("popd", new PopDirectoryCommand(this, dirHandler));
    registerSimpleCommand("man", new ManualCommand(this));
    registerSimpleCommand("mkdir", new MakeDirectoryCommand(dirHandler));
    registerSimpleCommand("cat", new ListFileContentsCommand(dirHandler));
    registerSimpleCommand("ls", new ListDirectoriesCommand(dirHandler));
    registerExpressionCommand("![0-9]*", new HistoryRunCommand(this));
    registerSimpleCommand("get", new WebGetCommand(dirHandler));
    registerSimpleCommand("grep", new SearchCommand(dirHandler));
    registerSimpleCommand("cp", new CopyFileCommand(dirHandler));
    registerSimpleCommand("mv", new MoveFileCommand(dirHandler));
  }

  /**
   * Notify the shell that the user has requested termination
   */
  public void exit() {
    this.running = false;
  }

  /**
   * Provides an unmodifiable copy of the CommandHistory list.
   *
   * @return the command history list
   */
  public List<String> getCommandHistory() {
    return Collections.unmodifiableList(commandHistory);
  }

  /**
   * pushes the given directory onto the directory stack
   *
   * @param dir the directory to be stored.
   */
  public void pushDirectory(String dir) {
    if (dir == null) {
      throw new IllegalArgumentException("Directory is null.");
    }
    //ensure the directory is canonicalized already.
    if (!dir.equals(Filesystem.makeCanonical(dir))) {
      throw new IllegalArgumentException(
          "Unable to push a non-canonical directory.");
    }
    directoryStack.push(dir);
  }

  /**
   * pops the top directory off of the directory stack and returns it.
   *
   * @return the topmost directory stored,
   * or null if the stack was empty
   */
  public String popDirectory() {
    if (directoryStack.isEmpty()) {
      return null;
    }
    return directoryStack.pop();
  }

  /**
   * @param showPrompt whether or not the prompt should be shown before
   *                   each command.
   */
  public void setShowPrompt(boolean showPrompt) {
    this.showPrompt = showPrompt;
  }

  /**
   * Entry point to the program
   *
   * @param args args that are ignored
   */
  public static void main(String[] args) {
    JShell shell =
        new JShell(new WorkingDirectoryHandler(new Filesystem()),
                   new InputStreamReader(System.in),
                   new OutputLocationStandardOut(),
                   new OutputLocationStandardError());
    shell.registerDefaultCommands();
    shell.run();
  }
}
