package net.metadata.mdb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.is_bg.ltf.db.common.ConnectionProperties;
import net.is_bg.ltf.db.common.DBConfig;
import net.is_bg.ltf.db.common.SqlStatement;
import net.is_bg.ltf.db.common.impl.DataSourceConnectionFactoryDrManager;
import net.is_bg.ltf.db.common.impl.logging.LogFactorySystemOut;
import net.is_bg.ltf.db.common.impl.timer.ElapsedTimer;
import net.is_bg.ltf.db.common.impl.visit.VisitEmpty;
import net.is_bg.ltf.db.common.interfaces.IConnectionFactoryX;
import net.is_bg.ltf.db.common.interfaces.timer.IElaplsedTimer;
import net.is_bg.ltf.db.common.interfaces.timer.IElaplsedTimerFactory;
import net.is_bg.ltf.db.common.interfaces.visit.IVisit;
import net.is_bg.ltf.db.common.interfaces.visit.IVisitFactory;

public class TestQueriesModule
{

  public static ConnectionProperties[] dBases = { 
    new ConnectionProperties("org.postgresql.Driver", "jdbc:postgresql://10.240.110.90:5432/iuis", "iuis", "iuis", "iuis_tru") };
  private String importData;
  
  public void init()
    throws IOException, SQLException
  {
    SqlStatement.setTest(true);
    DBConfig.initDBConfig(new LogFactorySystemOut(), new IVisitFactory() {
		
		@Override
		public IVisit getVist() {
			// TODO Auto-generated method stub
			return new VisitEmpty();
		}
	}, new DesktopAppConnectionFactory(), new IElaplsedTimerFactory() {
		
		@Override
		public IElaplsedTimer getElapsedTimer() {
			// TODO Auto-generated method stub
			return  new ElapsedTimer();
		}
	}, null);
  }
  
  public void test()
  {
    MonInsertDao monInsertDao = new MonInsertDao(DBConfig.getConnectionFactory());
    monInsertDao.importMonTables(this.importData);
  }
  
  public static class DesktopAppConnectionFactory
    implements IConnectionFactoryX
  {
    public Connection getConnection()
    {
      return new DataSourceConnectionFactoryDrManager(TestQueriesModule.dBases[11]).getConnection();
    }

	@Override
	public Connection getConnection(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
  }
  
  public static void main(String[] args)
    throws IOException, SQLException
  {
    Locale.setDefault(new Locale("bg", "BG"));
    SimpleDateFormat daytime = new SimpleDateFormat("dd.MM.yyyy");
    Date d = new Date();
    System.out.println(daytime.format(d));
    

    TestQueriesModule module = new TestQueriesModule();
    

    System.out.println("Data base connection initialized.....");
    module.init();
    module.test();
  }
  
  public String getImportData()
  {
    return this.importData;
  }
  
  public void setImportData(String importData)
  {
    this.importData = importData;
  }
}
