package de.jungblut.math.tuple;

import java.util.HashSet;

import junit.framework.TestCase;

import org.junit.Test;

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

  }

  @Test
  public void testComparable() {
    Tuple<Integer, String> tuple = new Tuple<Integer, String>(1, "lol");
    Tuple<Integer, String> tuple2 = new Tuple<Integer, String>(2, "lol");

    assertEquals(-1, tuple.compareTo(tuple2));
    assertEquals(0, tuple.compareTo(tuple));
    assertEquals(1, tuple2.compareTo(tuple));

  }

  @Test
  public void testToString() {
    Tuple<Integer, String> tuple = new Tuple<Integer, String>(1, "lol");
    assertEquals("Tuple [first=1, second=lol]", tuple.toString());
  }

}
