package com.sigpwned.emoji4j.core;

public interface GraphemeTrie {
  GraphemeTrie getChild(int codePoint);

  /**
   * @return the grapheme
   */
  Grapheme getGrapheme();
}
