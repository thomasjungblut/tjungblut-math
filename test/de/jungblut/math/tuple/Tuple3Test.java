package de.jungblut.math.tuple;

import java.util.HashSet;

import junit.framework.TestCase;

import org.junit.Test;

public class Tuple3Test extends TestCase {

  @Test
  public void testTuple() {
    Tuple3<Integer, String, Double> tp = new Tuple3<Integer, String, Double>(1,
        "abc", 2.0d);

    assertEquals(1, tp.getFirst().intValue());
    assertEquals("abc", tp.getSecond());
    assertEquals(2.0d, tp.getThird());
  }

  @Test
  public void testHashing() {
    HashSet<Tuple3<Integer, String, Double>> set = new HashSet<Tuple3<Integer, String, Double>>();

    set.add(new Tuple3<Integer, String, Double>(1, "lol", 9.5));
    set.add(new Tuple3<Integer, String, Double>(2, "lol", 0.1));
    set.add(new Tuple3<Integer, String, Double>(1, "lolomg", 0d));

    assertEquals(2, set.size());
    assertEquals(true,
        set.contains(new Tuple3<Integer, String, Double>(1, "okay", 3.1)));

  }

  @Test
  public void testComparable() {
    Tuple3<Integer, String, Double> tuple = new Tuple3<Integer, String, Double>(
        1, "lol", 0.1);
    Tuple3<Integer, String, Double> tuple2 = new Tuple3<Integer, String, Double>(
        2, "lol", 0.3);

    assertEquals(-1, tuple.compareTo(tuple2));
    assertEquals(0, tuple.compareTo(tuple));
    assertEquals(1, tuple2.compareTo(tuple));

  }

  @Test
  public void testToString() {
    Tuple3<Integer, String, Double> tuple = new Tuple3<Integer, String, Double>(
        1, "lol", 0.1d);
    assertEquals("Tuple3 [first=1, second=lol, third=0.1]", tuple.toString());
  }

}
