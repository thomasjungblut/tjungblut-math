package de.jungblut.math.dense;

import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.google.common.collect.AbstractIterator;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.function.DoubleDoubleVectorFunction;
import de.jungblut.math.function.DoubleVectorFunction;

/**
 * Single entry vector with just a single double at vector index 0. Setting
 * values is disallowed, getting values of illegal boundaries will return 0.
 * 
 * @author thomas.jungblut
 * 
 */
public final class SingleEntryDoubleVector implements DoubleVector {

  private final double value;

  public SingleEntryDoubleVector(double value) {
    this.value = value;
  }

  @Override
  public double get(int index) {
    return index == 0 ? value : 0d;
  }

  @Override
  public int getLength() {
    return 1;
  }

  @Override
  public int getDimension() {
    return 1;
  }

  @Override
  public void set(int index, double value) {
    throw new IllegalStateException("Can't mutate this single entry vector!");
  }

  @Override
  public DoubleVector apply(DoubleVectorFunction func) {
    return new SingleEntryDoubleVector(func.calculate(0, value));
  }

  @Override
  public DoubleVector apply(DoubleVector other, DoubleDoubleVectorFunction func) {
    return new SingleEntryDoubleVector(func.calculate(0, value, other.get(0)));
  }

  @Override
  public DoubleVector add(DoubleVector v) {
    return new SingleEntryDoubleVector(value + v.get(0));
  }

  @Override
  public DoubleVector add(double scalar) {
    return new SingleEntryDoubleVector(value + scalar);
  }

  @Override
  public DoubleVector subtract(DoubleVector v) {
    return new SingleEntryDoubleVector(value - v.get(0));
  }

  @Override
  public DoubleVector subtract(double scalar) {
    return new SingleEntryDoubleVector(value - scalar);
  }

  @Override
  public DoubleVector subtractFrom(double scalar) {
    return new SingleEntryDoubleVector(scalar - value);
  }

  @Override
  public DoubleVector multiply(double scalar) {
    return new SingleEntryDoubleVector(value * scalar);
  }

  @Override
  public DoubleVector multiply(DoubleVector vector) {
    return new SingleEntryDoubleVector(value * vector.get(0));
  }

  @Override
  public DoubleVector divide(double scalar) {
    return new SingleEntryDoubleVector(value / scalar);
  }

  @Override
  public DoubleVector divideFrom(double scalar) {
    return new SingleEntryDoubleVector(scalar / value);
  }

  @Override
  public DoubleVector divideFrom(DoubleVector vector) {
    return new SingleEntryDoubleVector(vector.get(0) / value);
  }

  @Override
  public DoubleVector divide(DoubleVector vector) {
    return new SingleEntryDoubleVector(value / vector.get(0));
  }

  @Override
  public DoubleVector pow(double x) {
    return new SingleEntryDoubleVector(x == 2d ? value * value : FastMath.pow(
        value, x));
  }

  @Override
  public DoubleVector abs() {
    return new SingleEntryDoubleVector(Math.abs(value));
  }

  @Override
  public DoubleVector sqrt() {
    return new SingleEntryDoubleVector(FastMath.sqrt(value));
  }

  @Override
  public DoubleVector log() {
    return new SingleEntryDoubleVector(FastMath.log(value));
  }

  @Override
  public DoubleVector exp() {
    return new SingleEntryDoubleVector(FastMath.exp(value));
  }

  @Override
  public double sum() {
    return value;
  }

  @Override
  public double dot(DoubleVector s) {
    return value * s.get(0);
  }

  @Override
  public DoubleVector slice(int end) {
    return this;
  }

  @Override
  public DoubleVector slice(int start, int end) {
    return this;
  }

  @Override
  public DoubleVector sliceByLength(int start, int length) {
    return this;
  }

  @Override
  public double max() {
    return value;
  }

  @Override
  public double min() {
    return value;
  }

  @Override
  public int maxIndex() {
    return 0;
  }

  @Override
  public int minIndex() {
    return 0;
  }

  @Override
  public double[] toArray() {
    return new double[] { value };
  }

  @Override
  public DoubleVector deepCopy() {
    return new SingleEntryDoubleVector(value);
  }

  @Override
  public Iterator<DoubleVectorElement> iterateNonZero() {
    return new AbstractIterator<DoubleVector.DoubleVectorElement>() {
      boolean done = false;

      @Override
      protected DoubleVectorElement computeNext() {
        if (value == 0d || done) {
          return endOfData();
        }
        done = true;
        DoubleVectorElement doubleVectorElement = new DoubleVectorElement();
        doubleVectorElement.setIndex(0);
        doubleVectorElement.setValue(value);
        return doubleVectorElement;
      }
    };
  }

  @Override
  public Iterator<DoubleVectorElement> iterate() {
    return new AbstractIterator<DoubleVector.DoubleVectorElement>() {
      boolean done = false;

      @Override
      protected DoubleVectorElement computeNext() {
        if (done) {
          return endOfData();
        }
        done = true;
        DoubleVectorElement doubleVectorElement = new DoubleVectorElement();
        doubleVectorElement.setIndex(0);
        doubleVectorElement.setValue(value);
        return doubleVectorElement;
      }
    };
  }

  @Override
  public boolean isSparse() {
    return false;
  }

  @Override
  public boolean isNamed() {
    return false;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(this.value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    SingleEntryDoubleVector other = (SingleEntryDoubleVector) obj;
    if (Double.doubleToLongBits(this.value) != Double
        .doubleToLongBits(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return this.value + "";
  }

}
