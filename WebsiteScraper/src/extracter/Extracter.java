package extracter;

import java.util.*;

/**
 * Represents an extracter.
 */
public interface Extracter {
  
  /**
   * Extracts a file specification.
   * @return an array list of strings representing file specification
   */
  ArrayList<String> extract(); 
}
