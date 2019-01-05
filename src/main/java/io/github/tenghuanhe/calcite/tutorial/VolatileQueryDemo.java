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

    System.out.println(calciteConnection.getRootSchema().getTableNames());
    ResultSet resultSet = calciteConnection.getMetaData().getTables(null, null, null, null);
    while (resultSet.next()) {
      System.out.println(resultSet.getString(1));
      System.out.println(resultSet.getString(2));
      System.out.println(resultSet.getString(3));
    }
    resultSet.close();

    Statement statement = calciteConnection.createStatement();
    resultSet = statement.executeQuery("SELECT * FROM \"departments\"");
    while (resultSet.next()) {
      for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++) {
        System.out.print(resultSet.getObject(i) + "\t");
      }
      System.out.println();
    }
    resultSet.close();
  }
}