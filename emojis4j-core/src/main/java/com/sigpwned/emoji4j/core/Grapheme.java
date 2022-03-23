package com.sigpwned.emoji4j.core;

import java.util.Arrays;
import java.util.Objects;

public class Grapheme {
  public static enum Type {
    EMOJI, PICTOGRAPHIC;
  }

  private final Type type;
  private final int[] coordinates;
  private final String name;

  public Grapheme(Type type, int[] coordinates, String name) {
    this.type = type;
    this.coordinates = coordinates;
    this.name = name;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the coordinates
   */
  public int[] getCoordinates() {
    return coordinates;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(coordinates);
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
    Grapheme other = (Grapheme) obj;
    return Arrays.equals(coordinates, other.coordinates) && Objects.equals(name, other.name)
        && type == other.type;
  }

  @Override
  public String toString() {
    return new String(coordinates, 0, coordinates.length);
  }
}
