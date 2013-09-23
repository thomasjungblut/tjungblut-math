package de.jungblut.math.dense;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.FastMath;
import org.jblas.MyNativeBlasLibraryLoader;

import de.jungblut.math.DoubleMatrix;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.DoubleVector.DoubleVectorElement;

/**
 * Dense double matrix implementation. Internally a column major ordering is
 * used, just like in any other scientific language like FORTRAN or MATLAB. This
 * is especially better for Java in terms of memory usage, as every row of the
 * matrix has additional array/object overhead to deal with.
 */
public final class DenseDoubleMatrix implements DoubleMatrix {

  private static final int JBLAS_COLUMN_THRESHOLD = 100;
  private static final int JBLAS_ROW_THRESHOLD = 100;

  private static final boolean JBLAS_AVAILABLE;

  static {
    JBLAS_AVAILABLE = MyNativeBlasLibraryLoader.loadLibraryAndCheckErrors();
  }

  /**
   * We use a column major format to store the matrix, as two dimensional arrays
   * have a high waste of space.
   */
  private final double[] matrix;
  private final int numRows;
  private final int numColumns;

  /**
   * Creates a new empty matrix from the rows and columns.
   * 
   * @param rows the num of rows.
   * @param columns the num of columns.
   */
  public DenseDoubleMatrix(int rows, int columns) {
    this.numRows = rows;
    this.numColumns = columns;
    this.matrix = new double[columns * rows];
  }

  /**
   * Creates a new empty matrix from the rows and columns filled with the given
   * default value.
   * 
   * @param rows the num of rows.
   * @param columns the num of columns.
   * @param defaultValue the default value.
   */
  public DenseDoubleMatrix(int rows, int columns, double defaultValue) {
    this(rows, columns);
    Arrays.fill(matrix, defaultValue);
  }

  /**
   * Creates a new empty matrix from the rows and columns filled with the given
   * random values.
   * 
   * @param rows the num of rows.
   * @param columns the num of columns.
   * @param rand the random instance to use.
   */
  public DenseDoubleMatrix(int rows, int columns, Random rand) {
    this(rows, columns);
    for (int i = 0; i < matrix.length; i++) {
      matrix[i] = rand.nextDouble();
    }
  }

  /**
   * Simple copy constructor, does a deep copy of the given parameter.
   * 
   * @param otherMatrix the other matrix.
   */
  public DenseDoubleMatrix(double[][] otherMatrix) {
    this(otherMatrix.length, otherMatrix[0].length);
    for (int row = 0; row < otherMatrix.length; row++) {
      for (int col = 0; col < otherMatrix[row].length; col++) {
        set(row, col, otherMatrix[row][col]);
      }
    }
  }

  /**
   * Generates a matrix out of a vector list. it treats the entries as rows and
   * the vector itself contains the values of the columns.
   * 
   * @param vectorArray the list of vectors.
   */
  public DenseDoubleMatrix(List<DoubleVector> vec) {
    this(vec.size(), vec.get(0).getDimension());
    int row = 0;
    for (DoubleVector value : vec) {
      for (int col = 0; col < value.getDimension(); col++) {
        set(row, col, value.get(col));
      }
      row++;
    }
  }

  /**
   * Generates a matrix out of a vector array. It treats the array entries as
   * rows and the vector itself contains the values of the columns.
   * 
   * @param vectorArray the array of vectors.
   */
  public DenseDoubleMatrix(DoubleVector[] vectorArray) {
    this(Arrays.asList(vectorArray));
  }

  /**
   * Creates a new matrix with the given vector into the first column and the
   * other matrix to the other columns. This is usually used in machine learning
   * algorithms that add a bias on the zero-index column.
   * 
   * @param first the new first column.
   * @param otherMatrix the other matrix to set on from the second column.
   */
  public DenseDoubleMatrix(DenseDoubleVector first, DoubleMatrix otherMatrix) {
    this(otherMatrix.getRowCount(), otherMatrix.getColumnCount() + 1);
    // copy the first column
    System.arraycopy(first.toArray(), 0, matrix, 0, first.getDimension());

    int offset = first.getDimension();
    for (int col : otherMatrix.columnIndices()) {
      double[] clv = otherMatrix.getColumnVector(col).toArray();
      System.arraycopy(clv, 0, matrix, offset, clv.length);
      offset += clv.length;
    }

  }

  /**
   * Copies the given double array v into the first row of this matrix, and
   * creates this with the number of given rows and columns. This is basically a
   * conversion case for column major storage to this class (in the past this
   * was backed by a two-dimensional array, thus the conversion needs).
   * 
   * @param v the values to put into the first row.
   * @param rows the number of rows.
   * @param columns the number of columns.
   */
  public DenseDoubleMatrix(double[] v, int rows, int columns) {
    this(v, rows, columns, true);
  }

  private DenseDoubleMatrix(double[] v, int rows, int columns, boolean copy) {
    this.numRows = rows;
    this.numColumns = columns;
    if (copy) {
      this.matrix = new double[columns * rows];
      System.arraycopy(v, 0, this.matrix, 0, v.length);
    } else {
      this.matrix = v;
    }
  }

  /*
   * ------------CONSTRUCTOR END------------
   */

  /**
   * @return the internal matrix representation, no defensive copy is made.
   */
  public double[] getColumnMajorMatrix() {
    return this.matrix;
  }

  @Override
  public double get(int row, int col) {
    return matrix[translate(row, col, numRows)];
  }

  /**
   * Gets a whole column of the matrix as a double array.
   */
  public double[] getColumn(int col) {
    double[] column = new double[numRows];
    int offset = translate(0, col, numRows);
    for (int i = 0; i < column.length; i++) {
      column[i] = matrix[offset + i];
    }
    return column;
  }

  @Override
  public int getColumnCount() {
    return numColumns;
  }

  @Override
  public DoubleVector getColumnVector(int col) {
    return new DenseDoubleVector(getColumn(col));
  }

  @Override
  public double[][] toArray() {
    double[][] mat = new double[getRowCount()][getColumnCount()];
    int index = 0;
    for (int col = 0; col < getColumnCount(); col++) {
      for (int row = 0; row < getRowCount(); row++) {
        mat[row][col] = matrix[index++];
      }
    }
    return mat;
  }

  /**
   * Get a single row of the matrix as a double array.
   */
  public double[] getRow(int row) {
    double[] rowArray = new double[getColumnCount()];
    for (int i = 0; i < getColumnCount(); i++) {
      rowArray[i] = get(row, i);
    }
    return rowArray;
  }

  @Override
  public int getRowCount() {
    return numRows;
  }

  @Override
  public DoubleVector getRowVector(int row) {
    return new DenseDoubleVector(getRow(row));
  }

  @Override
  public void set(int row, int col, double value) {
    this.matrix[translate(row, col, numRows)] = value;
  }

  /**
   * Sets the row to a given double array.
   */
  public void setRow(int row, double[] value) {
    for (int i = 0; i < value.length; i++) {
      this.matrix[translate(row, i, numRows)] = value[i];
    }
  }

  /**
   * Sets the column to a given double array.
   */
  public void setColumn(int col, double[] values) {
    int offset = translate(0, col, numRows);
    System.arraycopy(values, 0, matrix, offset, values.length);
  }

  @Override
  public void setColumnVector(int col, DoubleVector column) {
    this.setColumn(col, column.toArray());
  }

  @Override
  public void setRowVector(int rowIndex, DoubleVector row) {
    this.setRow(rowIndex, row.toArray());
  }

  @Override
  public DenseDoubleMatrix multiply(double scalar) {
    double[] csjr = new double[this.numRows * this.numColumns];
    for (int i = 0; i < matrix.length; i++) {
      csjr[i] = this.matrix[i] * scalar;
    }
    return new DenseDoubleMatrix(csjr, this.numRows, this.numColumns, false);
  }

  @Override
  public DoubleMatrix multiply(DoubleMatrix other) {
    DenseDoubleMatrix matrix = null;

    int m = this.numRows;
    int n = this.numColumns;
    int p = other.getColumnCount();
    // only execute when we have JBLAS and our matrix is bigger than 50x50
    if (JBLAS_AVAILABLE && m > JBLAS_ROW_THRESHOLD
        && n > JBLAS_COLUMN_THRESHOLD && !other.isSparse()) {
      org.jblas.DoubleMatrix jblasThis = new org.jblas.DoubleMatrix(m, n,
          this.matrix);
      org.jblas.DoubleMatrix jblasOther = new org.jblas.DoubleMatrix(
          other.getRowCount(), other.getColumnCount(),
          ((DenseDoubleMatrix) other).matrix);
      org.jblas.DoubleMatrix jblasRes = new org.jblas.DoubleMatrix(m, p);
      jblasThis.mmuli(jblasOther, jblasRes);
      // copy the result back
      matrix = new DenseDoubleMatrix(jblasRes.toArray(), this.getRowCount(),
          other.getColumnCount(), false);
    } else {
      matrix = new DenseDoubleMatrix(m, p);
      for (int k = 0; k < n; k++) {
        for (int i = 0; i < m; i++) {
          for (int j = 0; j < p; j++) {
            matrix.set(i, j, matrix.get(i, j) + get(i, k) * other.get(k, j));
          }
        }
      }
    }

    return matrix;
  }

  @Override
  public DoubleMatrix multiplyElementWise(DoubleMatrix other) {
    DenseDoubleMatrix matrix = new DenseDoubleMatrix(this.numRows,
        this.numColumns);

    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        matrix.set(i, j, this.get(i, j) * (other.get(i, j)));
      }
    }

    return matrix;
  }

  @Override
  public DoubleVector multiplyVectorRow(DoubleVector v) {
    DoubleVector vector = new DenseDoubleVector(this.getRowCount());
    for (int row = 0; row < numRows; row++) {
      double sum = 0.0d;
      if (v.isSparse()) {
        Iterator<DoubleVectorElement> iterateNonZero = v.iterateNonZero();
        while (iterateNonZero.hasNext()) {
          DoubleVectorElement next = iterateNonZero.next();
          sum += (matrix[translate(row, next.getIndex(), numRows)] * next
              .getValue());
        }
      } else {
        for (int col = 0; col < numColumns; col++) {
          sum += (matrix[translate(row, col, numRows)] * v.get(col));
        }
      }
      vector.set(row, sum);
    }

    return vector;
  }

  @Override
  public DoubleVector multiplyVectorColumn(DoubleVector v) {
    DoubleVector vector = new DenseDoubleVector(this.getColumnCount());
    for (int col = 0; col < numColumns; col++) {
      double sum = 0.0d;
      for (int row = 0; row < numRows; row++) {
        sum += (matrix[translate(row, col, numRows)] * v.get(row));
      }
      vector.set(col, sum);
    }

    return vector;
  }

  @Override
  public DenseDoubleMatrix transpose() {
    DenseDoubleMatrix m = new DenseDoubleMatrix(this.numColumns, this.numRows);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(j, i, this.matrix[translate(i, j, numRows)]);
      }
    }
    return m;
  }

  @Override
  public DenseDoubleMatrix subtractBy(double amount) {
    double[] csjr = new double[this.numRows * this.numColumns];
    for (int i = 0; i < matrix.length; i++) {
      csjr[i] = amount - this.matrix[i];
    }
    return new DenseDoubleMatrix(csjr, this.numRows, this.numColumns, false);
  }

  @Override
  public DenseDoubleMatrix subtract(double amount) {
    double[] csjr = new double[this.numRows * this.numColumns];
    for (int i = 0; i < matrix.length; i++) {
      csjr[i] = this.matrix[i] - amount;
    }
    return new DenseDoubleMatrix(csjr, this.numRows, this.numColumns, false);
  }

  @Override
  public DoubleMatrix subtract(DoubleMatrix other) {
    DoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, this.matrix[translate(i, j, numRows)] - other.get(i, j));
      }
    }
    return m;
  }

  @Override
  public DenseDoubleMatrix subtract(DoubleVector vec) {
    DenseDoubleMatrix cop = new DenseDoubleMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int i = 0; i < this.getColumnCount(); i++) {
      cop.setColumnVector(i, getColumnVector(i).subtract(vec));
    }
    return cop;
  }

  @Override
  public DoubleMatrix divide(DoubleVector vec) {
    DoubleMatrix cop = new DenseDoubleMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int i = 0; i < this.getColumnCount(); i++) {
      cop.setColumnVector(i, getColumnVector(i).divide(vec));
    }
    return cop;
  }

  @Override
  public DoubleMatrix divide(DoubleMatrix other) {
    DoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, this.matrix[translate(i, j, numRows)] / other.get(i, j));
      }
    }
    return m;
  }

  @Override
  public DoubleMatrix divide(double scalar) {
    double[] csjr = new double[this.numRows * this.numColumns];
    for (int i = 0; i < matrix.length; i++) {
      csjr[i] = this.matrix[i] / scalar;
    }
    return new DenseDoubleMatrix(csjr, this.numRows, this.numColumns, false);
  }

  @Override
  public DoubleMatrix add(DoubleMatrix other) {
    DoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, this.matrix[translate(i, j, numRows)] + other.get(i, j));
      }
    }
    return m;
  }

  @Override
  public DoubleMatrix pow(double x) {
    double[] csjr = new double[this.numRows * this.numColumns];
    for (int i = 0; i < matrix.length; i++) {
      if (x == 2d) {
        csjr[i] = this.matrix[i] * this.matrix[i];
      } else {
        csjr[i] = FastMath.pow(this.matrix[i], x);
      }
    }
    return new DenseDoubleMatrix(csjr, this.numRows, this.numColumns, false);
  }

  @Override
  public double max(int column) {
    double max = Double.MIN_VALUE;
    int offset = translate(0, column, numRows);
    for (int i = 0; i < getRowCount(); i++) {
      double d = matrix[offset + i];
      if (d > max) {
        max = d;
      }
    }
    return max;
  }

  @Override
  public double min(int column) {
    double min = Double.MAX_VALUE;
    int offset = translate(0, column, numRows);
    for (int i = 0; i < getRowCount(); i++) {
      double d = matrix[offset + i];
      if (d < min) {
        min = d;
      }
    }
    return min;
  }

  @Override
  public DoubleMatrix slice(int rows, int cols) {
    return slice(0, rows, 0, cols);
  }

  @Override
  public DoubleMatrix slice(int rowOffset, int rowMax, int colOffset, int colMax) {
    DenseDoubleMatrix m = new DenseDoubleMatrix(rowMax - rowOffset, colMax
        - colOffset);
    for (int row = rowOffset; row < rowMax; row++) {
      for (int col = colOffset; col < colMax; col++) {
        m.set(row - rowOffset, col - colOffset, this.get(row, col));
      }
    }
    return m;
  }

  @Override
  public boolean isSparse() {
    return false;
  }

  @Override
  public double sum() {
    double x = 0.0d;
    for (int i = 0; i < matrix.length; i++) {
      x += Math.abs(matrix[i]);
    }
    return x;
  }

  @Override
  public int[] columnIndices() {
    int[] x = new int[getColumnCount()];
    for (int i = 0; i < getColumnCount(); i++)
      x[i] = i;
    return x;
  }

  @Override
  public int[] rowIndices() {
    int[] x = new int[getRowCount()];
    for (int i = 0; i < getRowCount(); i++)
      x[i] = i;
    return x;
  }

  @Override
  public DoubleMatrix deepCopy() {
    return new DenseDoubleMatrix(toArray());
  }

  @Override
  public String toString() {
    if (numRows * numColumns < 100) {
      StringBuilder sb = new StringBuilder();
      double[][] array = toArray();
      for (int i = 0; i < numRows; i++) {
        sb.append(Arrays.toString(array[i]));
        sb.append('\n');
      }
      return sb.toString();
    } else {
      return sizeToString();
    }
  }

  /**
   * Returns the size of the matrix as string (ROWSxCOLUMNS).
   */
  public String sizeToString() {
    return numRows + "x" + numColumns;
  }

  /**
   * Translates the 2D addressing to a single offset in the 1D matrix.
   * 
   * @param row the row to get.
   * @param col the column to get.
   * @param numRows the number of rows in the matrix.
   * @return an offset in the 1D matrix that contains the row/col value.
   */
  private static int translate(int row, int col, int numRows) {
    return row + col * numRows;
  }

}
