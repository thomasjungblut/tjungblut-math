package de.jungblut.math.sparse;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Iterator;
import java.util.List;

import de.jungblut.math.BooleanMatrix;
import de.jungblut.math.BooleanVector;
import de.jungblut.math.BooleanVector.BooleanVectorElement;
import de.jungblut.math.DoubleMatrix;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.DoubleVector.DoubleVectorElement;
import de.jungblut.math.dense.DenseDoubleVector;

public final class SparseDoubleRowMatrix implements DoubleMatrix {

  // int -> vector, where int is the row index and vector the corresponding
  // row vector
  private final TIntObjectHashMap<SparseDoubleVector> matrix;
  protected final int numRows;
  protected final int numColumns;

  public SparseDoubleRowMatrix(int rows, int columns) {
    this.numRows = rows;
    this.numColumns = columns;
    this.matrix = new TIntObjectHashMap<SparseDoubleVector>(numRows);
  }

  public SparseDoubleRowMatrix(DoubleMatrix mat) {
    this(mat.getRowCount(), mat.getColumnCount());
    for (int i = 0; i < numColumns; i++) {
      setRowVector(i, mat.getRowVector(i));
    }
  }

  public SparseDoubleRowMatrix(DenseDoubleVector v, DoubleMatrix mat) {
    this(mat.getRowCount(), mat.getColumnCount() + 1);
    setColumnVector(0, v);
    for (int i = 1; i < numColumns; i++) {
      setColumnVector(i, mat.getColumnVector(i - 1));
    }
  }

  public SparseDoubleRowMatrix(double[][] otherMatrix) {
    this(otherMatrix.length, otherMatrix[0].length);
    for (int i = 0; i < numColumns; i++) {
      for (int row = 0; row < numRows; row++) {
        set(row, i, otherMatrix[row][i]);
      }
    }
  }

  public SparseDoubleRowMatrix(List<DoubleVector> vec) {
    this(vec.size(), vec.get(0).getDimension());

    int key = 0;
    for (DoubleVector value : vec) {
      matrix.put(key++, new SparseDoubleVector(value));
    }

  }

  public SparseDoubleRowMatrix(DoubleVector[] vec) {
    this(vec.length, vec[0].getDimension());

    int key = 0;
    for (DoubleVector value : vec) {
      matrix.put(key++, new SparseDoubleVector(value));
    }

  }

  @Override
  public double get(int row, int col) {
    SparseDoubleVector sparseDoubleVector = matrix.get(row);
    if (sparseDoubleVector == null)
      return NOT_FLAGGED;
    else
      return sparseDoubleVector.get(col);
  }

  @Override
  public int getColumnCount() {
    return numColumns;
  }

  @Override
  public DoubleVector getColumnVector(int col) {
    int[] keys = matrix.keys();
    DoubleVector v = new SparseDoubleVector(getRowCount());
    for (int key : keys) {
      v.set(key, get(key, col));
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
    if (v == null)
      return new SparseDoubleVector(getColumnCount());
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
      set(col, next.getIndex(), next.getValue());
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
    // TODO improve for sparse vectors..
    DoubleMatrix result = new SparseDoubleRowMatrix(this.getRowCount(),
        other.getColumnCount());
    for (int row = 0; row < getRowCount(); row++) {
      for (int col = 0; col < other.getColumnCount(); col++) {
        double sum = 0;
        for (int k = 0; k < getColumnCount(); k++) {
          sum += get(row, k) * other.get(k, col);
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
  public DoubleMatrix multiplyElementWise(BooleanMatrix other) {
    SparseDoubleRowMatrix matrix = new SparseDoubleRowMatrix(this.numRows,
        this.numColumns);
    for (int col : other.columnIndices()) {
      BooleanVector columnVector = other.getColumnVector(col);
      Iterator<BooleanVectorElement> iterateNonZero = columnVector
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        BooleanVectorElement next = iterateNonZero.next();
        matrix.set(next.getIndex(), col, get(next.getIndex(), col));
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix multiplyElementWise(DoubleMatrix other) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this);
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
  public DoubleVector multiplyVector(DoubleVector v) {
    DoubleVector result = new SparseDoubleVector(this.getRowCount());
    for (int col : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(col)
          .iterateNonZero();
      double sum = 0.0d;
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        sum += (e.getValue() * v.get(col));
      }
      result.set(col, sum);
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
    DoubleMatrix result = new SparseDoubleRowMatrix(this);
    for (int col : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(col)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        result.set(e.getIndex(), col, amount - get(e.getIndex(), col));
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix subtract(double amount) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this);
    for (int col : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(col)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        result.set(e.getIndex(), col, get(e.getIndex(), col) - amount);
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix subtract(DoubleMatrix other) {
    SparseDoubleRowMatrix m = new SparseDoubleRowMatrix(other);

    for (int col : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(col)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        m.set(e.getIndex(), col,
            get(e.getIndex(), col) - other.get(e.getIndex(), col));
      }
    }

    for (int col : other.columnIndices()) {
      Iterator<DoubleVectorElement> iterateNonZero = other.getColumnVector(col)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        m.set(e.getIndex(), col,
            get(e.getIndex(), col) - other.get(e.getIndex(), col));
      }
    }

    return m;
  }

  @Override
  public DoubleMatrix subtract(DoubleVector vec) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this);
    for (int col : this.matrix.keys()) {
      SparseDoubleVector colVec = matrix.get(col);
      result.setColumnVector(col, colVec.subtract(vec.get(col)));
    }
    return result;
  }

  @Override
  public DoubleMatrix divide(DoubleVector vec) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this);
    for (int row : this.matrix.keys()) {
      SparseDoubleVector rowVector = matrix.get(row);
      result.setRowVector(row, rowVector.divide(vec.get(row)));
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
    DoubleMatrix result = new SparseDoubleRowMatrix(this);
    for (int col : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(col)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        result.set(e.getIndex(), col, get(e.getIndex(), col) / scalar);
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix add(DoubleMatrix other) {
    SparseDoubleRowMatrix m = new SparseDoubleRowMatrix(other);

    for (int col : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(col)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        m.set(e.getIndex(), col,
            get(e.getIndex(), col) + other.get(e.getIndex(), col));
      }
    }

    for (int col : other.columnIndices()) {
      Iterator<DoubleVectorElement> iterateNonZero = other.getColumnVector(col)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        m.set(e.getIndex(), col,
            get(e.getIndex(), col) + other.get(e.getIndex(), col));
      }
    }

    return m;
  }

  @Override
  public DoubleMatrix pow(int x) {
    DoubleMatrix result = new SparseDoubleRowMatrix(this);
    for (int row : this.matrix.keys()) {
      Iterator<DoubleVectorElement> iterateNonZero = matrix.get(row)
          .iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement e = iterateNonZero.next();
        if (x != 2) {
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

  public static int[] fromUpTo(int from, int to, int stepsize) {
    int[] v = new int[(to - from) / stepsize];

    for (int i = 0; i < v.length; i++) {
      v[i] = from + i * stepsize;
    }
    return v;
  }

  @Override
  public String toString() {
    return numRows + "x" + numColumns;
  }

}
