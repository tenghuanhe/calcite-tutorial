package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.jdbc.CalciteConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by tenghuanhe on 17-4-13.
 */
public class VolatileQueryDemo {
  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    if (args.length <= 0) {
      System.out.println("model file not found!");
      System.out.println("usage: VolatileQueryDemo model.json");
      return;
    }
    String model = args[0];
    Class.forName("org.apache.calcite.jdbc.Driver");
    Properties info = new Properties();
    Connection connection = DriverManager.getConnection("jdbc:calcite:model=" + model, info);
    CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
    Statement statement = calciteConnection.createStatement();
    ResultSet resultSet = statement.executeQuery(
        "SELECT \"employee\".\"deptNo\", \"employee\".\"gender\", \"department\".\"name\""
            + " FROM \"employee\", \"department\""
            + " WHERE \"employee\".\"gender\" = 'm' and \"department\".\"id\" = \"employee\".\"deptNo\"");
    while (resultSet.next()) {
      for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++) {
        System.out.print(resultSet.getObject(i) + "\t");
      }
      System.out.println();
    }
    resultSet.close();
  }

  /* Generated class for sql `SELECT "deptNo", "gender" FROM "employees" WHERE "gender" = 'm'`

  public org.apache.calcite.linq4j.Enumerable bind(final org.apache.calcite.DataContext root) {
    final org.apache.calcite.rel.RelNode v1stashed = (org.apache.calcite.rel.RelNode) root.get("v1stashed");
    final org.apache.calcite.interpreter.Interpreter interpreter = new org.apache.calcite.interpreter.Interpreter(
        root,
        v1stashed);
    return new org.apache.calcite.linq4j.AbstractEnumerable() {
      public org.apache.calcite.linq4j.Enumerator enumerator() {
        return new org.apache.calcite.linq4j.Enumerator() {
          public final org.apache.calcite.linq4j.Enumerator inputEnumerator = interpreter.enumerator();

          public void reset() {
            inputEnumerator.reset();
          }

          public boolean moveNext() {
            while (inputEnumerator.moveNext()) {
              final Object[] current = (Object[]) inputEnumerator.current();
              final String inp1_ = current[1] == null ? (String) null : current[1].toString();
              if (inp1_ != null && org.apache.calcite.runtime.SqlFunctions.eq(inp1_, "m")) {
                return true;
              }
            }
            return false;
          }

          public void close() {
            inputEnumerator.close();
          }

          public Object current() {
            final Object[] current = (Object[]) inputEnumerator.current();
            return new Object[]{
                current[3],
                current[1]
            };
          }

        };
      }

    };
  }
   */
}
