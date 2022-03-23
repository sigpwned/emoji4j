package com.sigpwned.emoji4j.core.grapheme;

import com.sigpwned.emoji4j.core.Grapheme;

public class Pictographic extends Grapheme {
  public static Pictographic of(int[] coordinates, String name) {
    return new Pictographic(coordinates, name);
  }

  public Pictographic(int[] coordinates, String name) {
    super(Type.PICTOGRAPHIC, coordinates, name);
  }
}
