package com.sigpwned.emoji4j.core.trie;

import static java.lang.String.format;
import java.util.Arrays;
import java.util.TreeSet;
import com.sigpwned.emoji4j.core.Grapheme;
import com.sigpwned.emoji4j.core.GraphemeTrie;

public class PrimitiveArrayGraphemeTrie implements GraphemeTrie {
  public static PrimitiveArrayGraphemeTrie compile(HashMapGraphemeTrie t) {
    int size = t.size();

    int[] codePoints = new int[size];
    PrimitiveArrayGraphemeTrie[] children = new PrimitiveArrayGraphemeTrie[size];

    int index = 0;
    for (Integer child : new TreeSet<>(t.listChildren())) {
      codePoints[index] = child.intValue();
      children[index] = compile(t.getChild(child));
      index = index + 1;
    }

    return new PrimitiveArrayGraphemeTrie(codePoints, children, t.getGrapheme());
  }

  private final int[] codePoints;
  private final PrimitiveArrayGraphemeTrie[] children;
  private final Grapheme grapheme;

  public PrimitiveArrayGraphemeTrie(Grapheme grapheme) {
    this(new int[0], new PrimitiveArrayGraphemeTrie[0], grapheme);
  }

  public PrimitiveArrayGraphemeTrie(int[] codePoints, PrimitiveArrayGraphemeTrie[] children) {
    this(codePoints, children, null);
  }

  public PrimitiveArrayGraphemeTrie(int[] codePoints, PrimitiveArrayGraphemeTrie[] children,
      Grapheme grapheme) {
    if (codePoints.length != children.length)
      throw new IllegalArgumentException(
          format("codePoints length %d does not match children length %d", codePoints.length,
              children.length));
    this.codePoints = codePoints;
    this.children = children;
    this.grapheme = grapheme;
  }

  @Override
  public PrimitiveArrayGraphemeTrie getChild(int codePoint) {
    if (codePoints.length == 0)
      return null;
    if (codePoint < codePoints[0] || codePoint > codePoints[codePoints.length - 1])
      return null;
    int searchResult = Arrays.binarySearch(codePoints, codePoint);
    if (searchResult < 0 || searchResult >= codePoints.length)
      return null;
    return children[searchResult];
  }

  /**
   * @return the grapheme
   */
  @Override
  public Grapheme getGrapheme() {
    return grapheme;
  }

  @Override
  public int getDepth() {
    int maxChildDepth = 0;
    for (PrimitiveArrayGraphemeTrie child : children)
      maxChildDepth = Math.max(maxChildDepth, child.getDepth());
    return 1 + maxChildDepth;
  }
}
