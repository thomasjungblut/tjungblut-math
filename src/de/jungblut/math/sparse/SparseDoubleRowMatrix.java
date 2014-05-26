package de.jungblut.math.sparse;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.jungblut.math.DoubleMatrix;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.DoubleVector.DoubleVectorElement;
import de.jungblut.math.dense.DenseDoubleVector;

/**
 * Sparse matrix implementation that maps row indices to a sparse vector which
 * represents the row. So every access has at maximum two hashmap lookups (first
 * by the row index, then by the column index).
 * 
 * @author thomas.jungblut
 * 
 */
public final class SparseDoubleRowMatrix implements DoubleMatrix {

  // int -> vector, where int is the row index and vector the corresponding
  // row vector
  private final TIntObjectHashMap<SparseDoubleVector> matrix;
  private final int numRows;
  private final int numColumns;

  /**
   * Constructs a sparse matrix with the given dimensions.
   * 
   * @param rows the number of rows (act as a hint to the backed hashmap).
   * @param columns the number of columns.
   */
  public SparseDoubleRowMatrix(int rows, int columns) {
    this.numRows = rows;
    this.numColumns = columns;
    this.matrix = new TIntObjectHashMap<>(numRows);
  }

  /**
   * Dense primitive matrix copy constructor.
   * 
   * @param otherMatrix the other matrix.
   */
  public SparseDoubleRowMatrix(double[][] otherMatrix) {
    this(otherMatrix.length, otherMatrix[0].length);
    for (int i = 0; i < numColumns; i++) {
      for (int row = 0; row < numRows; row++) {
        set(row, i, otherMatrix[row][i]);
      }
    }
  }

  /**
   * Generates a matrix out of a vector list. it treats the entries as rows and
   * the vector itself contains the values of the columns.
   * 
   * @param vectorArray the list of vectors.
   */
  public SparseDoubleRowMatrix(List<DoubleVector> vec) {
    this(vec.size(), vec.get(0).getDimension());

    int key = 0;
    for (DoubleVector value : vec) {
      matrix.put(key++, new SparseDoubleVector(value));
    }
  }

  /**
   * Generates a matrix out of a vector array. It treats the array entries as
   * rows and the vector itself contains the values of the columns.
   * 
   * @param vectorArray the array of vectors.
   */
  public SparseDoubleRowMatrix(DoubleVector[] vectorArray) {
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
  public SparseDoubleRowMatrix(DenseDoubleVector first, DoubleMatrix otherMatrix) {
    this(otherMatrix.getRowCount(), otherMatrix.getColumnCount() + 1);
    setColumnVector(0, first);
    for (int i = 1; i < numColumns; i++) {
      setColumnVector(i, otherMatrix.getColumnVector(i - 1));
    }
  }

  /**
   * Row-copies the given matrix to this sparse implementation.
   * 
   * @param mat the matrix to copy.
   */
  public SparseDoubleRowMatrix(DoubleMatrix mat) {
    this(mat.getRowCount(), mat.getColumnCount());
    for (int i = 0; i < numColumns; i++) {
      setRowVector(i, mat.getRowVector(i));
    }
  }

  @Override
  public double get(int row, int col) {
    SparseDoubleVector vector = matrix.get(row);
    if (vector == null) {
      return NOT_FLAGGED;
    } else {
      return vector.get(col);
    }
  }

  @Override
  public int getColumnCount() {
    return numColumns;
  }

  @Override
  public DoubleVector getColumnVector(int col) {
    int[] rows = matrix.keys();
    DoubleVector v = new SparseDoubleVector(getRowCount());
    for (int row : rows) {
      v.set(row, get(row, col));
    }
    return v;
  }

  @Override
  public int getRowCount() {
    return numRows;
  }

  @Override
  public DoubleVector getRowVector(int row) {
    SparseDoubleVector v = matrix.get(row);
    if (v == null) {
      v = new SparseDoubleVector(getColumnCount());
      matrix.put(row, v);
    }
    return v;
  }

  @Override
  public void set(int row, int col, double value) {
    if (value != 0.0d) {
      SparseDoubleVector sparseDoubleVector = matrix.get(row);
      if (sparseDoubleVector == null) {
        sparseDoubleVector = new SparseDoubleVector(getColumnCount());
        matrix.put(row, sparseDoubleVector);
      }
      sparseDoubleVector.set(col, value);
    }
  }

  @Override
  public void setColumnVector(int col, DoubleVector column) {
    Iterator<DoubleVectorElement> iterateNonZero = column.iterateNonZero();
    while (iterateNonZero.hasNext()) {
      DoubleVectorElement next = iterateNonZero.next();
      set(next.getIndex(), col, next.getValue());
    }
  }

  @Override
  public void setRowVector(int rowIndex, DoubleVector row) {
    matrix.put(rowIndex, new SparseDoubleVector(row));
  }

  @Override
  public DoubleMatrix multiply(double scalar) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this);
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        result.set(row, e.getIndex(), get(row, e.getIndex()) * scalar);
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix multiply(DoubleMatrix other) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        other.getColumnCount());
    for (int row = 0; row < getRowCount(); row++) {
      for (int col = 0; col < other.getColumnCount(); col++) {
        double sum = 0;
        Iterator<DoubleVectorElement> kIterator = getRowVector(row)
            .iterateNonZero();
        while (kIterator.hasNext()) {
          DoubleVectorElement k = kIterator.next();
          double val = other.get(k.getIndex(), col);
          if (val != 0d) {
            sum += k.getValue() * val;
          }
        }
        result.set(row, col, sum);
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix slice(int rows, int cols) {
    return slice(0, rows, 0, cols);
  }

  @Override
  public DoubleMatrix slice(int rowOffset, int rowMax, int colOffset, int colMax) {
    DoubleMatrix m = new SparseDoubleRowMatrix(rowMax - rowOffset, colMax
        - colOffset);
    for (int col : columnIndices()) {
      DoubleVector columnVector = getColumnVector(col);
      columnVector = columnVector.slice(rowOffset, rowMax);
      m.setColumnVector(col - colOffset, columnVector);
    }

    return m;
  }

  @Override
  public DoubleMatrix multiplyElementWise(DoubleMatrix other) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        result.set(row, e.getIndex(),
            get(row, e.getIndex()) * other.get(row, e.getIndex()));
      }
    }
    return result;
  }

  @Override
  public DoubleVector multiplyVectorRow(DoubleVector v) {
    DoubleVector result = new SparseDoubleVector(this.getRowCount());
    for (int row : matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      double sum = 0.0d;
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        sum += (e.getValue() * v.get(e.getIndex()));
      }
      result.set(row, sum);
    }
    return result;
  }

  @Override
  public DoubleVector multiplyVectorColumn(DoubleVector v) {
    DoubleVector result = new SparseDoubleVector(this.getColumnCount());
    if (v.isSparse()) {
      Iterator<DoubleVectorElement> vectorNonZero = v.iterateNonZero();
      while (vectorNonZero.hasNext()) {
        DoubleVectorElement featureElement = vectorNonZero.next();
        DoubleVector rowVector = getRowVector(featureElement.getIndex());
        Iterator<DoubleVectorElement> rowNonZero = rowVector.iterateNonZero();
        while (rowNonZero.hasNext()) {
          DoubleVectorElement outcomeElement = rowNonZero.next();
          result.set(
              outcomeElement.getIndex(),
              result.get(outcomeElement.getIndex())
                  + (outcomeElement.getValue() * featureElement.getValue()));
        }
      }
    } else {
      for (int row : rowIndices()) {
        Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
            .iterateNonZero();
        while (iterateNonZero.hasNext()) {
          DoubleVectorElement e = iterateNonZero.next();
          result.set(e.getIndex(),
              (e.getValue() * v.get(row)) + result.get(e.getIndex()));
        }
      }
    }
    return result;
  }

  @Override
  public boolean isSparse() {
    return true;
  }

  @Override
  public DoubleMatrix transpose() {
    SparseDoubleRowMatrix m = new SparseDoubleRowMatrix(this.numColumns,
        this.numRows);
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        m.set(e.getIndex(), row, e.getValue());
      }
    }
    return m;
  }

  @Override
  public DoubleMatrix subtractBy(double amount) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterate = matrix.get(row).iterate();
      while (iterate.hasNext()) {
        DoubleVectorElement e = iterate.next();
        result.set(row, e.getIndex(), amount - e.getValue());
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix subtract(double amount) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterate = matrix.get(row).iterate();
      while (iterate.hasNext()) {
        DoubleVectorElement e = iterate.next();
        result.set(row, e.getIndex(), e.getValue() - amount);
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix subtract(DoubleMatrix other) {
    SparseDoubleRowMatrix result = new SparseDoubleRowMatrix(
        other.getRowCount(), other.getColumnCount());

    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterate = matrix.get(row).iterate();
      while (iterate.hasNext()) {
        DoubleVectorElement e = iterate.next();
        result.set(row, e.getIndex(),
            e.getValue() - other.get(row, e.getIndex()));
      }
    }

    return result;
  }

  @Override
  public DoubleMatrix subtract(DoubleVector vec) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int row : this.matrix.keys()) {
      SparseDoubleVector rowVec = matrix.get(row);
      result.setRowVector(row, rowVec.subtract(vec.get(row)));
    }
    return result;
  }

  @Override
  public DoubleMatrix divide(DoubleVector vec) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int row : this.matrix.keys()) {
      SparseDoubleVector rowVector = matrix.get(row);
      Iterator<DoubleVectorElement> iterateNonZero = rowVector.iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement next = iterateNonZero.next();
        result.set(row, next.getIndex(), next.getValue() / vec.get(row));
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix divide(DoubleMatrix other) {
    SparseDoubleRowMatrix m = new SparseDoubleRowMatrix(other);

    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        m.set(row, e.getIndex(),
            get(row, e.getIndex()) / other.get(row, e.getIndex()));
      }
    }

    for (int col : other.columnIndices()) {
      Iterator<DoubleVectorElement> iterateNonZero = other.getColumnVector(col)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        m.set(e.getIndex(), col,
            get(e.getIndex(), col) / other.get(e.getIndex(), col));
      }
    }

    return m;
  }

  @Override
  public DoubleMatrix divide(double scalar) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        result.set(row, e.getIndex(), e.getValue() / scalar);
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix add(DoubleMatrix other) {
    SparseDoubleRowMatrix result = new SparseDoubleRowMatrix(
        other.getRowCount(), other.getColumnCount());

    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterate = matrix.get(row).iterate();
      while (iterate.hasNext()) {
        DoubleVectorElement e = iterate.next();
        result.set(row, e.getIndex(),
            e.getValue() + other.get(row, e.getIndex()));
      }
    }

    return result;
  }

  @Override
  public DoubleMatrix pow(double x) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        this.getColumnCount());
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        if (x != 2d) {
          result.set(row, e.getIndex(), Math.pow(get(row, e.getIndex()), x));
        } else {
          double res = get(row, e.getIndex());
          result.set(row, e.getIndex(), res * res);
        }
      }
    }
    return result;
  }

  @Override
  public double max(int column) {
    return getColumnVector(column).max();
  }

  @Override
  public double min(int column) {
    return getColumnVector(column).min();
  }

  @Override
  public double sum() {
    double res = 0.0d;
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        res += e.getValue();
      }
    }
    return res;
  }

  @Override
  public int[] columnIndices() {
    return fromUpTo(0, getColumnCount(), 1);
  }

  @Override
  public int[] rowIndices() {
    return matrix.keys();
  }

  @Override
  public double[][] toArray() {
    int[] rowIndices = rowIndices();
    double[][] dim = new double[getRowCount()][getColumnCount()];

    for (int row : rowIndices) {
      DoubleVector rowVector = getRowVector(row);
      Iterator<DoubleVectorElement> iterateNonZero = rowVector.iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement next = iterateNonZero.next();
        dim[row][next.getIndex()] = next.getValue();
      }
    }

    return dim;
  }

  @Override
  public DoubleMatrix deepCopy() {
    return new SparseDoubleRowMatrix(this);
  }

  @Override
  public String toString() {
    if (numRows * numColumns < 50) {
      return matrix.toString();
    } else {
      return sizeToString();
    }
  }

  public void removeRow(int row) {
    matrix.remove(row);
  }

  /**
   * Returns the size of the matrix as string (ROWSxCOLUMNS).
   */
  public String sizeToString() {
    return numRows + "x" + numColumns;
  }

  static int[] fromUpTo(int from, int to, int stepsize) {
    int[] v = new int[(to - from) / stepsize];

    for (int i = 0; i < v.length; i++) {
      v[i] = from + i * stepsize;
    }
    return v;
  }

}
