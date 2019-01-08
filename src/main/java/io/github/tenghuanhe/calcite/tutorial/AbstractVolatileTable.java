package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tenghuanhe on 19-1-9.
 *
 * push down filter to scan
 */
public abstract class AbstractVolatileTable extends AbstractTable {
  protected VolatileData.Table sourceTable;

  AbstractVolatileTable(VolatileData.Table table) {
    this.sourceTable = table;
  }

  public RelDataType getRowType(final RelDataTypeFactory typeFactory) {
    List<RelDataType> typeList = new ArrayList<>();
    List<String> fieldNameList = new ArrayList<>();
    for (VolatileData.Column column : this.sourceTable.columns) {
      RelDataType sqlType = typeFactory.createJavaType(VolatileData.stringClassMap.get(column.type));
      sqlType = SqlTypeUtil.addCharsetAndCollation(sqlType, typeFactory);
      typeList.add(sqlType);
      fieldNameList.add(column.name);
    }
    return typeFactory.createStructType(typeList, fieldNameList);
  }
}
