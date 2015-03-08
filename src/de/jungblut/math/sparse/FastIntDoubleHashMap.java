package de.jungblut.math.sparse;

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;

public class FastIntDoubleHashMap extends TIntDoubleHashMap {

  public FastIntDoubleHashMap() {
    super();
  }

  public FastIntDoubleHashMap(int initialCapacity, float loadFactor,
      int noEntryKey, double noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }

  public FastIntDoubleHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  public FastIntDoubleHashMap(int initialCapacity) {
    super(initialCapacity);
  }

  public FastIntDoubleHashMap(int[] keys, double[] values) {
    super(keys, values);
  }

  public FastIntDoubleHashMap(TIntDoubleMap map) {
    super(map);
  }

  public FastIntDoubleHashMap fastDeepCopy() {
    // copies all of the internal array states using System.arrayCopy
    // TODO this still allocates some garbage on the heap...
    FastIntDoubleHashMap copy = new FastIntDoubleHashMap();
    copy.setUp(this._set.length);

    System.arraycopy(this._set, 0, copy._set, 0, this._set.length);
    System.arraycopy(this._values, 0, copy._values, 0, this._values.length);
    System.arraycopy(this._states, 0, copy._states, 0, this._states.length);

    copy._size = this._size;
    copy._maxSize = this._maxSize;
    copy._free = this._free;
    copy._autoCompactRemovesRemaining = this._autoCompactRemovesRemaining;

    return copy;
  }
}
