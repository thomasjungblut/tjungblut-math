package de.jungblut.math.tuple;

import java.util.HashSet;

import junit.framework.TestCase;

import org.junit.Test;

import de.jungblut.math.DoubleVector;

public class Tuple3Test extends TestCase {

  @Test
  public void testTuple() {
    Tuple3<Integer, String, Double> tp = new Tuple3<>(1, "abc", 2.0d);

    assertEquals(1, tp.getFirst().intValue());
    assertEquals("abc", tp.getSecond());
    assertEquals(2.0d, tp.getThird());
  }

  @Test
  public void testHashing() {
    HashSet<Tuple3<Integer, String, Double>> set = new HashSet<>();

    set.add(new Tuple3<>(1, "lol", 9.5));
    set.add(new Tuple3<>(2, "lol", 0.1));
    set.add(new Tuple3<>(1, "lolomg", 0d));

    assertEquals(2, set.size());
    assertEquals(true, set.contains(new Tuple3<>(1, "okay", 3.1)));

    // test null keys
    set = new HashSet<>();

    set.add(new Tuple3<Integer, String, Double>(null, "lol", 9.5));
    set.add(new Tuple3<Integer, String, Double>(null, "lol", 0.1));
    assertEquals(1, set.size());
    assertEquals(true,
        set.contains(new Tuple3<Integer, String, Double>(null, "okay", 3.1)));

  }

  @Test
  public void testEquality() {
    Tuple3<Integer, String, Double> dv = new Tuple3<>(1, "lol", 9.5);
    assertEquals(false, dv.equals(null));
    assertEquals(false, dv.equals("blabla"));
    assertEquals(true, dv.equals(dv));
    assertEquals(true, dv.equals(new Tuple3<>(1, "lol", 9.5)));
    assertEquals(false, dv.equals(new Tuple3<>(2, "lol", 9.5)));

    // test nulls
    dv = new Tuple3<>(null, "lol", 2d);
    assertEquals(false, dv.equals(null));
    assertEquals(false, dv.equals("blabla"));
    assertEquals(true, dv.equals(dv));
    assertEquals(false, dv.equals(new Tuple3<>(1, "lol", 2d)));
    assertEquals(true,
        dv.equals(new Tuple3<Integer, String, Double>(null, "lol", 2d)));
  }

  @Test
  public void testComparable() {
    Tuple3<Integer, String, Double> tuple = new Tuple3<>(1, "lol", 0.1);
    Tuple3<Integer, String, Double> tuple2 = new Tuple3<>(2, "lol", 0.3);

    assertEquals(-1, tuple.compareTo(tuple2));
    assertEquals(0, tuple.compareTo(tuple));
    assertEquals(1, tuple2.compareTo(tuple));

    Tuple3<DoubleVector, String, Double> tx = new Tuple3<>(null, "lol", 0.1);
    Tuple3<DoubleVector, String, Double> tx2 = new Tuple3<>(null, "lol", 0.3);

    assertEquals(0, tx2.compareTo(tx));

  }

  @Test
  public void testToString() {
    Tuple3<Integer, String, Double> tuple = new Tuple3<>(1, "lol", 0.1d);
    assertEquals("Tuple3 [first=1, second=lol, third=0.1]", tuple.toString());
  }

}
