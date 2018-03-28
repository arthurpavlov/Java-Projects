package driver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import command.CommandData;
import exception.InvalidParametersException;
import file.File;
import file.Filesystem;
import outputlocation.OutputLocation;
import outputlocation.OutputLocationFile;

/**
 * Parses parameters into output redirects, and the raw parameters
 */
public class CommandDataParser {

  private final WorkingDirectoryHandler fileHandler;
  //TODO interface standardOutput?
  private final OutputLocation standardOutput;
  private final OutputLocation standardError;

  //Split a line of text into "quote surrounded sections" and individual params
  //Group 0: each string
  //Everything else is non-capturing (?:)
  //Examples of what this does [and should-- eventually-- do]:
  //"abc d" -> [abc d]
  //a"bc"d -> [abcd] (currently: [a] [bc] [d])
  //a b c d -> [a] [b] [c] [d]
  //"ab c" d -> [ab c] [d]
  //a" -> [a"]
  private static final Pattern PARAMETER_PATTERN =
      Pattern.compile("(?:\"(.+?)\"|\\S+)");

  //ensure that flags are only alphanumeric, no special characters
  private static final Pattern FLAG_PATTERN = Pattern.compile("-[A-Za-z]+$");

  //matches > file >file >> file >file or any derivatives thereof
  //the weird [^\s>] group simply matches file names that don't contain
  // spaces or >s, as the validation logic is done in the method itself.
  private static final Pattern OUTPUT_REDIRECT_PATTERN
      = Pattern.compile("(?:\\s|^)(>|>>)\\s*([^\\s>]+)");

  /**
   * @param fileHandler the handler used to resolve file output redirects
   * @param out         the default output location for command data parsed
   *                    by this parser, given that it isn't changed
   * @param err         the error output location for command data parsed by
   *                    this parser, given that it isn't changed
   */
  public CommandDataParser(WorkingDirectoryHandler fileHandler,
                           OutputLocation out, OutputLocation err) {
    this.fileHandler = fileHandler;
    this.standardOutput = out;
    this.standardError = err;
  }

  private List<String> parseParameters(String input) {
    List<String> params = new ArrayList<>();
    //match all of the individual arguments from the argument portion
    if (input == null) { //no parameters
      return params;
    }
    input = input.trim();
    if (input.isEmpty()) { //no params, only white space
      return params;
    }
    Matcher m = PARAMETER_PATTERN.matcher(input);
    while (m.find()) {
      if (m.group(1) != null) { //quotation surrounded string
        params.add(m.group(1));
      } else { //just a regular string
        params.add(m.group(0));
      }
    }
    return params;
  }

  private OutputLocation getOutputLocation(String input)
      throws InvalidParametersException {
    if (input == null) {
      return null;
    }
    input = input.trim();
    Matcher m = OUTPUT_REDIRECT_PATTERN.matcher(input);
    OutputLocation ret = null;
    String currentFilename = null;
    while (m.find()) {
      if (currentFilename != null) {
        throw new InvalidParametersException(
            "Input already set to " + currentFilename);
      }
      //this group is (>|>>), so if it's not append it's going to be overwrite.
      boolean append = m.group(1).equals(">>");
      currentFilename = m.group(2);
      File file = fileHandler.getFile(currentFilename);
      if (file == null
          && fileHandler.getFileDirectory(currentFilename) == null) {
        //output doesn't exist, but the directory doesn't exist either.
        throw new InvalidParametersException(
            "Invalid output location: " + currentFilename);
      }
      if (file == null && !File
          .isValidFileName(Filesystem.getFileName(currentFilename))) {
        //output doesn't exist, and we can't create it because the requested
        //file name is invalid.
        throw new InvalidParametersException(
            "Invalid output file name: " + currentFilename);
      }
      if (file != null && !file.isFile()) {
        throw new InvalidParametersException(
            "Output location is a directory: " + currentFilename);
      }
      ret = new OutputLocationFile(fileHandler, currentFilename, append);
    }
    return ret;
  }

  private String removeOutputLocation(String input) {
    return OUTPUT_REDIRECT_PATTERN.matcher(input).replaceAll("");
  }

  private Set<String> getToggleFlags(String input)
      throws InvalidParametersException {
    Set<String> flags = new HashSet<>();
    for (String naiveParam : input.split(" ")) {
      if (naiveParam == null) {
        continue; //weird split edge case
      }
      if (!naiveParam.isEmpty() && !naiveParam.startsWith("-")) {
        break; //stop reading after we hit an argument
      }
      if (!FLAG_PATTERN.matcher(naiveParam).matches()) {
        throw new InvalidParametersException(
            "Failed to parse flag: " + naiveParam);
      }
      for (int i = 1; i < naiveParam.length(); i++) { //skip the dash
        String flag = String.valueOf(naiveParam.charAt(i)).toLowerCase();
        if (flags.contains(flag)) {
          throw new InvalidParametersException("Duplicate flag found: " + flag);
        }
        flags.add(flag); //add individual chars
      }
    }
    return flags;
  }

  private String removeToggleFlags(String input) {
    boolean readingFlag = false;
    int index;
    for (index = 0; index < input.length(); index++) {
      char c = input.charAt(index);
      if (Character.isWhitespace(c)) {
        readingFlag = false;
      } else if (c == '-') {
        if (readingFlag) {
          break;
        }
        readingFlag = true;
      } else if (Character.isAlphabetic(c)) {
        if (!readingFlag) {
          break;
        }
      } else {
        break;
      }
    }
    return input.substring(index, input.length());
  }

  /**
   * @param input the user supplied input to be validated and parsed
   * @return a {@link CommandData} object containing the parsed data
   * @throws InvalidParametersException if the data given was unable to be
   *                                    parsed.
   */
  public CommandData parse(String input) throws InvalidParametersException {
    input = input.trim();
    String[] splitInput = input.split(" ", 2);
    String commandName = splitInput[0];
    List<String> params;
    Set<String> toggleFlags;
    OutputLocation redirectOutput = null;
    if (splitInput.length == 2) { //we actually have arguments of some kind
      input = splitInput[1];
      toggleFlags = getToggleFlags(input);
      if (!toggleFlags.isEmpty()) {
        input = removeToggleFlags(input);
      }
      redirectOutput = getOutputLocation(input);
      if (redirectOutput != null) {
        input = removeOutputLocation(input);
      }
      params = parseParameters(input);
    } else {
      params = Collections.emptyList(); //no arguments
      toggleFlags = Collections.emptySet(); //no flags
    }

    //if the redirect output was successfully set to something, use that
    //otherwise use the default
    return new CommandData(commandName, params,
                           redirectOutput != null ? redirectOutput
                                                  : standardOutput,

                           standardError, toggleFlags);
  }
}
