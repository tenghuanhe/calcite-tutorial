package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.schema.ScannableTable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tenghuanhe on 17-4-12.
 */
public class VolatileScannableTable extends AbstractVolatileTable implements ScannableTable {
  VolatileScannableTable(VolatileData.Table table) {
    super(table);
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
}
