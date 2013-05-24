package de.jungblut.math.dense;

import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.common.collect.Lists;

import de.jungblut.math.DoubleMatrix;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.sparse.SparseDoubleVector;

public class DenseDoubleMatrixTest extends TestCase {

  @Test
  public void testConstructor() {

    DoubleMatrix mat = new DenseDoubleMatrix(3, 2);
    assertEquals(3, mat.getRowCount());
    assertEquals(2, mat.getColumnCount());
    assertEquals(0d, mat.get(0, 0));

    mat = new DenseDoubleMatrix(3, 2, 1d);
    assertEquals(3, mat.getRowCount());
    assertEquals(2, mat.getColumnCount());
    assertEquals(1d, mat.get(0, 0));

    mat = new DenseDoubleMatrix(3, 2, new Random(1L));
    assertEquals(3, mat.getRowCount());
    assertEquals(2, mat.getColumnCount());
    assertEquals(0.73087, mat.get(0, 0), 1e-5);

    List<DoubleVector> rows = Lists.newArrayList(
        (DoubleVector) new DenseDoubleVector(new double[] { 1, 2 }),
        (DoubleVector) new DenseDoubleVector(new double[] { 3, 4 }),
        (DoubleVector) new DenseDoubleVector(new double[] { 5, 6 }));
    double[][] result = new double[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } };
    mat = new DenseDoubleMatrix(rows);
    assertEquals(3, mat.getRowCount());
    assertEquals(2, mat.getColumnCount());
    matrixEquals(result, ((DenseDoubleMatrix) mat).getValues());

    DoubleVector[] array = rows.toArray(new DoubleVector[rows.size()]);
    mat = new DenseDoubleMatrix(array);
    assertEquals(3, mat.getRowCount());
    assertEquals(2, mat.getColumnCount());
    matrixEquals(result, ((DenseDoubleMatrix) mat).getValues());

    mat = new DenseDoubleMatrix(2, 2);

    DenseDoubleMatrix wBias = new DenseDoubleMatrix(DenseDoubleVector.ones(2),
        mat);
    assertEquals(2, wBias.getRowCount());
    assertEquals(3, wBias.getColumnCount());
    arrayEquals(wBias.getColumnVector(0).toArray(), DenseDoubleVector.ones(2)
        .toArray());
    arrayEquals(wBias.getColumnVector(1).toArray(), DenseDoubleVector.zeros(2)
        .toArray());
    arrayEquals(wBias.getColumnVector(2).toArray(), DenseDoubleVector.zeros(2)
        .toArray());

    double[] data = new double[] { 2, 3, 4, 5, 6, 7 };
    mat = new DenseDoubleMatrix(data, 3, 2);
    result = new double[][] { { 2d, 5d }, { 3d, 6d }, { 4d, 7d } };
    matrixEquals(result, ((DenseDoubleMatrix) mat).getValues());
  }

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

    mat.setRowVector(1, new DenseDoubleVector(new double[] { 2, 3, 4 }));
    arrayEquals(mat.getValues()[0], arr[0]);

    mat.setColumnVector(1, new DenseDoubleVector(new double[] { 2, 4 }));
    arrayEquals(mat.getColumnVector(0).toArray(), new double[] { 1, 2 });
    arrayEquals(mat.getColumnVector(1).toArray(), new double[] { 2, 4 });

    assertFalse(mat.isSparse());
  }

  @Test
  public void testMultiplication() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    // normal multiply
    DoubleMatrix multiply = mat.multiply(mat.transpose());
    assertEquals(2, multiply.getRowCount());
    assertEquals(2, multiply.getColumnCount());

    assertEquals(14d, multiply.get(0, 0));
    assertEquals(32d, multiply.get(0, 1));
    assertEquals(32d, multiply.get(1, 0));
    assertEquals(77d, multiply.get(1, 1));

    // multiply scalar
    DoubleMatrix multiply2 = multiply.multiply(2);
    assertEquals(2, multiply2.getRowCount());
    assertEquals(2, multiply2.getColumnCount());

    assertEquals(28d, multiply2.get(0, 0));
    assertEquals(64d, multiply2.get(0, 1));
    assertEquals(64d, multiply2.get(1, 0));
    assertEquals(154d, multiply2.get(1, 1));

    // now multiply some larger matrices
    mat = new DenseDoubleMatrix(101, 101, 6d);
    DenseDoubleMatrix mat2 = new DenseDoubleMatrix(101, 101, 3d);
    DoubleMatrix multiply3 = mat.multiply(mat2);
    assertEquals(101, multiply3.getRowCount());
    assertEquals(101, multiply3.getColumnCount());
    assertEquals(1818d, multiply3.get(0, 0));
    assertEquals(1818d, multiply3.get(100, 100));

    // multiply with column vectors
    mat = new DenseDoubleMatrix(arr);
    DoubleVector multiplyVectorColumn = mat
        .multiplyVectorColumn(new DenseDoubleVector(new double[] { 2d, 2d }));
    double[] result = new double[] { 10d, 14d, 18d };
    arrayEquals(result, multiplyVectorColumn.toArray());
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
  public void testMultiplyVector() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleVector vec = new DenseDoubleVector(new double[] { 2, 0, 2 });
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    DoubleVector multiply = mat.multiplyVector(vec);
    assertEquals(8d, multiply.get(0));
    assertEquals(20d, multiply.get(1));

    multiply = mat.multiplyVector(new SparseDoubleVector(vec));
    assertEquals(8d, multiply.get(0));
    assertEquals(20d, multiply.get(1));
  }

  @Test
  public void testDivide() throws Exception {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    double[][] result = new double[][] { { 1d, 1d, 1d }, { 1d, 1d, 1d } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    DenseDoubleMatrix multiply = (DenseDoubleMatrix) mat.divide(mat);
    matrixEquals(multiply.getValues(), result);

    multiply = (DenseDoubleMatrix) mat.divide(new DenseDoubleVector(
        new double[] { 2d, 2d }));
    result = new double[][] { { 0.5d, 1d, 1.5d }, { 2d, 2.5d, 3d } };
    matrixEquals(multiply.getValues(), result);

    multiply = (DenseDoubleMatrix) mat.divide(2d);
    matrixEquals(multiply.getValues(), result);

  }

  @Test
  public void testSubtraction() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);

    DoubleMatrix subtract = mat.subtract(2d);
    double[][] result = new double[][] { { -1, 0, 1 }, { 2, 3, 4 } };
    matrixEquals(result, ((DenseDoubleMatrix) subtract).getValues());

    subtract = mat.subtractBy(2d);
    result = new double[][] { { 1, 0, -1 }, { -2, -3, -4 } };
    matrixEquals(result, ((DenseDoubleMatrix) subtract).getValues());

    double[][] arr2 = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat2 = new DenseDoubleMatrix(arr2);
    result = new double[][] { { 0, 0, 0 }, { 0, 0, 0 } };
    subtract = mat.subtract(mat2);
    matrixEquals(result, ((DenseDoubleMatrix) subtract).getValues());

    DoubleVector column = new DenseDoubleVector(new double[] { 1, 1 });
    subtract = mat.subtract(column);
    result = new double[][] { { 0, 1, 2 }, { 3, 4, 5 } };
    matrixEquals(result, ((DenseDoubleMatrix) subtract).getValues());

  }

  @Test
  public void testAddition() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    double[][] result = new double[][] { { 2, 4, 6 }, { 8, 10, 12 } };
    DenseDoubleMatrix add = (DenseDoubleMatrix) mat.add(mat);
    matrixEquals(result, add.getValues());

  }

  @Test
  public void testMin() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    assertEquals(1d, mat.min(0));
    assertEquals(2d, mat.min(1));
    assertEquals(3d, mat.min(2));
  }

  @Test
  public void testMax() {
    double[][] arr = new double[][] { { 1, 5, 3 }, { 4, 2, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    assertEquals(4d, mat.max(0));
    assertEquals(5d, mat.max(1));
    assertEquals(6d, mat.max(2));
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

  @Test
  public void testSlicing() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    DoubleMatrix slice = mat.slice(1, 2);
    assertEquals(1, slice.getRowCount());
    assertEquals(2, slice.getColumnCount());
  }

  @Test
  public void testSum() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    double sum = mat.sum();
    assertEquals(21d, sum, 1e-5);
  }

  @Test
  public void testIndices() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    int[] columnIndices = mat.columnIndices();
    assertEquals(3, columnIndices.length);
    assertEquals(0, columnIndices[0]);
    assertEquals(1, columnIndices[1]);
    assertEquals(2, columnIndices[2]);

    int[] rowIndices = mat.rowIndices();
    assertEquals(2, rowIndices.length);
    assertEquals(0, rowIndices[0]);
    assertEquals(1, rowIndices[1]);
  }

  @Test
  public void testDeepcopy() {
    double[][] arr = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    DenseDoubleMatrix mat = new DenseDoubleMatrix(arr);
    DoubleMatrix deepCopy = mat.deepCopy();
    assertNotSame(mat, deepCopy);
    assertNotSame(arr, ((DenseDoubleMatrix) deepCopy).getValues());
  }

  @Test
  public void testToString() {

    DenseDoubleMatrix mat = new DenseDoubleMatrix(new double[][] { { 1, 2 },
        { 3, 4 } });
    assertEquals("[1.0, 2.0]\n[3.0, 4.0]\n", mat.toString());

    mat = new DenseDoubleMatrix(100, 102);
    assertEquals("100x102", mat.toString());

  }

  @Test
  public void testEye() {
    DenseDoubleMatrix eye = DenseDoubleMatrix.eye(5);
    double[][] result = new double[][] { { 1, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0 },
        { 0, 0, 1, 0, 0 }, { 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 1 } };

    matrixEquals(result, eye.getValues());
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
      assertEquals(left[i], right[i], 1e-5);
    }
  }
}
