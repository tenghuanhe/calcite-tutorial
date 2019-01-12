package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.adapter.enumerable.PhysType;
import org.apache.calcite.adapter.enumerable.PhysTypeImpl;
import org.apache.calcite.linq4j.tree.Blocks;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.Primitive;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;

import java.util.List;

public class VolatileTableScan extends TableScan implements EnumerableRel {
  final VolatileTranslatableTable volatileTable;
  final int[] fields;

  protected VolatileTableScan(final RelOptCluster cluster, final RelOptTable table,
      VolatileTranslatableTable volatileTable, int[] fields) {
    super(cluster, cluster.traitSetOf(EnumerableConvention.INSTANCE), table);
    this.volatileTable = volatileTable;
    this.fields = fields;
    assert volatileTable != null;
  }

  @Override public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
    assert inputs.isEmpty();
    return new VolatileTableScan(getCluster(), table, volatileTable, fields);
  }

  @Override public RelDataType deriveRowType() {
    final List<RelDataTypeField> fieldList = table.getRowType().getFieldList();
    final RelDataTypeFactory.Builder builder = getCluster().getTypeFactory().builder();
    for (int field : fields) {
      builder.add(fieldList.get(field));
    }
    return builder.build();
  }

  @Override public void register(RelOptPlanner planner) {
    planner.addRule(VolatileProjectTableScanRule.INSTANCE);
  }

  @Override public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery query) {
    return super.computeSelfCost(planner, query).multiplyBy(
        ((double) fields.length + 2D) / ((double) table.getRowType().getFieldCount() + 2D)
    );
  }

  @Override
  public RelWriter explainTerms(RelWriter rw) {
    return super.explainTerms(rw).item("fields", Primitive.asList(fields));
  }

  @Override public Result implement(final EnumerableRelImplementor implementor, final Prefer pref) {
    PhysType physType =
        PhysTypeImpl.of(
            implementor.getTypeFactory(),
            getRowType(),
            pref.preferArray()
        );
    return implementor.result(
        physType,
        // Expression meta-program for calling the VolatileTranslatableTable#project
        // method form the generated code
        Blocks.toBlock(
            Expressions.call(table.getExpression(VolatileTranslatableTable.class),
                "project", implementor.getRootExpression(),
                Expressions.constant(fields))));
  }
}
