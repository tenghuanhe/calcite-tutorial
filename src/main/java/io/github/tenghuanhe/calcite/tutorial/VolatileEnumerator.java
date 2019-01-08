package io.github.tenghuanhe.calcite.tutorial;

import org.apache.calcite.linq4j.Enumerator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tenghuanhe on 17-4-13.
 */
public class VolatileEnumerator<E> implements Enumerator<E> {

  private List<List<String>> rows;
  private RowConverter<E> rowConverter;
  private int currentIndex = -1;
  private String[] filterValues;

  VolatileEnumerator(List<List<String>> rows, List<String> types) {
    this(rows, types, identityList(types.size()));
  }

  VolatileEnumerator(List<List<String>> rows, List<String> types, int[] fields) {
    //noinspection unchecked
    this(rows, null, (RowConverter<E>) converter(types, fields));
  }

  VolatileEnumerator(List<List<String>> rows, String[] filterValues, List<String> types, int[] fields) {
    //noinspection unchecked
    this(rows, filterValues, (RowConverter<E>) converter(types, fields));
  }

  VolatileEnumerator(List<List<String>> rows, String[] filterValues, RowConverter<E> rowConverter) {
    this.rows = rows;
    this.filterValues = filterValues;
    this.rowConverter = rowConverter;
  }

  private static RowConverter<?> converter(List<String> types, int[] fields) {
    return new ArrayRowConverter(types, fields);
  }

  /** Returns an array of integers {0, ..., n - 1}. */
  static int[] identityList(int n) {
    int[] integers = new int[n];
    for (int i = 0; i < n; i++) {
      integers[i] = i;
    }
    return integers;
  }

  private static Object convertSingleCellValue(String stringValue, String fieldType) {
    if (stringValue == null) {
      return null;
    }
    if ((stringValue.length() == 0 || stringValue.equals("\\N")) && !fieldType.equals("string")) {
      return null;
    }
    switch (fieldType) {
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
    List<String> row = rows.get(currentIndex);
    return rowConverter.convert(row);
  }

  public boolean moveNext() {
    if (filterValues == null) {
      return ++currentIndex < rows.size();
    }
outer:
    for (; ; ) {
      currentIndex += 1;
      if (currentIndex >= rows.size()) {
        return false;
      }
      List<String> row = rows.get(currentIndex);
      for (int i = 0; i < row.size(); i++) {
        String filterValue = filterValues[i];
        if (filterValue != null) {
          if (!filterValue.equals(row.get(i))) {
            continue outer;
          }
        }
      }
      return true;
    }
  }

  public void reset() {
    currentIndex = -1;
  }

  public void close() {

  }

  abstract static class RowConverter<E> {
    abstract E convert(List<String> row);
  }

  static class ArrayRowConverter extends RowConverter<Object[]> {
    private int[] fields;
    private String[] fieldTypes;

    ArrayRowConverter(List<String> fieldTypes, int[] fields) {
      this.fieldTypes = fieldTypes.toArray(new String[0]);
      this.fields = fields;
    }

    Object[] convert(List<String> row) {
      Object[] objects = new Object[fields.length];
      for (int i = 0; i < fields.length; i++) {
        int fieldIdx = fields[i];
        objects[i] = convertSingleCellValue(row.get(fieldIdx), fieldTypes[fieldIdx]);
      }
      return objects;
    }
  }
}
