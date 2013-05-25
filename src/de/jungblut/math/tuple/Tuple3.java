package de.jungblut.math.tuple;

/**
 * Tuple class to hold three generic attributes. This class implements hashcode,
 * equals and comparable via the first element.
 */
public final class Tuple3<FIRST, SECOND, THIRD> implements
    Comparable<Tuple3<FIRST, SECOND, THIRD>> {

  private final FIRST first;
  private final SECOND second;
  private final THIRD third;

  public Tuple3(FIRST first, SECOND second, THIRD third) {
    super();
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public FIRST getFirst() {
    return first;
  }

  public SECOND getSecond() {
    return second;
  }

  public THIRD getThird() {
    return third;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.first == null) ? 0 : this.first.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    Tuple3 other = (Tuple3) obj;
    if (this.first == null) {
      if (other.first != null)
        return false;
    } else if (!this.first.equals(other.first))
      return false;
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(Tuple3<FIRST, SECOND, THIRD> o) {
    if (o.getFirst() instanceof Comparable && getFirst() instanceof Comparable) {
      return ((Comparable<FIRST>) getFirst()).compareTo(o.getFirst());
    } else {
      return 0;
    }
  }

  @Override
  public String toString() {
    return "Tuple3 [first=" + first + ", second=" + second + ", third=" + third
        + "]";
  }

}
