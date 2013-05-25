package de.jungblut.math.sparse;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.common.collect.Lists;

import de.jungblut.math.DoubleMatrix;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleMatrix;
import de.jungblut.math.dense.DenseDoubleVector;

public class SparseDoubleRowMatrixTest extends TestCase {

  @Test
  public void testConstructor() {

    DoubleMatrix mat = new SparseDoubleRowMatrix(3, 2);
    assertEquals(3, mat.getRowCount());
    assertEquals(2, mat.getColumnCount());
    assertEquals(0d, mat.get(0, 0));

    List<DoubleVector> rows = Lists.newArrayList(
        (DoubleVector) new DenseDoubleVector(new double[] { 1, 2 }),
        (DoubleVector) new DenseDoubleVector(new double[] { 3, 4 }),
        (DoubleVector) new DenseDoubleVector(new double[] { 5, 6 }));
    double[][] result = new double[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } };
    mat = new SparseDoubleRowMatrix(rows);
    assertEquals(3, mat.getRowCount());
    assertEquals(2, mat.getColumnCount());
    matrixEquals(result, mat.toArray());

    DoubleVector[] array = rows.toArray(new DoubleVector[rows.size()]);
    mat = new SparseDoubleRowMatrix(array);
    assertEquals(3, mat.getRowCount());
    assertEquals(2, mat.getColumnCount());
    matrixEquals(result, mat.toArray());

    mat = new SparseDoubleRowMatrix(2, 2);

    DoubleMatrix wBias = new SparseDoubleRowMatrix(DenseDoubleVector.ones(2),
        mat);
    assertEquals(2, wBias.getRowCount());
    assertEquals(3, wBias.getColumnCount());
    arrayEquals(wBias.getColumnVector(0).toArray(), DenseDoubleVector.ones(2)
        .toArray());
    arrayEquals(wBias.getColumnVector(1).toArray(), DenseDoubleVector.zeros(2)
        .toArray());
    arrayEquals(wBias.getColumnVector(2).toArray(), DenseDoubleVector.zeros(2)
        .toArray());
  }

  @Test
  public void testAccessors() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    assertEquals(2, mat.getRowCount());
    assertEquals(3, mat.getColumnCount());
    for (int i = 0; i < mat.getRowCount(); i++) {
      for (int j = 0; j < mat.getColumnCount(); j++) {
        double d = mat.get(i, j);
        assertEquals(arr[i][j], d);
      }
    }

    double[] row = new double[] { 1, 2, 3 };
    DoubleVector rowVector = mat.getRowVector(0);
    arrayEquals(row, rowVector.toArray());
    for (int i = 0; i < row.length; i++) {
      assertEquals(arr[0][i], row[i]);
    }
    row = new double[] { 4, 5, 6 };
    rowVector = mat.getRowVector(1);
    arrayEquals(row, rowVector.toArray());
    for (int i = 0; i < row.length; i++) {
      assertEquals(arr[1][i], row[i]);
    }

    double[] col = new double[] { 1, 4 };
    DoubleVector colVector = mat.getColumnVector(0);
    arrayEquals(col, colVector.toArray());
    for (int i = 0; i < col.length; i++) {
      assertEquals(arr[i][0], col[i]);
    }
    col = new double[] { 2, 5 };
    colVector = mat.getColumnVector(1);
    arrayEquals(col, colVector.toArray());
    for (int i = 0; i < col.length; i++) {
      assertEquals(arr[i][1], col[i]);
    }

    assertTrue(mat.isSparse());
  }

  @Test
  public void testMultiplication() throws Exception {
    double[][] arr = new double[][] { { 0, 2, 3 }, { 3, 0, 6 } };
    double[][] result = new double[][] { { 13, 18d }, { 18d, 45d } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    DoubleMatrix multiply = mat.multiply(mat.transpose());
    matrixEquals(result, multiply.toArray());

    double[][] arr2 = new double[][] { { 1, 0d }, { 2d, 5d }, { 0d, 6d } };
    result = new double[][] { { 4d, 28d }, { 3, 36d } };
    DoubleMatrix mat2 = new SparseDoubleRowMatrix(arr2);
    multiply = mat.multiply(mat2);
    matrixEquals(result, multiply.toArray());

  }

  @Test
  public void testMultiplyVector() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };

    // multiply with column vectors
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    DoubleVector multiplyVectorColumn = mat
        .multiplyVectorColumn(new DenseDoubleVector(new double[] { 2d, 2d }));
    double[] resultVec = new double[] { 10d, 14d, 18d };
    arrayEquals(resultVec, multiplyVectorColumn.toArray());

    DenseDoubleVector vec = new DenseDoubleVector(new double[] { 2, 0, 2 });
    DoubleVector multiply = mat.multiplyVectorRow(vec);
    assertEquals(8d, multiply.get(0));
    assertEquals(20d, multiply.get(1));

    multiply = mat.multiplyVectorRow(new SparseDoubleVector(vec));
    assertEquals(8d, multiply.get(0));
    assertEquals(20d, multiply.get(1));
  }

  @Test
  public void testMultiplyElement() throws Exception {
    double[][] arr = new double[][] { { 1, 0, 3 }, { 4, 5, 6 } };
    double[][] arr2 = new double[][] { { 1, 2, 0 }, { 4, 5, 6 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    SparseDoubleRowMatrix mat2 = new SparseDoubleRowMatrix(arr2);
    DoubleMatrix multiply = mat.multiplyElementWise(mat2);
    assertEquals(2, multiply.getRowCount());
    assertEquals(3, multiply.getColumnCount());

    assertEquals(1.0d, multiply.get(0, 0));
    assertEquals(0d, multiply.get(0, 1));
    assertEquals(0d, multiply.get(0, 2));
    assertEquals(16.0d, multiply.get(1, 0));
    assertEquals(25.0d, multiply.get(1, 1));
    assertEquals(36.0d, multiply.get(1, 2));
  }

  @Test
  public void testMultiplyScalar() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 0 }, { 4, 0, 6 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    DoubleMatrix multiply = mat.multiply(2);
    assertEquals(2, multiply.getRowCount());
    assertEquals(3, multiply.getColumnCount());

    assertEquals(2.0d, multiply.get(0, 0));
    assertEquals(4.0d, multiply.get(0, 1));
    assertEquals(0.0d, multiply.get(0, 2));
    assertEquals(8.0d, multiply.get(1, 0));
    assertEquals(0.0d, multiply.get(1, 1));
    assertEquals(12.0d, multiply.get(1, 2));
  }

  @Test
  public void testSlicing() {
    double[][] arr = new double[][] { { 0, 2, 0 }, { 4, 0, 6 } };
    double[][] res = new double[][] { { 0, 2 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    DoubleMatrix slice = mat.slice(1, 2);
    assertEquals(1, slice.getRowCount());
    assertEquals(2, slice.getColumnCount());
    matrixEquals(res, slice.toArray());
  }

  @Test
  public void testMin() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 0, 5, 6 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    // note we measure min and max on non-default elements.
    assertEquals(1d, mat.min(0));
    assertEquals(2d, mat.min(1));
    assertEquals(3d, mat.min(2));
  }

  @Test
  public void testMax() {
    double[][] arr = new double[][] { { 1, -5, 3 }, { 4, 0, 6 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    assertEquals(4d, mat.max(0));
    assertEquals(0d, mat.max(1));
    assertEquals(6d, mat.max(2));
  }

  @Test
  public void testSum() {
    double[][] arr = new double[][] { { 1, -5, 3 }, { 4, 0, 6 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    double sum = mat.sum();
    assertEquals(9d, sum, 1e-5);
  }

  @Test
  public void testDivide() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    double[][] result = new double[][] { { 1d, 1d, 1d }, { 1d, 1d, 1d } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    DoubleMatrix multiply = mat.divide(mat);
    matrixEquals(multiply.toArray(), result);

    multiply = mat.divide(new DenseDoubleVector(new double[] { 2d, 2d }));
    result = new double[][] { { 0.5d, 1d, 1.5d }, { 2d, 2.5d, 3d } };
    matrixEquals(multiply.toArray(), result);

    multiply = mat.divide(2d);
    matrixEquals(multiply.toArray(), result);
  }

  @Test
  public void testAddition() {
    double[][] arr = new double[][] { { 1, 0, 3 }, { 4, 5, 0 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
    double[][] result = new double[][] { { 2, 0, 6 }, { 8, 10, 0 } };
    DoubleMatrix add = mat.add(mat);
    matrixEquals(result, add.toArray());

  }

  @Test
  public void testPow() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);
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

  @Test
  public void testSubtract() throws Exception {
    SparseDoubleRowMatrix mat1 = new SparseDoubleRowMatrix(
        new DenseDoubleMatrix(new double[][] { { 1, 0, 3 }, { 0, 0, 0 },
            { 1, 0, 0 } }));

    SparseDoubleRowMatrix mat2 = new SparseDoubleRowMatrix(
        new DenseDoubleMatrix(new double[][] { { 1, 0, 3 }, { 0, 0, 0 },
            { 1, 0, 0 } }));

    assertEquals(0.0, mat1.subtract(mat2).sum(), 1e-2);

    double[][] arr = new double[][] { { 1, 0, 3 }, { 0, 5, 6 } };
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(arr);

    DoubleMatrix subtract = mat.subtract(2d);
    double[][] result = new double[][] { { -1, -2, 1 }, { -2, 3, 4 } };
    matrixEquals(result, subtract.toArray());

    subtract = mat.subtractBy(2d);
    result = new double[][] { { 1, 2, -1 }, { 2, -3, -4 } };
    matrixEquals(result, subtract.toArray());

    double[][] arr2 = new double[][] { { 1, 2, 0 }, { 4, 5, 0 } };
    mat2 = new SparseDoubleRowMatrix(arr2);
    result = new double[][] { { 0, -2, 3 }, { -4, 0, 6 } };
    subtract = mat.subtract(mat2);
    matrixEquals(result, subtract.toArray());

    DoubleVector column = new DenseDoubleVector(new double[] { 1, 1 });
    subtract = mat.subtract(column);
    result = new double[][] { { 0, -1, 2 }, { -1, 4, 5 } };
    matrixEquals(result, subtract.toArray());

  }

  @Test
  public void testDeepCopy() {
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(new double[][] {
        { 1, 0 }, { 0, 4 } });
    DoubleMatrix deepCopy = mat.deepCopy();
    assertNotSame(mat, deepCopy);
    matrixEquals(deepCopy.toArray(), mat.toArray());
  }

  @Test
  public void testToString() {
    SparseDoubleRowMatrix mat = new SparseDoubleRowMatrix(new double[][] {
        { 1, 2 }, { 3, 4 } });
    assertEquals("{1={1=4.0, 0=3.0},0={1=2.0, 0=1.0}}", mat.toString());

    mat = new SparseDoubleRowMatrix(100, 102);
    assertEquals("100x102", mat.toString());

  }

  public void matrixEquals(double[][] left, double[][] right) {
    assertEquals(left.length, right.length);
    for (int i = 0; i < left.length; i++) {
      arrayEquals(left[i], right[i]);
    }
  }

  public void arrayEquals(double[] left, double[] right) {
    assertEquals(left.length, right.length);

    for (int i = 0; i < left.length; i++) {
      assertEquals(left[i], right[i]);
    }
  }

}
