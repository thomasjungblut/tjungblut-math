package de.jungblut.math.sparse;

import gnu.trove.set.hash.TIntHashSet;

import java.util.BitSet;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.google.common.collect.AbstractIterator;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.function.DoubleDoubleVectorFunction;
import de.jungblut.math.function.DoubleVectorFunction;

/**
 * Sparse bit vector backed by a {@link BitSet}. Every element either is zero or
 * one, on access this bit will be cast to a double to be in sync with the
 * {@link DoubleVector} interface.
 * 
 * @author thomas.jungblut
 * 
 */
public final class SparseBitVector implements DoubleVector {

  private static final double SPARSE_DEFAULT_VALUE = 0d;
  private final int dimension;
  private final BitSet vector;

  /**
   * Constructs a new {@link SparseBitVector}.
   * 
   * @param dimension the expected dimensionality of the vector.
   */
  public SparseBitVector(int dimension) {
    this.dimension = dimension;
    this.vector = new BitSet(dimension);
  }

  /**
   * Constructs a new {@link SparseBitVector}.
   * 
   * @param v the given vector to copy.
   */
  public SparseBitVector(DoubleVector v) {
    this(v.getDimension());
    Iterator<DoubleVectorElement> iterateNonZero = v.iterateNonZero();
    while (iterateNonZero.hasNext()) {
      DoubleVectorElement next = iterateNonZero.next();
      set(next.getIndex(), next.getValue());
    }
  }

  /**
   * Constructs a new {@link SparseBitVector}.
   * 
   * @param arr the given vector to copy.
   */
  public SparseBitVector(double[] arr) {
    this(arr.length);
    for (int i = 0; i < arr.length; i++) {
      set(i, arr[i]);
    }
  }

  /**
   * Creates a new vector with the given array and the first value firstElement.
   * 
   * @param firstElement the element that will be at index 0 (first position) in
   *          the resulting vector.
   * @param array the rest of the array for the vector.
   */
  public SparseBitVector(double firstElement, double[] arr) {
    this(arr.length + 1);
    set(0, firstElement);
    for (int i = 0; i < arr.length; i++) {
      set(i + 1, arr[i]);
    }
  }

  /**
   * Creates a new vector with the given array and the last value 'lastValue'.
   * This resulting vector will be of size array.length+1.
   * 
   * @param array the first part of the vector.
   * @param lastValue the element that will be at index length-1 (last position)
   *          in the resulting vector.
   */
  public SparseBitVector(double[] array, double lastValue) {
    this(array.length + 1);
    for (int i = 0; i < array.length; i++) {
      set(i, array[i]);
    }
    set(array.length, lastValue);
  }

  @Override
  public double get(int index) {
    return vector.get(index) ? 1d : 0d;
  }

  @Override
  public int getLength() {
    return vector.cardinality();
  }

  @Override
  public int getDimension() {
    return dimension;
  }

  @Override
  public void set(int index, double value) {
    if (value != SPARSE_DEFAULT_VALUE) {
      vector.set(index);
    }
  }

  @Override
  public DoubleVector apply(DoubleVectorFunction func) {
    SparseBitVector newV = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> iterate = this.iterate();
    while (iterate.hasNext()) {
      DoubleVectorElement next = iterate.next();
      double res = func.calculate(next.getIndex(), next.getValue());
      newV.set(next.getIndex(), res);
    }
    return newV;
  }

  @Override
  public DoubleVector apply(DoubleVector other, DoubleDoubleVectorFunction func) {
    SparseBitVector newV = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> iterate = this.iterate();
    while (iterate.hasNext()) {
      DoubleVectorElement next = iterate.next();
      double res = func.calculate(next.getIndex(), next.getValue(),
          other.get(next.getIndex()));
      newV.set(next.getIndex(), res);
    }
    return newV;
  }

  @Override
  public DoubleVector add(DoubleVector other) {
    DoubleVector result = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> iter = other.iterateNonZero();
    TIntHashSet calculated = new TIntHashSet();
    while (iter.hasNext()) {
      DoubleVectorElement e = iter.next();
      int index = e.getIndex();
      calculated.add(index);
      result.set(index, this.get(index) + e.getValue());
    }

    iter = iterateNonZero();
    while (iter.hasNext()) {
      DoubleVectorElement e = iter.next();
      int index = e.getIndex();
      if (!calculated.contains(index)) {
        result.set(index, e.getValue() + other.get(index));
      }
    }
    return result;
  }

  @Override
  public DoubleVector add(double scalar) {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterate();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() + scalar);
    }
    return v;
  }

  @Override
  public DoubleVector subtract(DoubleVector other) {
    DoubleVector result = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> iter = other.iterateNonZero();
    TIntHashSet calculated = new TIntHashSet();
    while (iter.hasNext()) {
      DoubleVectorElement e = iter.next();
      int index = e.getIndex();
      calculated.add(index);
      result.set(index, this.get(index) - e.getValue());
    }

    iter = iterateNonZero();
    while (iter.hasNext()) {
      DoubleVectorElement e = iter.next();
      int index = e.getIndex();
      if (!calculated.contains(index)) {
        result.set(index, e.getValue() - other.get(index));
      }
    }

    return result;
  }

  @Override
  public DoubleVector subtract(double scalar) {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterate();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() - scalar);
    }
    return v;
  }

  @Override
  public DoubleVector subtractFrom(double scalar) {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterate();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), scalar - e.getValue());
    }
    return v;
  }

  @Override
  public DoubleVector multiply(double scalar) {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() * scalar);
    }
    return v;
  }

  @Override
  public DoubleVector multiply(DoubleVector s) {
    DoubleVector vec = new SparseBitVector(s.getDimension());
    // take a shortcut by just iterating over the non-zero elements of the
    // smaller vector of both multiplicants.
    DoubleVector smallestVector = s.getLength() < getLength() ? s : this;
    DoubleVector largerVector = smallestVector == this ? s : this;
    Iterator<DoubleVectorElement> it = smallestVector.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement next = it.next();
      double otherValue = largerVector.get(next.getIndex());
      vec.set(next.getIndex(), next.getValue() * otherValue);
    }

    return vec;
  }

  @Override
  public DoubleVector divide(double scalar) {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() / scalar);
    }
    return v;
  }

  @Override
  public DoubleVector divide(DoubleVector vector) {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() / vector.get(e.getIndex()));
    }
    return v;
  }

  @Override
  public DoubleVector divideFrom(DoubleVector vector) {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = vector.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() / get(e.getIndex()));
    }
    return v;
  }

  @Override
  public DoubleVector pow(double x) {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      double value = 0.0d;
      if (x == 2d) {
        value = e.getValue() * e.getValue();
      } else {
        value = FastMath.pow(e.getValue(), x);
      }
      v.set(e.getIndex(), value);
    }
    return v;
  }

  @Override
  public DoubleVector sqrt() {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), FastMath.sqrt(e.getValue()));
    }
    return v;
  }

  @Override
  public DoubleVector log() {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), FastMath.log(e.getValue()));
    }
    return v;
  }

  @Override
  public DoubleVector exp() {
    DoubleVector v = new SparseBitVector(getDimension());
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), FastMath.exp(e.getValue()));
    }
    return v;
  }

  @Override
  public double sum() {
    double sum = 0.0d;
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      sum += e.getValue();
    }
    return sum;
  }

  @Override
  public DoubleVector abs() {
    DoubleVector v = new SparseBitVector(this);
    Iterator<DoubleVectorElement> it = v.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), FastMath.abs(e.getValue()));
    }
    return v;
  }

  @Override
  public DoubleVector divideFrom(double scalar) {
    DoubleVector v = new SparseBitVector(this);
    Iterator<DoubleVectorElement> it = v.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), scalar / e.getValue());
    }
    return v;
  }

  @Override
  public double dot(DoubleVector s) {
    double dotProduct = 0.0d;
    // take a shortcut by just iterating over the non-zero elements of the
    // smaller vector of both multiplicants.
    DoubleVector smallestVector = s.getLength() < getLength() ? s : this;
    DoubleVector largerVector = smallestVector == this ? s : this;
    Iterator<DoubleVectorElement> it = smallestVector.iterateNonZero();

    while (it.hasNext()) {
      DoubleVectorElement next = it.next();
      double d = largerVector.get(next.getIndex());
      dotProduct += d * next.getValue();
    }

    return dotProduct;
  }

  @Override
  public DoubleVector slice(int length) {
    return slice(0, length);
  }

  @Override
  public DoubleVector slice(int start, int end) {
    DoubleVector nv = new SparseBitVector(end - start);
    Iterator<DoubleVectorElement> iterateNonZero = iterateNonZero();
    while (iterateNonZero.hasNext()) {
      DoubleVectorElement next = iterateNonZero.next();
      if (next.getIndex() >= start && next.getIndex() < end) {
        nv.set(next.getIndex() - start, next.getValue());
      }
    }
    return nv;
  }

  @Override
  public DoubleVector sliceByLength(int start, int length) {
    DoubleVector nv = new SparseBitVector(length);
    Iterator<DoubleVectorElement> iterateNonZero = iterateNonZero();
    final int endIndex = start + length;
    while (iterateNonZero.hasNext()) {
      DoubleVectorElement next = iterateNonZero.next();
      if (next.getIndex() >= start && next.getIndex() < endIndex) {
        nv.set(next.getIndex() - start, next.getValue());
      }
    }
    return nv;
  }

  @Override
  public double max() {
    double res = -Double.MAX_VALUE;
    Iterator<DoubleVectorElement> it = this.iterateNonZero();
    int iterated = 0;
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      if (res < e.getValue()) {
        res = e.getValue();
      }
      iterated++;
    }

    // at the end check for zero, because we have skipped zero elements
    if (iterated != getDimension() && res < 0d) {
      res = 0d;
    }
    return res;
  }

  @Override
  public double min() {
    double res = Double.MAX_VALUE;
    Iterator<DoubleVectorElement> it = this.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      if (res > e.getValue()) {
        res = e.getValue();
      }
    }

    return res;
  }

  @Override
  public int maxIndex() {
    int index = 0;
    double res = -Double.MAX_VALUE;
    Iterator<DoubleVectorElement> it = this.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      if (res < e.getValue()) {
        res = e.getValue();
        index = e.getIndex();
      }
    }

    return index;
  }

  @Override
  public int minIndex() {
    int index = 0;
    double res = Double.MAX_VALUE;
    Iterator<DoubleVectorElement> it = this.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      if (res > e.getValue()) {
        res = e.getValue();
        index = e.getIndex();
      }
    }
    return index;
  }

  @Override
  public double[] toArray() {
    double[] d = new double[this.getDimension()];
    Iterator<DoubleVectorElement> it = this.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      d[e.getIndex()] = e.getValue();
    }
    return d;
  }

  @Override
  public String toString() {
    if (getLength() < 50) {
      return vector.toString();
    } else {
      return getDimension() + "x1";
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + vector.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SparseBitVector other = (SparseBitVector) obj;
    if (!vector.equals(other.vector))
      return false;
    return true;
  }

  @Override
  public DoubleVector deepCopy() {
    return new SparseBitVector(this);
  }

  @Override
  public Iterator<DoubleVectorElement> iterateNonZero() {
    return new NonZeroIterator();
  }

  @Override
  public Iterator<DoubleVectorElement> iterate() {
    return new DefaultIterator();
  }

  private final class NonZeroIterator extends
      AbstractIterator<DoubleVectorElement> {

    private final DoubleVectorElement element = new DoubleVectorElement();
    private int index;

    @Override
    protected final DoubleVectorElement computeNext() {
      // look for the element that has a bit set
      while (index < getDimension() && !vector.get(index)) {
        index++;
      }
      if (index < getDimension()) {
        element.setIndex(index);
        element.setValue(get(index));
        index++;
        return element;
      } else {
        return endOfData();
      }
    }

  }

  private final class DefaultIterator extends
      AbstractIterator<DoubleVectorElement> {

    private final DoubleVectorElement element = new DoubleVectorElement();
    private int index = 0;

    @Override
    protected DoubleVectorElement computeNext() {
      if (index < getDimension()) {
        element.setIndex(index);
        element.setValue(get(index));
        index++;
        return element;
      } else {
        return endOfData();
      }
    }

  }

  @Override
  public boolean isNamed() {
    return false;
  }

  @Override
  public boolean isSparse() {
    return true;
  }

  @Override
  public String getName() {
    return null;
  }

}
