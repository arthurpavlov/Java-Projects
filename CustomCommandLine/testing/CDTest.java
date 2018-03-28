package test;

import driver.WorkingDirectoryHandler;
import file.Filesystem;
import command.ChangeDirectoryCommand;
public class CDTest {
  public static void main(String [ ] args){
    Filesystem fs = new Filesystem();
    WorkingDirectoryHandler wdh = new WorkingDirectoryHandler(fs);
    ChangeDirectoryCommand cd = new ChangeDirectoryCommand(wdh);
    System.out.println(wdh.getCurrentDir());
  }

}
