package net.metadata.mdb;

import net.is_bg.ltf.db.common.AbstractMainDao;
import net.is_bg.ltf.db.common.UpdateSqlStatement;
import net.is_bg.ltf.db.common.interfaces.IConnectionFactory;

public class MonInsertDao
  extends AbstractMainDao
{
  public MonInsertDao(IConnectionFactory connectionFactory)
  {
    super(connectionFactory);
  }
  
  public void importMonTables(String importData)
  {
    final String tmpImportData = importData;
    UpdateSqlStatement update = new UpdateSqlStatement()
    {
      protected String getSqlString()
      {
        return tmpImportData;
      }
    };
    execute(update);
  }
}
