package com.sigpwned.emoji4j.core;

import java.util.Arrays;

public class GraphemeTrie {
  private final int[] codePoints;
  private final GraphemeTrie[] children;
  private final Grapheme grapheme;

  public GraphemeTrie(Grapheme grapheme) {
    this(new int[0], new GraphemeTrie[0], grapheme);
  }

  public GraphemeTrie(int[] codePoints, GraphemeTrie[] children) {
    this(codePoints, children, null);
  }

  public GraphemeTrie(int[] codePoints, GraphemeTrie[] children, Grapheme grapheme) {
    this.codePoints = codePoints;
    this.children = children;
    this.grapheme = grapheme;
  }

  public GraphemeTrie getChild(int codePoint) {
    int searchResult = Arrays.binarySearch(codePoints, codePoint);
    if (searchResult < 0 || searchResult >= codePoints.length)
      return null;
    return children[searchResult];
  }

  /**
   * @return the grapheme
   */
  public Grapheme getGrapheme() {
    return grapheme;
  }
}
