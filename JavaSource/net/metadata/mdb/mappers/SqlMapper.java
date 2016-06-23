package net.metadata.mdb.mappers;

import java.sql.SQLException;

import com.healthmarketscience.jackcess.DataType;


public abstract interface SqlMapper
{
  public abstract String getTargetType(DataType paramDataType)
    throws SQLException;
}
