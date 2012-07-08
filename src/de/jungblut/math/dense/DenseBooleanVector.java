package de.jungblut.math.dense;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import de.jungblut.math.BooleanVector;

/**
 * Implementation of dense boolean vectors, internally represented as an array
 * of booleans.
 */
public final class DenseBooleanVector implements BooleanVector {

  private final boolean[] vector;

  /**
   * Vector contructor out of a given boolean array. Be aware that the
   * references are just bend over, so there is no deep copy happening here.
   * 
   * @param arr a boolean array.
   */
  public DenseBooleanVector(boolean[] arr) {
    // normally, we should make a deep copy
    this.vector = arr;
  }

  /*
   * (non-Javadoc)
   * @see de.jungblut.math.BooleanVector#get(int)
   */
  @Override
  public final boolean get(int index) {
    return vector[index];
  }

  /*
   * (non-Javadoc)
   * @see de.jungblut.math.BooleanVector#getLength()
   */
  @Override
  public final int getLength() {
    return vector.length;
  }

  /**
   * Internal method to set values at a given index to the given value.
   * 
   * @param index the given index.
   * @param value the given value.
   */
  final void set(int index, boolean value) {
    vector[index] = value;
  }

  /*
   * (non-Javadoc)
   * @see de.jungblut.math.BooleanVector#toArray()
   */
  @Override
  public final boolean[] toArray() {
    return vector;
  }

  @Override
  public final String toString() {
    return Arrays.toString(vector);
  }

  /*
   * (non-Javadoc)
   * @see de.jungblut.math.BooleanVector#iterateNonZero()
   */
  @Override
  public Iterator<BooleanVectorElement> iterateNonZero() {
    return new NonZeroIterator();
  }

  /**
   * Not-false iterator class.
   * 
   */
  private final class NonZeroIterator extends
      AbstractIterator<BooleanVectorElement> {

    private final BooleanVectorElement element = new BooleanVectorElement();
    private final boolean[] array;
    private int currentIndex = 0;

    private NonZeroIterator() {
      this.array = vector;
    }

    @Override
    protected final BooleanVectorElement computeNext() {
      while (!array[currentIndex]) {
        currentIndex++;
        if (currentIndex >= array.length)
          return endOfData();
      }
      element.setIndex(currentIndex);
      element.setValue(array[currentIndex]);
      return element;
    }
  }

}
