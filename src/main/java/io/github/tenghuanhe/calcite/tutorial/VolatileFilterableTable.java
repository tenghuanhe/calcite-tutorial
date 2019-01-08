package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.FilterableTable;
import org.apache.calcite.sql.SqlKind;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tenghuanhe on 19-1-8.
 *
 * push down filter to scan
 */
public class VolatileFilterableTable extends AbstractVolatileTable implements FilterableTable {
  VolatileFilterableTable(VolatileData.Table table) {
    super(table);
  }

  @Override public Enumerable<Object[]> scan(final DataContext root, final List<RexNode> filters) {
    final List<String> types = sourceTable.columns.stream().map(column -> column.type).collect(Collectors.toList());
    // TODO: use other info
    final int colCnt = this.sourceTable.columns.size();
    final String[] filterValues = new String[colCnt];
    filters.removeIf(filter -> addFilter(filter, filterValues));
    final int[] fields = VolatileEnumerator.identityList(types.size());
    return new AbstractEnumerable<>() {
      public Enumerator<Object[]> enumerator() {
        //noinspection unchecked
        return new VolatileEnumerator(sourceTable.rows, filterValues, types, fields);
      }
    };
  }

  private boolean addFilter(RexNode filter, Object[] filterValues) {
    if (filter.isA(SqlKind.EQUALS)) {
      final RexCall call = (RexCall) filter;
      RexNode left = call.getOperands().get(0);
      if (left.isA(SqlKind.CAST)) {
        left = ((RexCall) left).getOperands().get(0);
      }
      final RexNode right = call.getOperands().get(1);
      if (left instanceof RexInputRef
          && right instanceof RexLiteral) {
        final int index = ((RexInputRef) left).getIndex();
        if (filterValues[index] == null) {
          filterValues[index] = ((RexLiteral) right).getValue2().toString();
          return true;
        }
      }
    }
    return false;
  }
}
