package de.jungblut.math;

import junit.framework.TestCase;

import org.junit.Test;

import de.jungblut.math.DoubleVector.DoubleVectorElement;

public class DoubleVectorElementTest extends TestCase {

  @Test
  public void testDoubleVectorElement() {
    DoubleVectorElement element = new DoubleVectorElement();

    element.setIndex(1);
    element.setValue(24d);

    assertEquals(1, element.getIndex());
    assertEquals(24d, element.getValue());

    assertEquals("1 -> 24.0", element.toString());

  }

}
