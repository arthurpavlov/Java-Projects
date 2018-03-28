package outputlocation;

/**
 * Represents the console location in which the output should be written.
 */
public class OutputLocationConsole implements OutputLocation {
    private String rdata;
    
    /**
     * @param outputstring      the string that needs to written
     */
    public OutputLocationConsole(String outputstring){
      rdata = outputstring;
    }
    
    /**
     * Writes to console location.
     */
    public void write(){
      System.out.println(rdata);
    }
}
