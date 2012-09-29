package de.jungblut.math.dense;

import java.util.Iterator;

import junit.framework.TestCase;

import org.junit.Test;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.DoubleVector.DoubleVectorElement;

public class DenseDoubleVectorTest extends TestCase {

  @Test
  public void testAccessors() throws Exception {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    DenseDoubleVector vec = new DenseDoubleVector(arr);
    arrayEquals(arr, vec.toArray());
  }

  @Test
  public void testMultiply() throws Exception {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    double[] res = new double[] { 1, 4, 9, 16, 25 };
    DenseDoubleVector vec = new DenseDoubleVector(arr);
    DoubleVector multiply = vec.multiply(vec);
    arrayEquals(res, multiply.toArray());
  }

  @Test
  public void testPow() throws Exception {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    double[] res = new double[] { 1, 4, 9, 16, 25 };
    double[] res3 = new double[] { 1, 8, 27, 64, 125 };
    DenseDoubleVector vec = new DenseDoubleVector(arr);
    DoubleVector multiply = vec.pow(2);
    arrayEquals(res, multiply.toArray());

    multiply = vec.pow(3);
    arrayEquals(res3, multiply.toArray());
  }
  
  @Test
  public void testIterateNonZero() throws Exception {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    DenseDoubleVector vec = new DenseDoubleVector(arr);
    Iterator<DoubleVectorElement> iterateNonZero = vec.iterateNonZero();
    while(iterateNonZero.hasNext()){
      DoubleVectorElement next = iterateNonZero.next();
      assertEquals(arr[next.getIndex()], next.getValue());
    }
  }

  public void arrayEquals(double[] left, double[] right) {
    assertEquals(left.length, right.length);

    for (int i = 0; i < left.length; i++) {
      assertEquals(left[i], right[i]);
    }
  }
}
