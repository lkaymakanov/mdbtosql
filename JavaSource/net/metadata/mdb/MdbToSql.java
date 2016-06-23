package net.metadata.mdb;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.IndexData;
import com.healthmarketscience.jackcess.Table;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.metadata.mdb.callbacks.PrintStreamProgressCallback;
import net.metadata.mdb.callbacks.ProgressCallback;
import net.metadata.mdb.callbacks.ThrottledProgressCallback;
import net.metadata.mdb.mappers.NameMapper;
import net.metadata.mdb.mappers.PostgreSqlMapper;
import net.metadata.mdb.mappers.SqlMapper;
import net.metadata.mdb.writers.PrintStreamSqlWriter;
import net.metadata.mdb.writers.SqlWriter;

public class MdbToSql
{
  public static void main(String[] args)
    throws IOException, SQLException
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(out);
    exportSql(new PrintStreamSqlWriter(printStream), new PostgreSqlMapper(), new String("D:\\jboss\\DataUni.mdb"), 
      DropStyle.NO_DROP, new ThrottledProgressCallback(
      new PrintStreamProgressCallback(System.out), 1L, 5000L));
   // TestQueriesModule test = new TestQueriesModule();
   // test.init();
    
    OutputStream outputStream = new FileOutputStream("D:\\jboss\\sqlfile.sql");
    out.writeTo(outputStream);
    
    //test.setImportData(out.toString());
    //test.test();
    if (args.length == 1)
    {
      exportSql(new PrintStreamSqlWriter(System.out), new PostgreSqlMapper(), args[0], DropStyle.DROP_IF_EXISTS, 
        ProgressCallback.NULL);
    }
    else if (args.length == 2)
    {
      exportSql(new PrintStreamSqlWriter(printStream), new PostgreSqlMapper(), args[0], 
        DropStyle.DROP_IF_EXISTS, new ThrottledProgressCallback(
        new PrintStreamProgressCallback(System.out), 1L, 5000L));
      System.out.println(out.toString());
    }
    else
    {
      System.err.println("Usage:");
      System.err.println("      MdbToSql INPUT_DATABASE [OUTPUT_FILE]");
      System.err.println("  If output file is not given, stdout will be used");
      System.exit(1);
    }
  }
  
  public static void exportSql(SqlWriter writer, SqlMapper mapper, String inputFile, DropStyle dropStyle, ProgressCallback callback)
    throws IOException, SQLException
  {
    exportSql(writer, mapper, NameMapper.IDENTITY, inputFile, dropStyle, callback);
  }
  
  public static void exportSql(SqlWriter writer, SqlMapper sqlMapper, NameMapper nameMapper, String inputFile, DropStyle dropStyle, ProgressCallback callback)
    throws IOException, SQLException
  {
    Database db = Database.open(new File(inputFile), true);
    Set<String> tableNames = db.getTableNames();
    long i = 1L;
    writer.writeSql("DROP SCHEMA IF EXISTS mon CASCADE;");
    writer.writeSql("CREATE SCHEMA  mon ;");
    for (String tableName : tableNames)
    {
      callback.startTable(tableName, i++, tableNames.size());
      exportTable(writer, sqlMapper, nameMapper, db, tableName, dropStyle, callback);
      callback.endTable();
    }
  }
  
  private static void exportTable(SqlWriter writer, SqlMapper sqlMapper, NameMapper nameMapper, Database db, String tableName, DropStyle dropStyle, ProgressCallback callback)
    throws IOException, SQLException
  {
    switch (dropStyle)
    {
    case DROP_IF_EXISTS: 
      writer.writeSql("DROP TABLE  \"mon\".\"" + nameMapper.getTableName(tableName) + "\";");
      break;
    case NO_DROP: 
      writer.writeSql("DROP TABLE IF EXISTS \"mon." + nameMapper.getTableName(tableName) + "\";");
    }
    Table table = db.getTable(tableName);
    writeCreateTableStatement(writer, sqlMapper, nameMapper, table);
    writeInsertIntoStatements(writer, nameMapper, table, callback);
  }
  
  private static void writeInsertIntoStatements(SqlWriter writer, NameMapper nameMapper, Table table, ProgressCallback callback)
    throws IOException
  {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    


    long i = 1L;
    long count = table.getRowCount();
    Map<String, Object> row;
    while ((row = table.getNextRow()) != null)
    {
      //Map<String, Object> row;
      callback.startRow(i++, count);
      StringBuilder builder = new StringBuilder();
      builder.append("INSERT INTO \"mon\".\"" + nameMapper.getTableName(table.getName()) + "\" VALUES (");
      boolean first = true;
      for (Object value : row.values())
      {
        if (!first) {
          builder.append(",");
        } else {
          first = false;
        }
        if ((value instanceof String))
        {
          builder.append("'" + ((String)value).replace("'", "''") + "'");
        }
        else if ((value instanceof Date))
        {
          builder.append("'" + dateFormat.format(value) + "'");
        }
        else if ((value instanceof Byte))
        {
          byte v = ((Byte)value).byteValue();
          if (v < 0)
          {
            int r = v + 256;
            System.out.println(v + " sled -->" + (v + 256));
            builder.append(r);
          }
          else
          {
            builder.append(v);
          }
        }
        else
        {
          builder.append(value);
        }
      }
      builder.append(");");
      writer.writeSql(builder.toString());
      callback.endRow();
    }
  }
  
  private static String foldColumns(List<IndexData.ColumnDescriptor> columns)
  {
    boolean first = true;
    StringBuilder builder = new StringBuilder();
    for (IndexData.ColumnDescriptor columnDescriptor : columns)
    {
      if (!first) {
        builder.append(",");
      } else {
        first = false;
      }
      builder.append("\"" + columnDescriptor.getName() + "\"");
    }
    return builder.toString();
  }
  
  private static void writeCreateTableStatement(SqlWriter writer, SqlMapper sqlMapper, NameMapper nameMapper, Table table)
    throws SQLException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("CREATE TABLE \"mon\".\"" + nameMapper.getTableName(table.getName()) + "\" (\n");
    boolean first = true;
    for (Column col : table.getColumns())
    {
      if (!first) {
        builder.append(",\n");
      } else {
        first = false;
      }
      builder.append("    \"" + col.getName() + "\" " + sqlMapper.getTargetType(col.getType()));
    }
    for (Index index : table.getIndexes()) {
      if (index.isPrimaryKey())
      {
        builder.append(",\n");
        builder.append("    CONSTRAINT \"" + nameMapper.getTableName(table.getName()) + "_" + index.getName() + "\" PRIMARY KEY (" + 
          foldColumns(index.getColumns()) + ")");
      }
    }
    builder.append("\n);");
    writer.writeSql(builder.toString());
  }
}
