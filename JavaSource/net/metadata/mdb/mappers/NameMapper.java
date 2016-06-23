package net.metadata.mdb.mappers;

public abstract interface NameMapper
{
  public static final NameMapper IDENTITY = new NameMapper()
  {
    public String getTableName(String tableName)
    {
      return tableName;
    }
  };
  
  public abstract String getTableName(String paramString);
}
