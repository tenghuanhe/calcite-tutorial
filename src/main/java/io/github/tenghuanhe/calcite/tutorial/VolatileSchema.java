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

  public VolatileSchema(String schemaName) {
    this.schemaName = schemaName;
  }

  @Override
  public Map<String, Table> getTableMap() {
    Map<String, Table> tables = new HashMap<>();
    VolatileData.Database database = VolatileData.DATABASE_MAP.get(this.schemaName);
    if (database == null) {
      return tables;
    }
    for (VolatileData.Table table : database.tables) {
      tables.put(table.tableName, new VolatileTable(table));
    }
    return tables;
  }
}
