package com.sigpwned.emoji4j.core.grapheme;

import java.util.Arrays;
import com.sigpwned.emoji4j.core.Grapheme;

public class Pictographic extends Grapheme {
  public Pictographic(int[] coordinates, String name) {
    super(Type.PICTOGRAPHIC, coordinates, name);
  }

  @Override
  public String toString() {
    return "Pictographic [getCoordinates()=" + Arrays.toString(getCoordinates()) + ", getName()="
        + getName() + "]";
  }
}
