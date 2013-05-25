package de.jungblut.math.sparse;

import java.util.HashSet;

import junit.framework.TestCase;

import org.apache.commons.math3.util.FastMath;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.math.function.DoubleDoubleVectorFunction;
import de.jungblut.math.function.DoubleVectorFunction;

@RunWith(JUnit4.class)
public class SparseDoubleVectorTest extends TestCase {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testConstructor() throws Exception {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    double[] res = new double[] { 25, 1, 2, 3, 4, 5 };
    // check if 25 was added in front
    DoubleVector vec = new SparseDoubleVector(25, arr);
    arrayEquals(res, vec.toArray());
    // check if 25 was added in the back
    res = new double[] { 1, 2, 3, 4, 5, 25 };
    vec = new SparseDoubleVector(arr, 25);
    arrayEquals(res, vec.toArray());

    // fill with default elements
    vec = new DenseDoubleVector(3, 1d);
    res = new double[] { 1, 1, 1 };
    arrayEquals(res, vec.toArray());

    // copy a dense vector
    vec = new SparseDoubleVector(vec);
    arrayEquals(res, vec.toArray());
    // copy a sparse vector
    res = new double[] { 4, 0, 5, 6, 0, 7, 8, 0 };
    vec = new SparseDoubleVector(new SparseDoubleVector(res));
    arrayEquals(res, vec.toArray());
  }

  @Test
  public void testAccessors() throws Exception {
    double[] arr = new double[] { 1, 2, 0, 4, 5 };
    DoubleVector vec = new SparseDoubleVector(arr);
    arrayEquals(arr, vec.toArray());
    assertNull(vec.getName());
    assertEquals(true, vec.isSparse());
    assertEquals(false, vec.isNamed());

  }

  @Test
  public void testApply() {
    double[] arr = new double[] { 1, 2, 0, 3, 4, 5 };
    double[] res = new double[] { 2, 3, 1, 4, 5, 6 };
    DoubleVector vec = new SparseDoubleVector(arr);
    DoubleVector apply = vec.apply(new DoubleVectorFunction() {

      @Override
      public double calculate(int index, double value) {
        // just increment by 1
        return value + 1;
      }
    });

    arrayEquals(apply.toArray(), res);

    DoubleVector resVec = new DenseDoubleVector(res);
    apply = apply.apply(resVec, new DoubleDoubleVectorFunction() {

      @Override
      public double calculate(int index, double left, double right) {
        // difference
        return left - right;
      }
    });

    // check if the sum in this vector is zero
    assertEquals(0d, apply.sum());
  }

  @Test
  public void testAddition() {
    double[] arr = new double[] { 1, 2, 3, 4, 5, 0, 1, -1 };
    DoubleVector vec = new SparseDoubleVector(arr);
    double[] arr2 = new double[] { 2, 3, 4, 5, 6, 1, 0, 1 };
    DoubleVector vec2 = new SparseDoubleVector(arr2);

    DoubleVector summation = vec.add(vec2);
    double[] res = new double[] { 3, 5, 7, 9, 11, 1, 1, 0 };

    arrayEquals(res, summation.toArray());

    summation = summation.add(5d);
    res = new double[] { 8, 10, 12, 14, 16, 6, 6, 5 };

    arrayEquals(res, summation.toArray());

  }

  @Test
  public void testSubtraction() {
    double[] arr = new double[] { 1, 2, 3, 0, 5, 5, 8 };
    double[] arr2 = new double[] { 2, 3, 4, -5, 6, 5, 0 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    SparseDoubleVector vec2 = new SparseDoubleVector(arr2);

    DoubleVector summation = vec.subtract(vec2);
    double[] res = new double[] { -1, -1, -1, 5, -1, 0, 8 };

    arrayEquals(res, summation.toArray());

    summation = summation.subtract(5d);
    res = new double[] { -6, -6, -6, 0, -6, -5, 3 };

    arrayEquals(res, summation.toArray());

    DoubleVector subtractFrom = summation.subtractFrom(15);
    res = new double[] { 21, 21, 21, 15, 21, 20, 12 };
    arrayEquals(res, subtractFrom.toArray());
  }

  @Test
  public void testMultiplication() throws Exception {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    double[] res = new double[] { 1, 4, 9, 16, 25 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    DoubleVector multiply = vec.multiply(vec);
    arrayEquals(res, multiply.toArray());

    DoubleVector multiply2 = multiply.multiply(15);
    res = new double[] { 15, 4 * 15, 9 * 15, 16 * 15, 25 * 15 };
    arrayEquals(res, multiply2.toArray());

    SparseDoubleVector vec2 = new SparseDoubleVector(new double[] { 1, 0, 0, 1,
        0 });
    DoubleVector multiply3 = vec.multiply(vec2);
    res = new double[] { 1, 0, 0, 4, 0 };
    arrayEquals(res, multiply3.toArray());
  }

  @Test
  public void testDivision() {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    double[] arr2 = new double[] { 10, 20, 36, 40, 55 };
    double[] res = new double[] { 10, 10, 12, 10, 11 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    SparseDoubleVector vec2 = new SparseDoubleVector(arr2);
    DoubleVector div = vec2.divide(vec);
    arrayEquals(res, div.toArray());

    DoubleVector multiply2 = div.divide(5);
    res = new double[] { 2, 2, 12d / 5d, 2, 11d / 5d };
    arrayEquals(res, multiply2.toArray());

    div = new SparseDoubleVector(new double[] { 16, 40, 10 });
    DoubleVector divFrom = div.divideFrom(8);
    res = new double[] { 0.5, 0.2, 0.8 };
    arrayEquals(res, divFrom.toArray());

    div = new SparseDoubleVector(new double[] { 16, 40, 10, 50, 50 });
    divFrom = div.divideFrom(new SparseDoubleVector(new double[] { 1, 2, 3, 4,
        5 }));
    res = new double[] { 1d / 16d, 2d / 40d, 3d / 10d, 4d / 50d, 5d / 50d };
    arrayEquals(res, divFrom.toArray());

  }

  @Test
  public void testPow() throws Exception {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    double[] res = new double[] { 1, 4, 9, 16, 25 };
    double[] res3 = new double[] { 1, 8, 27, 64, 125 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    DoubleVector multiply = vec.pow(2);
    arrayEquals(res, multiply.toArray());

    multiply = vec.pow(3);
    arrayEquals(res3, multiply.toArray());
  }

  @Test
  public void testSqrt() {
    double[] arr = new double[] { 4, 9, 16, 49 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    double[] res = new double[] { 2, 3, 4, 7 };

    arrayEquals(res, vec.sqrt().toArray());
  }

  @Test
  public void testLog() {
    double[] arr = new double[] { 4, 9, 16, 49 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    double[] res = new double[] { FastMath.log(4), FastMath.log(9),
        FastMath.log(16), FastMath.log(49) };

    arrayEquals(res, vec.log().toArray());
  }

  @Test
  public void testExp() {
    double[] arr = new double[] { 4, 9, 16, 49 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    double[] res = new double[] { FastMath.exp(4), FastMath.exp(9),
        FastMath.exp(16), FastMath.exp(49) };

    arrayEquals(res, vec.exp().toArray());
  }

  @Test
  public void testAbs() {
    double[] arr = new double[] { -4, 9, -16, 49 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    double[] res = new double[] { 4, 9, 16, 49 };

    arrayEquals(res, vec.abs().toArray());
  }

  @Test
  public void testSum() {
    double[] arr = new double[] { 1, 2, 3, 0, 0, 0, 0, 4, 5 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    assertEquals(15d, vec.sum());
  }

  @Test
  public void testDot() {
    double[] arr = new double[] { 1, 2, 3, 0, 0, 0, 4, 5 };
    double[] arr2 = new double[] { 1, 4, 9, 1, 0, 0, 16, 25 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    SparseDoubleVector vec2 = new SparseDoubleVector(arr2);

    assertEquals(225d, vec.dot(vec2));

    // reverse to test the inner conditioning
    vec = new SparseDoubleVector(arr2);
    vec2 = new SparseDoubleVector(arr);

    assertEquals(225d, vec.dot(vec2));
  }

  @Test
  public void testSlicing() {
    DoubleVector v = new SparseDoubleVector(new double[] { 1, 2, 0, 0, 0, 3, 4,
        5 });
    DoubleVector slice = v.slice(4);
    assertEquals(2, slice.getLength());
    assertEquals(4, slice.getDimension());
    double[] res = new double[] { 1, 2, 0, 0 };
    arrayEquals(res, slice.toArray());

    slice = v.slice(4, 8);
    assertEquals(3, slice.getLength());
    assertEquals(4, slice.getDimension());
    res = new double[] { 0, 3, 4, 5 };
    arrayEquals(res, slice.toArray());

    DoubleVector sliceByLength = v.sliceByLength(0, 4);
    assertEquals(2, sliceByLength.getLength());
    assertEquals(4, sliceByLength.getDimension());
    res = new double[] { 1, 2, 0, 0 };
    arrayEquals(res, sliceByLength.toArray());

  }

  @Test
  public void testMax() {
    DoubleVector v = new SparseDoubleVector(new double[] { 1, 0, 0, 0, 0, 0, 2,
        3, 5, 0, 4 });

    assertEquals(5d, v.max());
    assertEquals(8, v.maxIndex());

  }

  @Test
  public void testMin() {
    DoubleVector v = new SparseDoubleVector(new double[] { 2, -1, 3, 0, 0, 0,
        0, 0, 4, 5 });
    assertEquals(-1d, v.min());
    assertEquals(1, v.minIndex());

  }

  @Test
  public void testToString() {
    double[] arr = new double[] { 1, 2, 3, 4, 5 };
    SparseDoubleVector vec = new SparseDoubleVector(arr);
    assertEquals("{4=5.0, 3=4.0, 2=3.0, 1=2.0, 0=1.0}", vec.toString());
    vec = new SparseDoubleVector(new DenseDoubleVector(51, 1d));
    assertEquals("51x1", vec.toString());

  }

  @Test
  public void testEquality() {
    SparseDoubleVector dv = new SparseDoubleVector(
        new double[] { 1, 2, 3, 4, 5 });
    assertEquals(false, dv.equals(null));
    assertEquals(false, dv.equals("blabla"));
    assertEquals(true, dv.equals(dv));
    assertEquals(true,
        dv.equals(new SparseDoubleVector(new double[] { 1, 2, 3, 4, 5 })));

    assertEquals(false,
        dv.equals(new SparseDoubleVector(new double[] { 1, 2, 5, 4, 5 })));

  }

  @Test
  public void testHashing() {
    HashSet<DoubleVector> set = new HashSet<DoubleVector>();

    set.add(new SparseDoubleVector(new double[] { 1, 2, 3, 4, 5 }));
    set.add(new SparseDoubleVector(new double[] { 1, 2, 6, 4, 5 }));
    set.add(new SparseDoubleVector(new double[] { 1, 2, 3, 4, 5 }));

    assertEquals(2, set.size());

  }

  @Test
  public void testDeepCopy() {
    SparseDoubleVector mat = new SparseDoubleVector(new double[] { 1, 0 });
    DoubleVector deepCopy = mat.deepCopy();
    assertNotSame(mat, deepCopy);
    arrayEquals(deepCopy.toArray(), mat.toArray());
  }

  public void arrayEquals(double[] left, double[] right) {
    assertEquals(left.length, right.length);

    for (int i = 0; i < left.length; i++) {
      assertEquals(left[i], right[i], 1e-5);
    }
  }

}
