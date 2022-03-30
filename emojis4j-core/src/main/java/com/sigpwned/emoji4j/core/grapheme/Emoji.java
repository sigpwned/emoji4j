package com.sigpwned.emoji4j.core.grapheme;

import com.sigpwned.emoji4j.core.Grapheme;

/**
 * A formal emoji grapheme
 */
public class Emoji extends Grapheme {
  public static Emoji of(int[] coordinates, String name) {
    return new Emoji(coordinates, name);
  }

  public Emoji(int[] coordinates, String name) {
    super(Type.EMOJI, coordinates, name);
  }
}
