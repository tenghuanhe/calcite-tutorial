package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.tools.RelBuilderFactory;

import java.util.List;

public class VolatileProjectTableScanRule extends RelOptRule {
  public static final VolatileProjectTableScanRule INSTANCE =
      new VolatileProjectTableScanRule(RelFactories.LOGICAL_BUILDER);

  public VolatileProjectTableScanRule(RelBuilderFactory relBuilderFactory) {
    super(operand(LogicalProject.class,
        operand(VolatileTableScan.class, none())),
        relBuilderFactory,
        "VolatileProjectTableScanRule");
  }

  @Override public void onMatch(final RelOptRuleCall call) {
    final LogicalProject project = call.rel(0);
    final VolatileTableScan scan = call.rel(1);
    int[] fields = getProjectFields(project.getProjects());
    if (fields == null) {
      return;
    }
    call.transformTo(
        new VolatileTableScan(
            scan.getCluster(),
            scan.getTable(),
            scan.volatileTable,
            fields));
  }

  private int[] getProjectFields(List<RexNode> exps) {
    final int[] fields = new int[exps.size()];
    for (int i = 0; i < exps.size(); i++) {
      final RexNode exp = exps.get(i);
      if (exp instanceof RexInputRef) {
        fields[i] = ((RexInputRef) exp).getIndex();
      } else {
        return null; // not a simple projection
      }
    }
    return fields;
  }
}
