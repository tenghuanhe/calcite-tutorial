package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

import java.util.Map;

/**
 * Created by tenghuanhe on 17-4-12.
 */
public class VolatileSchemaFactory implements SchemaFactory {
  public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
    System.out.println(name);
    return new VolatileSchema(name);
  }
}
