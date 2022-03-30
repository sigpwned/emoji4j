package com.sigpwned.emoji4j.core.data;

import java.util.Arrays;
import java.util.Objects;
import com.sigpwned.emoji4j.core.annotation.Generated;

public class GraphemeEntry {
  public static final String EMOJI_TYPE = "emoji";

  public static final String PICTOGRAPHIC_TYPE = "pictographic";

  public static GraphemeEntry of(String name, String type, int[] defaultCodePointSequence,
      int[][] alternativeCodePointSequences) {
    return new GraphemeEntry(name, type, defaultCodePointSequence, alternativeCodePointSequences);
  }

  private final String name;
  private final String type;
  private final int[] canonicalCodePointSequence;
  private final int[][] alternativeCodePointSequences;

  public GraphemeEntry(String name, String type, int[] canonicalCodePointSequence,
      int[][] alternativeCodePointSequences) {
    if (name == null)
      throw new NullPointerException();
    if (type == null)
      throw new NullPointerException();
    if (!type.equals(EMOJI_TYPE) && !type.equals(PICTOGRAPHIC_TYPE))
      throw new IllegalArgumentException("unrecognized type " + type);
    if (canonicalCodePointSequence == null)
      throw new NullPointerException();
    this.name = name;
    this.type = type;
    this.canonicalCodePointSequence = canonicalCodePointSequence;
    this.alternativeCodePointSequences =
        alternativeCodePointSequences != null ? alternativeCodePointSequences : new int[0][];
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @return the defaultCodePointSequence
   */
  public int[] getCanonicalCodePointSequence() {
    return canonicalCodePointSequence;
  }

  /**
   * @return the alternativeCodePointSequences
   */
  public int[][] getAlternativeCodePointSequences() {
    return alternativeCodePointSequences;
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.deepHashCode(alternativeCodePointSequences);
    result = prime * result + Arrays.hashCode(canonicalCodePointSequence);
    result = prime * result + Objects.hash(name, type);
    return result;
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GraphemeEntry other = (GraphemeEntry) obj;
    return Arrays.deepEquals(alternativeCodePointSequences, other.alternativeCodePointSequences)
        && Arrays.equals(canonicalCodePointSequence, other.canonicalCodePointSequence)
        && Objects.equals(name, other.name) && Objects.equals(type, other.type);
  }

  @Override
  @Generated
  public String toString() {
    return "GraphemeEntry [name=" + name + ", type=" + type + ", defaultCodePointSequence="
        + Arrays.toString(canonicalCodePointSequence) + ", alternativeCodePointSequences="
        + Arrays.toString(alternativeCodePointSequences) + "]";
  }
}
