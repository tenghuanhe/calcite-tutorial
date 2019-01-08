package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tenghuanhe on 17-4-12.
 */
public class VolatileSchema extends AbstractSchema {
  private String schemaName;
  private String flavor;

  VolatileSchema(String schemaName) {
    this.schemaName = schemaName;
    this.flavor = System.getProperty("calcite.volatile.flavor");
  }

  @Override
  public Map<String, Table> getTableMap() {
    Map<String, Table> tables = new HashMap<>();
    VolatileData.Database database = VolatileData.DATABASE_MAP.get(this.schemaName);
    if (database == null) {
      return tables;
    }
    for (VolatileData.Table table : database.tables) {
      tables.put(table.tableName, createTable(table));
    }
    return tables;
  }

  private Table createTable(VolatileData.Table table) {
    switch (flavor) {
    case "scannable":
      return new VolatileScannableTable(table);
    case "filterable":
      return new VolatileFilterableTable(table);
    case "translatable":
      throw new AssertionError("Translatable flavor not supported yet");
    default:
      throw new AssertionError("Unknown flavor " + this.flavor);
    }
  }
}
