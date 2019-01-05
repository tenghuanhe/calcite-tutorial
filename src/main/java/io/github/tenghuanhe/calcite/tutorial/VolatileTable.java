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

/**
 * Created by tenghuanhe on 17-4-12.
 */
public class VolatileTable extends AbstractTable implements ScannableTable {
  private VolatileData.Table sourceTable;
  private RelDataType dataType;

  public VolatileTable(VolatileData.Table table) {
    this.sourceTable = table;
  }

  private static int[] toArray(int n) {
    int[] array = new int[n];
    for (int i = 0; i < n; i++) {
      array[i] = i;
    }
    return array;
  }

  public Enumerable<Object[]> scan(DataContext root) {
    final List<String> types = new ArrayList<String>(sourceTable.columns.size());
    for (VolatileData.Column column : sourceTable.columns) {
      types.add(column.type);
    }
    final int[] fields = toArray(this.dataType.getFieldCount());

    return new AbstractEnumerable<Object[]>() {
      public Enumerator<Object[]> enumerator() {
        return null;
      }
    };
  }

  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    if (dataType == null) {
      RelDataTypeFactory.FieldInfoBuilder fieldInfoBuilder = typeFactory.builder();
      for (VolatileData.Column column : this.sourceTable.columns) {
        RelDataType sqlType = typeFactory.createJavaType(VolatileData.stringClassMap.get(column.type));
        sqlType = SqlTypeUtil.addCharsetAndCollation(sqlType, typeFactory);
        fieldInfoBuilder.add(column.name, sqlType);
      }

      this.dataType = typeFactory.createStructType(fieldInfoBuilder);
    }
    return this.dataType;
  }
}
