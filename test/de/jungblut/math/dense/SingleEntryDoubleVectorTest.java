package de.jungblut.math.dense;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.DoubleVector.DoubleVectorElement;
import de.jungblut.math.function.DoubleDoubleVectorFunction;
import de.jungblut.math.function.DoubleVectorFunction;

public class SingleEntryDoubleVectorTest {

  @Test
  public void testConstruction() {
    SingleEntryDoubleVector singleEntryDoubleVector = new SingleEntryDoubleVector(
        5d);
    assertEquals(5d, singleEntryDoubleVector.get(0), 0d);
    assertEquals(1, singleEntryDoubleVector.getDimension());
    assertEquals(1, singleEntryDoubleVector.getLength());
  }

  @Test
  public void testApply() {
    DoubleVector vec = new SingleEntryDoubleVector(5d);
    vec = vec.apply(new DoubleVectorFunction() {

      @Override
      public double calculate(int index, double value) {
        return value * 2;
      }
    });
    assertEquals(10d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

    DoubleVector vec2 = new SingleEntryDoubleVector(5d);
    vec2 = vec.apply(vec2, new DoubleDoubleVectorFunction() {

      @Override
      public double calculate(int index, double left, double right) {
        return left + right;
      }
    });
    assertEquals(15d, vec2.get(0), 0d);
    assertEquals(1, vec2.getDimension());
    assertEquals(1, vec2.getLength());

  }

  @Test
  public void testAdd() {
    DoubleVector vec = new SingleEntryDoubleVector(5d);
    vec = vec.add(5);
    assertEquals(10d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

    DoubleVector vec2 = new SingleEntryDoubleVector(5d);
    vec2 = vec.add(vec2);
    assertEquals(15d, vec2.get(0), 0d);
    assertEquals(1, vec2.getDimension());
    assertEquals(1, vec2.getLength());

  }

  @Test
  public void testSubtract() {
    DoubleVector vec = new SingleEntryDoubleVector(7d);
    vec = vec.subtract(5);
    assertEquals(2d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

    DoubleVector vec2 = new SingleEntryDoubleVector(15d);
    vec2 = vec.subtract(vec2);
    assertEquals(-13d, vec2.get(0), 0d);
    assertEquals(1, vec2.getDimension());
    assertEquals(1, vec2.getLength());

    vec2 = vec2.subtractFrom(10);
    assertEquals(23d, vec2.get(0), 0d);
    assertEquals(1, vec2.getDimension());
    assertEquals(1, vec2.getLength());

  }

  @Test
  public void testMultiply() {
    DoubleVector vec = new SingleEntryDoubleVector(5d);
    vec = vec.multiply(5);
    assertEquals(25d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

    DoubleVector vec2 = new SingleEntryDoubleVector(5d);
    vec2 = vec.multiply(vec2);
    assertEquals(5d * 25d, vec2.get(0), 0d);
    assertEquals(1, vec2.getDimension());
    assertEquals(1, vec2.getLength());

  }

  @Test
  public void testDivide() {
    DoubleVector vec = new SingleEntryDoubleVector(10d);
    vec = vec.divide(5);
    assertEquals(2d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

    DoubleVector vec2 = new SingleEntryDoubleVector(5d);
    vec2 = vec.divide(vec2);
    assertEquals(2 / 5d, vec2.get(0), 0d);
    assertEquals(1, vec2.getDimension());
    assertEquals(1, vec2.getLength());

    vec2 = vec2.divideFrom(100);
    assertEquals(250, vec2.get(0), 0d);
    assertEquals(1, vec2.getDimension());
    assertEquals(1, vec2.getLength());

    SingleEntryDoubleVector vec3 = new SingleEntryDoubleVector(5d);
    vec2 = vec2.divideFrom(vec3);
    assertEquals(5 / 250d, vec2.get(0), 0d);
    assertEquals(1, vec2.getDimension());
    assertEquals(1, vec2.getLength());

  }

  @Test
  public void testPow() {
    DoubleVector vec = new SingleEntryDoubleVector(10d);
    vec = vec.pow(2);
    assertEquals(100d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

    vec = new SingleEntryDoubleVector(10d);
    vec = vec.pow(3);
    assertEquals(1000d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

  }

  @Test
  public void testAbs() {
    DoubleVector vec = new SingleEntryDoubleVector(10d);
    vec = vec.abs();
    assertEquals(10d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

    vec = new SingleEntryDoubleVector(-10d);
    vec = vec.abs();
    assertEquals(10d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

  }

  @Test
  public void testSqrt() {
    DoubleVector vec = new SingleEntryDoubleVector(100d);
    vec = vec.sqrt();
    assertEquals(10d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

  }

  @Test
  public void testLog() {
    DoubleVector vec = new SingleEntryDoubleVector(Math.E);
    vec = vec.log();
    assertEquals(1d, vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

  }

  @Test
  public void testExp() {
    DoubleVector vec = new SingleEntryDoubleVector(Math.E);
    vec = vec.exp();
    assertEquals(Math.exp(Math.E), vec.get(0), 0d);
    assertEquals(1, vec.getDimension());
    assertEquals(1, vec.getLength());

  }

  @Test
  public void testSum() {
    DoubleVector vec = new SingleEntryDoubleVector(Math.E);
    double sum = vec.sum();
    assertEquals(Math.E, sum, 0d);

  }

  @Test
  public void testDot() {
    DoubleVector vec = new SingleEntryDoubleVector(5d);
    DoubleVector vec2 = new SingleEntryDoubleVector(5d);
    double res = vec.dot(vec2);
    assertEquals(5d * 5d, res, 0d);

  }

  @Test
  public void testMinMax() {
    DoubleVector vec = new SingleEntryDoubleVector(5d);
    assertEquals(5d, vec.min(), 0d);
    assertEquals(5d, vec.max(), 0d);

    assertEquals(0, vec.minIndex());
    assertEquals(0, vec.maxIndex());
  }

  @Test
  public void testToArray() {
    DoubleVector vec = new SingleEntryDoubleVector(5d);
    double[] array = vec.toArray();
    assertEquals(1, array.length);
    assertEquals(array[0], 5d, 0d);
  }

  @Test
  public void testIteration() {
    DoubleVector vec = new SingleEntryDoubleVector(5d);
    Iterator<DoubleVectorElement> iterate = vec.iterate();
    int numItems = 0;
    while (iterate.hasNext()) {
      DoubleVectorElement next = iterate.next();
      assertEquals(next.getIndex(), 0);
      assertEquals(next.getValue(), 5d, 0d);
      numItems++;
    }
    assertEquals(1, numItems);
  }

  @Test
  public void testNonZeroIteration() {
    DoubleVector vec = new SingleEntryDoubleVector(5d);
    Iterator<DoubleVectorElement> iterate = vec.iterate();
    int numItems = 0;
    while (iterate.hasNext()) {
      DoubleVectorElement next = iterate.next();
      assertEquals(next.getIndex(), 0);
      assertEquals(next.getValue(), 5d, 0d);
      numItems++;
    }
    assertEquals(1, numItems);

    vec = new SingleEntryDoubleVector(0d);
    iterate = vec.iterateNonZero();
    numItems = 0;
    while (iterate.hasNext()) {
      DoubleVectorElement next = iterate.next();
      assertEquals(next.getIndex(), 0);
      assertEquals(next.getValue(), 5d, 0d);
      numItems++;
    }
    assertEquals(0, numItems);
  }

}
