package com.sigpwned.emoji4j.core.trie;

import static java.util.Collections.unmodifiableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.sigpwned.emoji4j.core.Grapheme;
import com.sigpwned.emoji4j.core.GraphemeData;
import com.sigpwned.emoji4j.core.GraphemeEntry;
import com.sigpwned.emoji4j.core.GraphemeTrie;
import com.sigpwned.emoji4j.core.grapheme.Emoji;
import com.sigpwned.emoji4j.core.grapheme.Pictographic;

public class HashMapGraphemeTrie implements GraphemeTrie {
  public static HashMapGraphemeTrie fromGraphemeData(GraphemeData gs) {
    HashMapGraphemeTrie result = new HashMapGraphemeTrie();
    for (GraphemeEntry g : gs.getGraphemes()) {
      Grapheme grapheme;
      switch (g.getType()) {
        case GraphemeEntry.EMOJI_TYPE:
          grapheme = new Emoji(g.getCanonicalCodePointSequence(), g.getName());
          break;
        case GraphemeEntry.PICTOGRAPHIC_TYPE:
          grapheme = new Pictographic(g.getCanonicalCodePointSequence(), g.getName());
          break;
        default:
          throw new IllegalArgumentException("unrecognized grapheme entry type " + g.getType());
      }

      result.put(g.getCanonicalCodePointSequence(), grapheme);
      for (int[] alternativeCodePointSequence : g.getAlternativeCodePointSequences())
        result.put(alternativeCodePointSequence, grapheme);
    }
    return result;
  }

  private final Map<Integer, HashMapGraphemeTrie> children;
  private Grapheme grapheme;

  public HashMapGraphemeTrie() {
    this.children = new HashMap<>();
  }

  @Override
  public HashMapGraphemeTrie getChild(int codePoint) {
    return children.get(codePoint);
  }

  private void put(int[] codePointSequence, Grapheme grapheme) {
    HashMapGraphemeTrie ti = this;
    for (int codePoint : codePointSequence)
      ti = ti.getOrCreateChild(codePoint);
    ti.setGrapheme(grapheme);
  }

  private HashMapGraphemeTrie getOrCreateChild(int codePoint) {
    return children.computeIfAbsent(codePoint, cp -> new HashMapGraphemeTrie());
  }

  public Set<Integer> listChildren() {
    return unmodifiableSet(children.keySet());
  }

  public int size() {
    return children.size();
  }

  /**
   * @return the grapheme
   */
  @Override
  public Grapheme getGrapheme() {
    return grapheme;
  }

  /**
   * @param grapheme the grapheme to set
   */
  private void setGrapheme(Grapheme grapheme) {
    this.grapheme = grapheme;
  }

  @Override
  public int getDepth() {
    int maxChildDepth = 0;
    for (HashMapGraphemeTrie child : children.values())
      maxChildDepth = Math.max(maxChildDepth, child.getDepth());
    return 1 + maxChildDepth;
  }
}
