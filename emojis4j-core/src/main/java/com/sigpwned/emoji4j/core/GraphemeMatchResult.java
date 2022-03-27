package com.sigpwned.emoji4j.core;

public interface GraphemeMatchResult {
  public int getStart();

  public int getEnd();

  public String getMatchedText();

  public Grapheme getGrapheme();
}
