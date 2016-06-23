package net.metadata.mdb.writers;

import java.io.PrintStream;

public class PrintStreamSqlWriter
  implements SqlWriter
{
  private final PrintStream out;
  
  public PrintStreamSqlWriter(PrintStream out)
  {
    this.out = out;
  }
  
  public void writeSql(String sql)
  {
    this.out.println(sql);
  }
}
