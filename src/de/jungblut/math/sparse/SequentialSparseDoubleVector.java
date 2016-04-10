package de.jungblut.math.sparse;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.google.common.collect.AbstractIterator;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.function.DoubleDoubleVectorFunction;
import de.jungblut.math.function.DoubleVectorFunction;

/**
 * Sparse double vector backed by two parallel arrays (one for indices and one
 * for values).
 * 
 * @author thomas.jungblut
 * 
 */
public final class SequentialSparseDoubleVector implements DoubleVector {

  private final int dimension;
  private final OrderedIntDoubleMapping mapping;

  /**
   * Constructs a new {@link SequentialSparseDoubleVector}.
   * 
   * @param dimension the expected dimensionality of the vector.
   */
  public SequentialSparseDoubleVector(int dimension) {
    this.dimension = dimension;
    this.mapping = new OrderedIntDoubleMapping();
  }

  /**
   * Constructs a new {@link SequentialSparseDoubleVector}.
   * 
   * @param expectedLength the expected length of the vector
   * @param dimension the expected dimensionality of the vector.
   */
  public SequentialSparseDoubleVector(int dimension, int expectedLength) {
    this.dimension = dimension;
    this.mapping = new OrderedIntDoubleMapping(expectedLength);
  }

  /**
   * Constructs a new {@link SequentialSparseDoubleVector}.
   * 
   * @param v the given vector to copy.
   */
  public SequentialSparseDoubleVector(DoubleVector v) {
    this(v.getDimension(), v.getLength());
    if (v.isSparse()) {
      SequentialSparseDoubleVector vx = (SequentialSparseDoubleVector) v;
      mapping.copyInternalState(vx.mapping);
    } else {
      Iterator<DoubleVectorElement> iterateNonZero = v.iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement next = iterateNonZero.next();
        mapping.set(next.getIndex(), next.getValue());
      }
    }
  }

  /**
   * Constructs a new {@link SequentialSparseDoubleVector}.
   * 
   * @param arr the given vector to copy.
   */
  public SequentialSparseDoubleVector(double[] arr) {
    this(arr.length);
    for (int i = 0; i < arr.length; i++) {
      mapping.set(i, arr[i]);
    }
  }

  /**
   * Creates a new vector with the given array and the first value firstElement.
   * 
   * @param firstElement the element that will be at index 0 (first position) in
   *          the resulting vector.
   * @param array the rest of the array for the vector.
   */
  public SequentialSparseDoubleVector(double firstElement, double[] arr) {
    this(arr.length + 1);
    mapping.set(0, firstElement);
    for (int i = 0; i < arr.length; i++) {
      mapping.set(i + 1, arr[i]);
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
  public SequentialSparseDoubleVector(double[] arr, double lastValue) {
    this(arr.length + 1);
    for (int i = 0; i < arr.length; i++) {
      mapping.set(i, arr[i]);
    }
    mapping.set(arr.length, lastValue);
  }

  @Override
  public double get(int index) {
    return mapping.get(index);
  }

  @Override
  public int getLength() {
    return mapping.getNumMappings();
  }

  @Override
  public int getDimension() {
    return dimension;
  }

  @Override
  public void set(int index, double value) {
    mapping.set(index, value);
  }

  @Override
  public DoubleVector apply(DoubleVectorFunction func) {
    SequentialSparseDoubleVector newV = new SequentialSparseDoubleVector(this);
    Iterator<DoubleVectorElement> iterate = iterate();
    while (iterate.hasNext()) {
      DoubleVectorElement next = iterate.next();
      double res = func.calculate(next.getIndex(), next.getValue());
      newV.set(next.getIndex(), res);
    }
    return newV;
  }

  @Override
  public DoubleVector apply(DoubleVector other, DoubleDoubleVectorFunction func) {
    SequentialSparseDoubleVector newV = new SequentialSparseDoubleVector(this);
    Iterator<DoubleVectorElement> iterate = iterate();
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

    DoubleVector result = new SequentialSparseDoubleVector(this);
    Iterator<DoubleVectorElement> iter = other.iterateNonZero();
    while (iter.hasNext()) {
      DoubleVectorElement e = iter.next();
      int index = e.getIndex();
      result.set(index, get(index) + e.getValue());
    }

    return result;
  }

  @Override
  public DoubleVector add(double scalar) {
    DoubleVector v = new SequentialSparseDoubleVector(dimension,
        mapping.getNumMappings());
    Iterator<DoubleVectorElement> it = iterate();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() + scalar);
    }
    return v;
  }

  @Override
  public DoubleVector subtract(DoubleVector other) {
    SequentialSparseDoubleVector result = new SequentialSparseDoubleVector(this);
    if (other.isSparse() && other instanceof SequentialSparseDoubleVector) {
      SequentialSparseDoubleVector vec = (SequentialSparseDoubleVector) other;
      result.mapping.merge(vec.mapping, (l, r) -> l - r);
    } else {
      Iterator<DoubleVectorElement> iter = other.iterateNonZero();
      while (iter.hasNext()) {
        DoubleVectorElement e = iter.next();
        int index = e.getIndex();
        result.set(index, result.get(index) - e.getValue());
      }
    }
    return result;
  }

  @Override
  public DoubleVector subtract(double scalar) {
    DoubleVector v = new SequentialSparseDoubleVector(dimension,
        mapping.getNumMappings());
    Iterator<DoubleVectorElement> it = iterate();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() - scalar);
    }
    return v;
  }

  @Override
  public DoubleVector subtractFrom(double scalar) {
    DoubleVector v = new SequentialSparseDoubleVector(dimension,
        mapping.getNumMappings());
    Iterator<DoubleVectorElement> it = iterate();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), scalar - e.getValue());
    }
    return v;
  }

  @Override
  public DoubleVector multiply(double scalar) {
    DoubleVector result = new SequentialSparseDoubleVector(this);
    Iterator<DoubleVectorElement> iter = result.iterateNonZero();
    while (iter.hasNext()) {
      DoubleVectorElement e = iter.next();
      int index = e.getIndex();
      result.set(index, scalar * e.getValue());
    }

    return result;
  }

  @Override
  public DoubleVector multiply(DoubleVector s) {
    // take a shortcut by just iterating over the non-zero elements of the
    // smaller vector of both multiplicants.
    DoubleVector smallestVector = s.getLength() < getLength() ? s : this;
    DoubleVector vec = new SequentialSparseDoubleVector(s.getDimension());
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
    DoubleVector result = new SequentialSparseDoubleVector(this);
    Iterator<DoubleVectorElement> iter = result.iterateNonZero();
    while (iter.hasNext()) {
      DoubleVectorElement e = iter.next();
      int index = e.getIndex();
      result.set(index, e.getValue() / scalar);
    }

    return result;
  }

  @Override
  public DoubleVector divide(DoubleVector vector) {
    DoubleVector v = new SequentialSparseDoubleVector(this);
    Iterator<DoubleVectorElement> it = iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() / vector.get(e.getIndex()));
    }
    return v;
  }

  @Override
  public DoubleVector divideFrom(DoubleVector vector) {
    DoubleVector v = new SequentialSparseDoubleVector(this);
    Iterator<DoubleVectorElement> it = vector.iterateNonZero();
    while (it.hasNext()) {
      DoubleVectorElement e = it.next();
      v.set(e.getIndex(), e.getValue() / get(e.getIndex()));
    }
    return v;
  }

  @Override
  public DoubleVector divideFrom(double scalar) {
    DoubleVector result = new SequentialSparseDoubleVector(this);
    Iterator<DoubleVectorElement> iter = result.iterateNonZero();
    while (iter.hasNext()) {
      DoubleVectorElement e = iter.next();
      int index = e.getIndex();
      result.set(index, scalar / e.getValue());
    }

    return result;
  }

  @Override
  public DoubleVector pow(double x) {
    SequentialSparseDoubleVector v = new SequentialSparseDoubleVector(this);
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      double value = mapping.getValues()[i];
      if (x == 2d) {
        value = value * value;
      } else {
        value = FastMath.pow(value, x);
      }
      v.mapping.getValues()[i] = value;
    }
    return v;
  }

  @Override
  public DoubleVector sqrt() {
    SequentialSparseDoubleVector v = new SequentialSparseDoubleVector(this);
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      v.mapping.getValues()[i] = FastMath.sqrt(mapping.getValues()[i]);
    }
    return v;
  }

  @Override
  public DoubleVector log() {
    SequentialSparseDoubleVector v = new SequentialSparseDoubleVector(this);
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      v.mapping.getValues()[i] = FastMath.log(mapping.getValues()[i]);
    }
    return v;
  }

  @Override
  public DoubleVector exp() {
    SequentialSparseDoubleVector v = new SequentialSparseDoubleVector(this);
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      v.mapping.getValues()[i] = FastMath.exp(mapping.getValues()[i]);
    }
    return v;
  }

  @Override
  public double sum() {
    double sum = 0.0d;
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      sum += mapping.getValues()[i];
    }
    return sum;
  }

  @Override
  public DoubleVector abs() {
    SequentialSparseDoubleVector v = new SequentialSparseDoubleVector(this);
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      v.mapping.getValues()[i] = FastMath.abs(mapping.getValues()[i]);
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
    DoubleVector nv = new SequentialSparseDoubleVector(end - start);
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
    DoubleVector nv = new SequentialSparseDoubleVector(length, length);
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
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      if (res < mapping.getValues()[i]) {
        res = mapping.getValues()[i];
      }
    }

    // at the end check for zero, because we have skipped zero elements
    if (mapping.getNumMappings() != getDimension() && res == 0d) {
      res = 0d;
    }
    return res;
  }

  @Override
  public double min() {
    double res = Double.MAX_VALUE;
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      if (res > mapping.getValues()[i]) {
        res = mapping.getValues()[i];
      }
    }

    return res;
  }

  @Override
  public int maxIndex() {
    int index = 0;
    double res = -Double.MAX_VALUE;
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      if (res < mapping.getValues()[i]) {
        res = mapping.getValues()[i];
        index = mapping.getIndices()[i];
      }
    }

    return index;
  }

  @Override
  public int minIndex() {
    int index = 0;
    double res = Double.MAX_VALUE;
    for (int i = 0; i < mapping.getNumMappings(); i++) {
      if (res > mapping.getValues()[i]) {
        res = mapping.getValues()[i];
        index = mapping.getIndices()[i];
      }
    }
    return index;
  }

  @Override
  public double[] toArray() {
    double[] d = new double[dimension];
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
      StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < mapping.getNumMappings(); i++) {
        sb.append(mapping.getIndices()[i]);
        sb.append('=');
        sb.append(mapping.getValues()[i]);
        if (i != mapping.getNumMappings() - 1) {
          sb.append(", ");
        }
      }
      sb.append(']');
      return sb.toString();
    } else {
      return getDimension() + "x1";
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + dimension;
    result = prime * result + Arrays.hashCode(mapping.getIndices());
    result = prime * result + Arrays.hashCode(mapping.getValues());
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
    SequentialSparseDoubleVector other = (SequentialSparseDoubleVector) obj;
    if (dimension != other.dimension)
      return false;
    if (!Arrays.equals(mapping.getIndices(), other.mapping.getIndices()))
      return false;
    if (!Arrays.equals(mapping.getValues(), other.mapping.getValues()))
      return false;
    return true;
  }

  @Override
  public DoubleVector deepCopy() {
    return new SequentialSparseDoubleVector(this);
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
    private int currentIndex = 0;

    @Override
    protected final DoubleVectorElement computeNext() {
      if (currentIndex < mapping.getNumMappings()) {
        element.setIndex(mapping.getIndices()[currentIndex]);
        element.setValue(mapping.getValues()[currentIndex]);
        currentIndex++;
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
  public boolean isSingle() {
    return false;
  }

  @Override
  public String getName() {
    return null;
  }
}
