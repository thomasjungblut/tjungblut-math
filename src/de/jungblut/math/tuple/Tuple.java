package de.jungblut.math.tuple;

/**
 * Tuple class to hold two generic attributes. This class implements hashcode,
 * equals and comparable via the first element.
 */
public final class Tuple<FIRST, SECOND> implements
    Comparable<Tuple<FIRST, SECOND>> {

  private final FIRST first;
  private final SECOND second;

  public Tuple(FIRST first, SECOND second) {
    super();
    this.first = first;
    this.second = second;
  }

  public FIRST getFirst() {
    return first;
  }

  public SECOND getSecond() {
    return second;
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
    Tuple other = (Tuple) obj;
    if (this.first == null) {
      if (other.first != null)
        return false;
    } else if (!this.first.equals(other.first))
      return false;
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(Tuple<FIRST, SECOND> o) {
    if (o.getFirst() instanceof Comparable && getFirst() instanceof Comparable) {
      return ((Comparable<FIRST>) getFirst()).compareTo(o.getFirst());
    } else {
      return 0;
    }
  }

  @Override
  public String toString() {
    return "Tuple [first=" + first + ", second=" + second + "]";
  }

}
