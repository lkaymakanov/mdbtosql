package net.metadata.mdb.callbacks;

public abstract interface ProgressCallback
{
  public static final ProgressCallback NULL = new ProgressCallback()
  {
    public void startRow(long current, long total) {}
    
    public void endRow() {}
    
    public void startTable(String name, long current, long total) {}
    
    public void endTable() {}
  };
  
  public abstract void startTable(String paramString, long paramLong1, long paramLong2);
  
  public abstract void endTable();
  
  public abstract void startRow(long paramLong1, long paramLong2);
  
  public abstract void endRow();
}
