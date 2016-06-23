package net.metadata.mdb.callbacks;

public class ThrottledProgressCallback
  implements ProgressCallback
{
  private final ProgressCallback delegate;
  private final long tableCount;
  private final long rowCount;
  private boolean ignoreNextEnd;
  
  public ThrottledProgressCallback(ProgressCallback delegate, long tableCount, long rowCount)
  {
    this.delegate = delegate;
    this.tableCount = tableCount;
    this.rowCount = rowCount;
  }
  
  public void startTable(String name, long current, long total)
  {
    if ((current == total) || (current % this.tableCount == 0L))
    {
      this.delegate.startTable(name, current, total);
      this.ignoreNextEnd = false;
    }
    else
    {
      this.ignoreNextEnd = true;
    }
  }
  
  public void endTable()
  {
    if (!this.ignoreNextEnd) {
      this.delegate.endTable();
    }
  }
  
  public void startRow(long current, long total)
  {
    if ((current == total) || (current % this.rowCount == 0L))
    {
      this.delegate.startRow(current, total);
      this.ignoreNextEnd = false;
    }
    else
    {
      this.ignoreNextEnd = true;
    }
  }
  
  public void endRow()
  {
    if (!this.ignoreNextEnd) {
      this.delegate.endRow();
    }
  }
}
