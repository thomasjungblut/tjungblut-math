package de.jungblut.math.tuple;

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

}
