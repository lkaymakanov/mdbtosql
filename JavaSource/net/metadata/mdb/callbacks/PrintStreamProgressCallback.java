package net.metadata.mdb.callbacks;

import java.io.PrintStream;

public class PrintStreamProgressCallback
  implements ProgressCallback
{
  private final PrintStream out;
  
  public PrintStreamProgressCallback(PrintStream out)
  {
    this.out = out;
  }
  
  public void startRow(long current, long total)
  {
    this.out.println("- row " + current + "/" + total);
  }
  
  public void startTable(String name, long current, long total)
  {
    this.out.println("Table " + name + "(" + current + "/" + total + ")");
  }
  
  public void endRow() {}
  
  public void endTable() {}
}
