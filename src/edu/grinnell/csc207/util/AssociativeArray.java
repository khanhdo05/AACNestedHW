package edu.grinnell.csc207.util;

import static java.lang.reflect.Array.newInstance;

/**
 * A basic implementation of Associative Arrays with keys of type K and values of type V.
 * Associative Arrays store key/value pairs and permit you to look up values by key.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @author Khanh Do
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V>[] pairs;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings({"unchecked"})
  public AssociativeArray() {
    // Creating new arrays is sometimes a PITN.
    this.pairs = (KVPair<K, V>[]) newInstance((new KVPair<K, V>()).getClass(), DEFAULT_CAPACITY);
    this.size = 0;
  } // AssociativeArray()

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   *
   * @return a new copy of the array
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> copy = new AssociativeArray<>();
    copy.size = this.size;

    while (copy.size >= copy.pairs.length) {
      copy.expand();
    } // while

    // Manually make a deep copy of the array.
    for (int i = 0; i < this.size; i++) {
      copy.pairs[i] = this.pairs[i].clone();
    } // for

    return copy;
  } // clone()

  /**
   * Convert the array to a string.
   *
   * @return a string of the form "{Key0:Value0, Key1:Value1, ... KeyN:ValueN}"
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");

    for (int i = 0; i < this.size; i++) {
      sb.append(this.pairs[i].toString());

      // if this is not the last element, add a comma
      if (i < this.size - 1) {
        sb.append(", ");
      } // if
    } // for

    sb.append("}");

    return sb.toString();
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to get(key) will return value.
   *
   * @param key The key whose value we are seeting.
   * @param value The value of that key.
   *
   * @throws NullKeyException If the client provides a null key.
   */
  public void set(K key, V value) throws NullKeyException {
    if (key == null) {
      throw new NullKeyException();
    } // if

    // If the key already exists, update the value.
    if (this.hasKey(key)) {
      for (int i = 0; i < this.size; i++) {
        if (this.pairs[i].key.equals(key)) {
          this.pairs[i].val = value;
        } // if
      } // for
    } else {
      // If the array is full, expand it.
      if (this.size == this.pairs.length) {
        this.expand();
      } // if
      this.pairs[this.size] = new KVPair<>(key, value);
      this.size++;
    } // if
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @param key The key whose value we are getting.
   *
   * @return The value associated with key.
   *
   * @throws KeyNotFoundException when the key is null or does not appear in the associative array.
   */
  public V get(K key) throws KeyNotFoundException {
    if (key == null) {
      throw new KeyNotFoundException("Key is null");
    } // if

    for (int i = 0; i < this.size; i++) {
      if (this.pairs[i].key.equals(key)) {
        return this.pairs[i].val;
      } // if
    } // for

    throw new KeyNotFoundException(
        "Key: " + key.toString() + " does not appear in the associative array.");
  } // get(K)

  /**
   * Determine if key appears in the associative array. Should return false for the null key, since
   * it cannot appear.
   *
   * @param key The key we're looking for.
   *
   * @return true if the key appears and false otherwise.
   */
  public boolean hasKey(K key) {
    for (int i = 0; i < this.size; i++) {
      if (this.pairs[i].key.equals(key)) {
        return true;
      } // if
    } // for

    return false;
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls to get(key) will throw an
   * exception. If the key does not appear in the associative array, does nothing.
   *
   * @param key The key to remove.
   */
  public void remove(K key) {
    // if the key does not appear in the associative array, do nothing
    if (!this.hasKey(key)) {
      return;
    } // if

    // copy the last element to this position
    for (int i = 0; i < this.size; i++) {
      if (this.pairs[i].key.equals(key)) {
        this.pairs[i] = this.pairs[this.size - 1];
        this.pairs[this.size - 1] = null;
        this.size--;
        return;
      } // if
    } // for
  } // remove(K)

  /**
   * Determine how many key/value pairs are in the associative array.
   *
   * @return the number of key/value pairs in the associative array.
   */
  public int size() {
    return this.size;
  } // size()

  /**
   * Return an array of all the keys in the associative array.
   * 
   * @return an array of all the keys in the associative array.
   */
  @SuppressWarnings("unchecked")
  public K[] keys() {
    try {
      K[] keys = (K[]) newInstance(this.pairs[0].key.getClass(), this.size);
      for (int i = 0; i < this.size; i++) {
        keys[i] = this.pairs[i].key;
      } // for
      return keys;
    } catch (Exception e) {
      return (K[]) new String[0];
    } // try/catch
  } // keys()

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Expand the underlying array.
   */
  void expand() {
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key. If no such entry is found,
   * throws an exception.
   *
   * @param key The key of the entry.
   *
   * @return The index of the key, if found.
   *
   * @throws KeyNotFoundException If the key does not appear in the associative array.
   */
  int find(K key) throws KeyNotFoundException {
    for (int i = 0; i < this.size; i++) {
      if (this.pairs[i].key.equals(key)) {
        return i;
      } // if
    } // for

    throw new KeyNotFoundException();
  } // find(K)
} // class AssociativeArray
