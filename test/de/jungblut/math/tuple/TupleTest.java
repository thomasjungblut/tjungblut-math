package de.jungblut.math.tuple;

import java.util.HashSet;

import junit.framework.TestCase;

import org.junit.Test;

import de.jungblut.math.DoubleVector;

public class TupleTest extends TestCase {

  @Test
  public void testTuple() {
    Tuple<Integer, String> tp = new Tuple<Integer, String>(1, "abc");

    assertEquals(1, tp.getFirst().intValue());
    assertEquals("abc", tp.getSecond());
  }

  @Test
  public void testHashing() {
    HashSet<Tuple<Integer, String>> set = new HashSet<Tuple<Integer, String>>();

    set.add(new Tuple<Integer, String>(1, "lol"));
    set.add(new Tuple<Integer, String>(2, "lol"));
    set.add(new Tuple<Integer, String>(1, "lolomg"));

    assertEquals(2, set.size());
    assertEquals(true, set.contains(new Tuple<Integer, String>(1, "okay")));

    // test null keys
    set = new HashSet<Tuple<Integer, String>>();

    set.add(new Tuple<Integer, String>(null, "lol"));
    set.add(new Tuple<Integer, String>(null, "lol"));
    assertEquals(1, set.size());
    assertEquals(true, set.contains(new Tuple<Integer, String>(null, "okay")));

  }

  @Test
  public void testEquality() {
    Tuple<Integer, String> dv = new Tuple<Integer, String>(1, "lol");
    assertEquals(false, dv.equals(null));
    assertEquals(false, dv.equals("blabla"));
    assertEquals(true, dv.equals(dv));
    assertEquals(true, dv.equals(new Tuple<Integer, String>(1, "lol")));
    assertEquals(false, dv.equals(new Tuple<Integer, String>(2, "lol")));

    // test nulls
    dv = new Tuple<Integer, String>(null, "lol");
    assertEquals(false, dv.equals(null));
    assertEquals(false, dv.equals("blabla"));
    assertEquals(true, dv.equals(dv));
    assertEquals(false, dv.equals(new Tuple<Integer, String>(1, "lol")));
    assertEquals(true, dv.equals(new Tuple<Integer, String>(null, "lol")));
  }

  @Test
  public void testComparable() {
    Tuple<Integer, String> tuple = new Tuple<Integer, String>(1, "lol");
    Tuple<Integer, String> tuple2 = new Tuple<Integer, String>(2, "lol");

    assertEquals(-1, tuple.compareTo(tuple2));
    assertEquals(0, tuple.compareTo(tuple));
    assertEquals(1, tuple2.compareTo(tuple));

    Tuple<DoubleVector, String> tx = new Tuple<DoubleVector, String>(null,
        "lol");
    Tuple<DoubleVector, String> tx2 = new Tuple<DoubleVector, String>(null,
        "lol");

    assertEquals(0, tx2.compareTo(tx));
  }

  @Test
  public void testToString() {
    Tuple<Integer, String> tuple = new Tuple<Integer, String>(1, "lol");
    assertEquals("Tuple [first=1, second=lol]", tuple.toString());
  }

}
