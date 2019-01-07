package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tenghuanhe on 17-4-12.
 */
public class VolatileTable extends AbstractTable implements ScannableTable {
  private VolatileData.Table sourceTable;
  private RelDataType dataType;

  VolatileTable(VolatileData.Table table) {
    this.sourceTable = table;
  }

  public Enumerable<Object[]> scan(DataContext root) {
    final List<String> types = sourceTable.columns.stream().map(column -> column.type).collect(Collectors.toList());
    return new AbstractEnumerable<>() {
      public Enumerator<Object[]> enumerator() {
        //noinspection unchecked
        return new VolatileEnumerator(sourceTable.rows, types);
      }
    };
  }

  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    if (dataType == null) {
      List<RelDataType> typeList = new ArrayList<>();
      List<String> fieldNameList = new ArrayList<>();
      for (VolatileData.Column column : this.sourceTable.columns) {
        RelDataType sqlType = typeFactory.createJavaType(VolatileData.stringClassMap.get(column.type));
        sqlType = SqlTypeUtil.addCharsetAndCollation(sqlType, typeFactory);
        typeList.add(sqlType);
        fieldNameList.add(column.name);
      }
      this.dataType = typeFactory.createStructType(typeList, fieldNameList);
    }
    return this.dataType;
  }
}
