package net.metadata.mdb.mappers;

import com.healthmarketscience.jackcess.DataType;
import java.sql.SQLException;

public class PostgreSqlMapper
  extends BaseSqlMapper
{
  public String getTargetType(DataType type)
    throws SQLException
  {
    if (type.getSQLType() == 8) {
      return "FLOAT";
    }
    if (type.getSQLType() == -1) {
      return "TEXT";
    }
    if (type.getSQLType() == -6) {
      return "SMALLINT";
    }
    return super.getTargetType(type);
  }
}
