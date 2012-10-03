package de.jungblut.math.dense;

import junit.framework.TestCase;

import org.junit.Test;

import de.jungblut.math.DoubleMatrix;
import de.jungblut.math.DoubleVector;

public class DenseDoubleMatrixTest extends TestCase {

  @Test
  public void testAccessors() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    assertEquals(2, mat.getRowCount());
    assertEquals(3, mat.getColumnCount());
    for (int i = 0; i < mat.getRowCount(); i++) {
      for (int j = 0; j < mat.getColumnCount(); j++) {
        double d = mat.get(i, j);
        assertEquals(arr[i][j], d);
      }
    }

    double[] row = mat.getRow(0);
    DoubleVector rowVector = mat.getRowVector(0);
    arrayEquals(row, rowVector.toArray());
    for (int i = 0; i < row.length; i++) {
      assertEquals(arr[0][i], row[i]);
    }
    row = mat.getRow(1);
    rowVector = mat.getRowVector(1);
    arrayEquals(row, rowVector.toArray());
    for (int i = 0; i < row.length; i++) {
      assertEquals(arr[1][i], row[i]);
    }

    double[] col = mat.getColumn(0);
    DoubleVector colVector = mat.getColumnVector(0);
    arrayEquals(col, colVector.toArray());
    for (int i = 0; i < col.length; i++) {
      assertEquals(arr[i][0], col[i]);
    }
    col = mat.getColumn(1);
    colVector = mat.getColumnVector(1);
    arrayEquals(col, colVector.toArray());
    for (int i = 0; i < col.length; i++) {
      assertEquals(arr[i][1], col[i]);
    }

  }

  @Test
  public void testMultiplication() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    DoubleMatrix multiply = mat.multiply(mat.transpose());
    assertEquals(2, multiply.getRowCount());
    assertEquals(2, multiply.getColumnCount());

    assertEquals(14.0d, multiply.get(0, 0));
    assertEquals(32.0d, multiply.get(0, 1));
    assertEquals(32.0d, multiply.get(1, 0));
    assertEquals(77.0d, multiply.get(1, 1));

  }

  @Test
  public void testMultiplyElement() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    DoubleMatrix multiply = mat.multiplyElementWise(mat);
    assertEquals(2, multiply.getRowCount());
    assertEquals(3, multiply.getColumnCount());

    assertEquals(1.0d, multiply.get(0, 0));
    assertEquals(4.0d, multiply.get(0, 1));
    assertEquals(9.0d, multiply.get(0, 2));
    assertEquals(16.0d, multiply.get(1, 0));
    assertEquals(25.0d, multiply.get(1, 1));
    assertEquals(36.0d, multiply.get(1, 2));
  }

  @Test
  public void testDivide() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    DoubleMatrix multiply = mat.divide(mat);
    assertEquals(2, multiply.getRowCount());
    assertEquals(3, multiply.getColumnCount());

    assertEquals(1.0d, multiply.get(0, 0));
    assertEquals(1.0d, multiply.get(0, 1));
    assertEquals(1.0d, multiply.get(0, 2));
    assertEquals(1.0d, multiply.get(1, 0));
    assertEquals(1.0d, multiply.get(1, 1));
    assertEquals(1.0d, multiply.get(1, 2));
  }

  @Test
  public void testPow() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    DoubleMatrix multiply = mat.pow(2);
    assertEquals(2, multiply.getRowCount());
    assertEquals(3, multiply.getColumnCount());

    assertEquals(1.0d, multiply.get(0, 0));
    assertEquals(4.0d, multiply.get(0, 1));
    assertEquals(9.0d, multiply.get(0, 2));
    assertEquals(16.0d, multiply.get(1, 0));
    assertEquals(25.0d, multiply.get(1, 1));
    assertEquals(36.0d, multiply.get(1, 2));

    multiply = mat.pow(3);
    assertEquals(2, multiply.getRowCount());
    assertEquals(3, multiply.getColumnCount());

    assertEquals(1.0d, multiply.get(0, 0));
    assertEquals(8.0d, multiply.get(0, 1));
    assertEquals(27.0d, multiply.get(0, 2));
    assertEquals(64.0d, multiply.get(1, 0));
    assertEquals(125.0d, multiply.get(1, 1));
    assertEquals(216.0d, multiply.get(1, 2));
  }

  public void arrayEquals(double[] left, double[] right) {
    assertEquals(left.length, right.length);

    for (int i = 0; i < left.length; i++) {
      assertEquals(left[i], right[i]);
    }
  }
}
