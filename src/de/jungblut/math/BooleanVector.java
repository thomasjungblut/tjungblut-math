package de.jungblut.math;

import java.util.Iterator;

/**
 * Vector consisting of booleans.
 */
public interface BooleanVector {

  /**
   * Gets the boolean at the given index.
   */
  public boolean get(int index);

  /**
   * Returns the length of this vector.
   */
  public int getLength();

  /**
   * Returns an array representation of the inner values.
   */
  public boolean[] toArray();

  /**
   * Iterates over the not-false elements in this vector.
   */
  public Iterator<BooleanVectorElement> iterateNonZero();

  /**
   * Element class for iterating.
   */
  public static final class BooleanVectorElement {

    private int index;
    private boolean value;

    public BooleanVectorElement() {
      super();
    }

    public BooleanVectorElement(int index, boolean value) {
      super();
      this.index = index;
      this.value = value;
    }

    /**
     * @return the index of the current element.
     */
    public final int getIndex() {
      return index;
    }

    /**
     * @return the value of the vector at the current index.
     */
    public final boolean getValue() {
      return value;
    }

    public final void setIndex(int in) {
      this.index = in;
    }

    public final void setValue(boolean in) {
      this.value = in;
    }
  }

}
