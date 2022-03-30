package com.sigpwned.emoji4j.core.trie;

import com.sigpwned.emoji4j.core.Grapheme;
import com.sigpwned.emoji4j.core.GraphemeData;
import com.sigpwned.emoji4j.core.GraphemeTrie;
import com.sigpwned.emoji4j.core.data.GraphemeEntry;
import com.sigpwned.emoji4j.core.grapheme.Emoji;
import com.sigpwned.emoji4j.core.grapheme.Pictographic;
import com.sigpwned.emoji4j.core.org.apache.commons.lang.IntHashMap;

public class DefaultGraphemeTrie implements GraphemeTrie {
  public static DefaultGraphemeTrie fromGraphemeData(GraphemeData gs) {
    DefaultGraphemeTrie result = new DefaultGraphemeTrie();
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

  private final IntHashMap<DefaultGraphemeTrie> children;
  private Grapheme grapheme;

  public DefaultGraphemeTrie() {
    this.children = new IntHashMap<>();
  }

  @Override
  public DefaultGraphemeTrie getChild(int codePoint) {
    return children.get(codePoint);
  }

  private void put(int[] codePointSequence, Grapheme grapheme) {
    DefaultGraphemeTrie ti = this;
    for (int codePoint : codePointSequence)
      ti = ti.getOrCreateChild(codePoint);
    ti.setGrapheme(grapheme);
  }

  private DefaultGraphemeTrie getOrCreateChild(int codePoint) {
    DefaultGraphemeTrie result = children.get(codePoint);
    if (result == null)
      children.put(codePoint, result = new DefaultGraphemeTrie());
    return result;
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
}
