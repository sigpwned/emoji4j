package com.sigpwned.emoji4j.core;

public interface GraphemeTrie {
  GraphemeTrie getChild(int codePoint);

  int getDepth();

  /**
   * @return the grapheme
   */
  Grapheme getGrapheme();
}
