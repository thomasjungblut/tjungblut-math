package de.jungblut.math.dense;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jblas.MyNativeBlasLibraryLoader;

import de.jungblut.math.DoubleMatrix;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.DoubleVector.DoubleVectorElement;

/**
 * Dense double matrix implementation, internally uses two dimensional double
 * arrays.
 */
public final class DenseDoubleMatrix implements DoubleMatrix {

  private static final int JBLAS_COLUMN_THRESHOLD = 100;
  private static final int JBLAS_ROW_THRESHOLD = 100;

  private static final boolean JBLAS_AVAILABLE;

  static {
    JBLAS_AVAILABLE = MyNativeBlasLibraryLoader.loadLibraryAndCheckErrors();
  }

  private final double[][] matrix;
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
    this.matrix = new double[rows][columns];
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
    for (int i = 0; i < numRows; i++) {
      Arrays.fill(matrix[i], defaultValue);
    }
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
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        matrix[i][j] = rand.nextDouble();
      }
    }
  }

  /**
   * Simple copy constructor, does a deep copy of the given parameter.
   * 
   * @param otherMatrix the other matrix.
   */
  public DenseDoubleMatrix(double[][] otherMatrix) {
    this(otherMatrix.length, otherMatrix[0].length);
    for (int i = 0; i < otherMatrix.length; i++) {
      System.arraycopy(otherMatrix[i], 0, matrix[i], 0, otherMatrix[i].length);
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
    int index = 0;
    for (DoubleVector value : vec) {
      matrix[index++] = value.toArray();
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
    for (int row = 0; row < getRowCount(); row++) {
      set(row, 0, first.get(row));
      DoubleVector rowVector = otherMatrix.getRowVector(row);
      System.arraycopy(rowVector.toArray(), 0, matrix[row], 1,
          rowVector.getLength());
    }
  }

  /**
   * Copies the given double array v into the first row of this matrix, and
   * creates this with the number of given rows and columns.
   * 
   * @param v the values to put into the first row.
   * @param rows the number of rows.
   * @param columns the number of columns.
   */
  public DenseDoubleMatrix(double[] v, int rows, int columns) {
    this(rows, columns);
    for (int i = 0; i < rows; i++) {
      System.arraycopy(v, i * columns, this.matrix[i], 0, columns);
    }

    int index = 0;
    for (int col = 0; col < columns; col++) {
      for (int row = 0; row < rows; row++) {
        matrix[row][col] = v[index++];
      }
    }
  }

  /*
   * ------------CONSTRUCTOR END------------
   */

  @Override
  public double get(int row, int col) {
    return this.matrix[row][col];
  }

  /**
   * Gets a whole column of the matrix as a double array.
   */
  public double[] getColumn(int col) {
    double[] column = new double[numRows];
    for (int r = 0; r < numRows; r++) {
      column[r] = matrix[r][col];
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

  /**
   * Get the matrix as 2-dimensional double array (first dimension is the row,
   * second the column) to faster access the values.
   */
  public double[][] getValues() {
    return matrix;
  }

  /**
   * Get a single row of the matrix as a double array.
   */
  public double[] getRow(int row) {
    return matrix[row];
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
    this.matrix[row][col] = value;
  }

  /**
   * Sets the row to a given double array. This does not copy, rather than just
   * bends the references.
   */
  public void setRow(int row, double[] value) {
    this.matrix[row] = value;
  }

  /**
   * Sets the column to a given double array. This does not copy, rather than
   * just bends the references.
   */
  public void setColumn(int col, double[] values) {
    for (int i = 0; i < getRowCount(); i++) {
      this.matrix[i][col] = values[i];
    }
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
    DenseDoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, this.matrix[i][j] * scalar);
      }
    }
    return m;
  }

  @Override
  public DoubleMatrix multiply(DoubleMatrix other) {
    DenseDoubleMatrix matrix = new DenseDoubleMatrix(this.getRowCount(),
        other.getColumnCount());

    int m = this.numRows;
    int n = this.numColumns;
    int p = other.getColumnCount();
    // only execute when we have JBLAS and our matrix is bigger than 50x50
    if (JBLAS_AVAILABLE && m > JBLAS_ROW_THRESHOLD
        && n > JBLAS_COLUMN_THRESHOLD) {
      org.jblas.DoubleMatrix jblasThis = new org.jblas.DoubleMatrix(this.matrix);
      org.jblas.DoubleMatrix jblasOther = new org.jblas.DoubleMatrix(
          ((DenseDoubleMatrix) other).matrix);
      org.jblas.DoubleMatrix jblasRes = new org.jblas.DoubleMatrix(
          matrix.getRowCount(), this.getColumnCount());
      jblasThis.mmuli(jblasOther, jblasRes);
      // copy the result back
      for (int row = 0; row < matrix.getRowCount(); row++) {
        for (int col = 0; col < matrix.getColumnCount(); col++) {
          matrix.set(row, col, jblasRes.get(jblasRes.index(row, col)));
        }
      }
    } else {
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
  public DoubleVector multiplyVector(DoubleVector v) {
    DoubleVector vector = new DenseDoubleVector(this.getRowCount());
    for (int row = 0; row < numRows; row++) {
      double sum = 0.0d;
      if (v.isSparse()) {
        Iterator<DoubleVectorElement> iterateNonZero = v.iterateNonZero();
        while (iterateNonZero.hasNext()) {
          DoubleVectorElement next = iterateNonZero.next();
          sum += (matrix[row][next.getIndex()] * next.getValue());
        }
      } else {
        for (int col = 0; col < numColumns; col++) {
          sum += (matrix[row][col] * v.get(col));
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
        sum += (matrix[row][col] * v.get(row));
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
        m.set(j, i, this.matrix[i][j]);
      }
    }
    return m;
  }

  @Override
  public DenseDoubleMatrix subtractBy(double amount) {
    DenseDoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, amount - this.matrix[i][j]);
      }
    }
    return m;
  }

  @Override
  public DenseDoubleMatrix subtract(double amount) {
    DenseDoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, this.matrix[i][j] - amount);
      }
    }
    return m;
  }

  @Override
  public DoubleMatrix subtract(DoubleMatrix other) {
    DoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, this.matrix[i][j] - other.get(i, j));
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
        m.set(i, j, this.matrix[i][j] / other.get(i, j));
      }
    }
    return m;
  }

  @Override
  public DoubleMatrix divide(double scalar) {
    DoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, this.matrix[i][j] / scalar);
      }
    }
    return m;
  }

  @Override
  public DoubleMatrix add(DoubleMatrix other) {
    DoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        m.set(i, j, this.matrix[i][j] + other.get(i, j));
      }
    }
    return m;
  }

  @Override
  public DoubleMatrix pow(double x) {
    DoubleMatrix m = new DenseDoubleMatrix(this.numRows, this.numColumns);
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        if (x == 2d) {
          m.set(i, j, matrix[i][j] * matrix[i][j]);
        } else {
          m.set(i, j, Math.pow(matrix[i][j], x));
        }
      }
    }
    return m;
  }

  @Override
  public double max(int column) {
    double max = Double.MIN_VALUE;
    for (int i = 0; i < getRowCount(); i++) {
      double d = matrix[i][column];
      if (d > max) {
        max = d;
      }
    }
    return max;
  }

  @Override
  public double min(int column) {
    double min = Double.MAX_VALUE;
    for (int i = 0; i < getRowCount(); i++) {
      double d = matrix[i][column];
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
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numColumns; j++) {
        x += Math.abs(matrix[i][j]);
      }
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
    return new DenseDoubleMatrix(getValues());
  }

  @Override
  public String toString() {
    if (numRows * numColumns < 100) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < numRows; i++) {
        sb.append(Arrays.toString(matrix[i]));
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
   * Gets the eye matrix (ones on the main diagonal) with a given dimension.
   */
  public static DenseDoubleMatrix eye(int dimension) {
    DenseDoubleMatrix m = new DenseDoubleMatrix(dimension, dimension);

    for (int i = 0; i < dimension; i++) {
      m.set(i, i, 1);
    }

    return m;
  }

}
