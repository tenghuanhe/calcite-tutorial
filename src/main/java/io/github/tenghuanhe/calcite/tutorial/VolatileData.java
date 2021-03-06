package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.sql.type.SqlTypeName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tenghuanhe on 17-4-12.
 */
class VolatileData {

  static final Map<String, Database> DATABASE_MAP = new HashMap<>();
  static Map<String, Class> stringClassMap = new HashMap<>();
  static Map<String, SqlTypeName> sqlTypeNameMap = new HashMap<>();

  static {
    sqlTypeNameMap.put("char", SqlTypeName.CHAR);
    stringClassMap.put("char", Character.class);
    sqlTypeNameMap.put("varchar", SqlTypeName.VARCHAR);
    stringClassMap.put("varchar", String.class);
    sqlTypeNameMap.put("boolean", SqlTypeName.BOOLEAN);
    sqlTypeNameMap.put("integer", SqlTypeName.INTEGER);
    stringClassMap.put("integer", Integer.class);
    sqlTypeNameMap.put("tinyint", SqlTypeName.TINYINT);
    sqlTypeNameMap.put("smallint", SqlTypeName.SMALLINT);
    sqlTypeNameMap.put("bigint", SqlTypeName.BIGINT);
    sqlTypeNameMap.put("decimal", SqlTypeName.DECIMAL);
    sqlTypeNameMap.put("numeric", SqlTypeName.DECIMAL);
    sqlTypeNameMap.put("float", SqlTypeName.FLOAT);
    sqlTypeNameMap.put("real", SqlTypeName.REAL);
    sqlTypeNameMap.put("double", SqlTypeName.DOUBLE);
    sqlTypeNameMap.put("date", SqlTypeName.DATE);
    stringClassMap.put("date", Date.class);
    sqlTypeNameMap.put("time", SqlTypeName.TIME);
    sqlTypeNameMap.put("timestamp", SqlTypeName.TIMESTAMP);
    sqlTypeNameMap.put("any", SqlTypeName.ANY);

    Database company = new Database();
    Table employees = new Table();
    employees.tableName = "employee";
    employees.columns.add(new Column("id", "integer"));
    employees.columns.add(new Column("gender", "varchar"));
    employees.columns.add(new Column("name", "varchar"));
    employees.columns.add(new Column("deptNo", "integer"));

    employees.rows.add(Arrays.asList("1", "f", "alice", "1"));
    employees.rows.add(Arrays.asList("2", "m", "bob", "2"));
    employees.rows.add(Arrays.asList("3", "m", "tom", "1"));
    employees.rows.add(Arrays.asList("4", "m", "shaw", "2"));

    Table departments = new Table();
    departments.tableName = "department";
    departments.columns.add(new Column("id", "integer"));
    departments.columns.add(new Column("name", "varchar"));
    departments.rows.add(Arrays.asList("1", "sales"));
    departments.rows.add(Arrays.asList("2", "develop"));

    company.tables.add(employees);
    company.tables.add(departments);

    DATABASE_MAP.put("company", company);
  }

  static class Database {
    List<Table> tables = new ArrayList<>();
  }

  static class Table {
    String tableName;
    List<Column> columns = new ArrayList<>();
    List<List<String>> rows = new ArrayList<>();
  }

  static class Column {
    String name;
    String type;

    Column(String name, String type) {
      this.name = name;
      this.type = type;
    }
  }
}
