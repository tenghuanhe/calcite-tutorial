package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.linq4j.Enumerator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tenghuanhe on 17-4-13.
 */
public class VolatileEnumerator<E> implements Enumerator<E> {

  private List<List<String>> rows = null;
  private List<String> types;
  private RowConverter<E> rowConverter;
  private int currentIndex = -1;

  public VolatileEnumerator(int[] fields, List<String> types, List<List<String>> rows) {
    this.rows = rows;
    this.types = types;
    this.rowConverter = (RowConverter<E>) new ArrayRowConverter(fields);
  }

  public static Object convertOptiqCellValue(String stringValue, String dataType) {
    if (stringValue == null) {
      return null;
    }

    if ((stringValue.length() == 0 || stringValue.equals("\\N")) && !dataType.equals("string")) {
      return null;
    }

    switch (dataType) {
    case "tinyint":
      return Byte.valueOf(stringValue);
    case "short":
    case "smallint":
      return Short.valueOf(stringValue);
    case "integer":
      return Integer.valueOf(stringValue);
    case "long":
    case "bigint":
      return Long.valueOf(stringValue);
    case "double":
      return Double.valueOf(stringValue);
    case "decimal":
      return new BigDecimal(stringValue);
    case "float":
      return Float.valueOf(stringValue);
    case "boolean":
      return Boolean.valueOf(stringValue);
    default:
      return stringValue;

    }
  }

  public E current() {
    List<String> row = rows.get(++currentIndex);
    return rowConverter.convert(row, this.types);
  }

  public boolean moveNext() {
    return ++currentIndex < rows.size();
  }

  public void reset() {
    currentIndex = -1;
  }

  public void close() {

  }

  abstract static class RowConverter<E> {
    abstract E convert(List<String> row, List<String> columnTypes);
  }

  static class ArrayRowConverter extends RowConverter<Object[]> {
    private int[] fields;

    public ArrayRowConverter(int[] fields) {
      this.fields = fields;
    }

    Object[] convert(List<String> row, List<String> columnTypes) {
      Object[] objects = new Object[fields.length];
      for (int i = 0; i < fields.length; i++) {
        objects[i] = convertOptiqCellValue(row.get(i), columnTypes.get(i));
      }

      return objects;
    }
  }
}
