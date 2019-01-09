package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.schema.QueryableTable;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Schemas;
import org.apache.calcite.schema.TranslatableTable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class VolatileTranslatableTable extends AbstractVolatileTable implements QueryableTable, TranslatableTable {
  VolatileTranslatableTable(final VolatileData.Table table) {
    super(table);
  }

  public Enumerable<Object> project(final DataContext root, final int[] fields) {
    final List<String> types = sourceTable.columns.stream().map(column -> column.type).collect(Collectors.toList());
    return new AbstractEnumerable<>() {
      public Enumerator<Object> enumerator() {
        //noinspection unchecked
        return new VolatileEnumerator(sourceTable.rows, null, types, fields);
      }
    };
  }

  public Expression getExpression(SchemaPlus schema, String tableName,
      Class clazz) {
    return Schemas.tableExpression(schema, getElementType(), tableName, clazz);
  }

  public Type getElementType() {
    return Object[].class;
  }

  public <T> Queryable<T> asQueryable(QueryProvider queryProvider,
      SchemaPlus schema, String tableName) {
    throw new UnsupportedOperationException();
  }

  @Override public RelNode toRel(final RelOptTable.ToRelContext context, final RelOptTable relOptTable) {
    final int fieldCount = relOptTable.getRowType().getFieldCount();
    final int[] fields = VolatileEnumerator.identityList(fieldCount);
    return new VolatileTableScan(context.getCluster(), relOptTable, this, fields);
  }
}
