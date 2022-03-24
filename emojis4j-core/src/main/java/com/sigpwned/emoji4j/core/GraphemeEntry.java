package com.sigpwned.emoji4j.core;

import java.util.Arrays;
import java.util.Objects;

public class GraphemeEntry {
  private final String name;
  private final String type;
  private final int[] defaultCodePointSequence;
  private final int[][] alternativeCodePointSequences;

  public GraphemeEntry(String name, String type, int[] defaultCodePointSequence,
      int[][] alternativeCodePointSequences) {
    this.name = name;
    this.type = type;
    this.defaultCodePointSequence = defaultCodePointSequence;
    this.alternativeCodePointSequences = alternativeCodePointSequences;
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
  public int[] getDefaultCodePointSequence() {
    return defaultCodePointSequence;
  }

  /**
   * @return the alternativeCodePointSequences
   */
  public int[][] getAlternativeCodePointSequences() {
    return alternativeCodePointSequences;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.deepHashCode(alternativeCodePointSequences);
    result = prime * result + Arrays.hashCode(defaultCodePointSequence);
    result = prime * result + Objects.hash(name, type);
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
    GraphemeEntry other = (GraphemeEntry) obj;
    return Arrays.deepEquals(alternativeCodePointSequences, other.alternativeCodePointSequences)
        && Arrays.equals(defaultCodePointSequence, other.defaultCodePointSequence)
        && Objects.equals(name, other.name) && Objects.equals(type, other.type);
  }

  @Override
  public String toString() {
    return "GraphemeEntry [name=" + name + ", type=" + type + ", defaultCodePointSequence="
        + Arrays.toString(defaultCodePointSequence) + ", alternativeCodePointSequences="
        + Arrays.toString(alternativeCodePointSequences) + "]";
  }
}
