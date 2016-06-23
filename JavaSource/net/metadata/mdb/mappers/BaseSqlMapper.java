package net.metadata.mdb.mappers;

import com.healthmarketscience.jackcess.DataType;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class BaseSqlMapper
  implements SqlMapper
{
  private static final Map<Integer, String> SQL_TYPES = new HashMap() {};
  
  public String getTargetType(DataType type)
    throws SQLException
  {
    return (String)SQL_TYPES.get(Integer.valueOf(type.getSQLType()));
  }
}
