package com.sigpwned.emoji4j.core;

public interface GraphemeMatchResult {
  public int start();

  public int end();

  public String group();

  public Grapheme getGrapheme();
}
