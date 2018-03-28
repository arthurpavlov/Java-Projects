package outputlocation;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Represents the file location in which the output should be written.
 */
public class OutputLocationFile implements OutputLocation {
  private String rdata;
  private String fname;
  
  /**
   * @param outputstring        the string that needs to written
   * @param filename            the name of the file that the outputstring will 
   *                            be written to
   */
  public OutputLocationFile(String outputstring, String filename){
    rdata = outputstring;
    fname = filename;
  }
  
  /**
   * Writes to file location.
   */
  public void write(){ 
    try{
      //create file
      FileWriter fw = new FileWriter(fname+".txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      PrintWriter out = new PrintWriter(bw);
      //write to file
      out.println(rdata);
      out.close();
    }
    catch(Exception e){
      System.out.print("An error was encounter while creating " + fname+".txt");
    }
  }
}
