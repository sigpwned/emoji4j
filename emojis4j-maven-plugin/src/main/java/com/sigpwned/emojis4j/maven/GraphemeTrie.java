package com.sigpwned.emojis4j.maven;

import static java.util.Collections.unmodifiableSortedSet;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;

public class GraphemeTrie {
  private final NavigableMap<CodePoint, GraphemeTrie> children;
  private final CodePointSequence codePoints;
  private GraphemeBuilder grapheme;

  /**
   * Root only
   */
  public GraphemeTrie() {
    this(null);
  }

  public GraphemeTrie(CodePointSequence codePoints) {
    this.children = new TreeMap<>();
    this.codePoints = codePoints;
  }

  public void put(CodePointSequence cps, GraphemeBuilder grapheme) {
    GraphemeTrie node = this;
    for (CodePoint cp : cps) {
      GraphemeTrie child;
      if (node.children.containsKey(cp))
        child = node.children.get(cp);
      else {
        node.children.put(cp,
            child = new GraphemeTrie(node.getCodePoints() != null ? node.getCodePoints().plus(cp)
                : CodePointSequence.of(cp)));
      }

      node = child;
    }
    node.setGrapheme(grapheme);
  }

  public SortedSet<CodePoint> listChildren() {
    return unmodifiableSortedSet(children.navigableKeySet());
  }

  public GraphemeTrie getChild(CodePoint key) {
    GraphemeTrie result = children.get(key);
    if (result == null)
      throw new IllegalArgumentException("no child " + key);
    return result;
  }

  /**
   * @return the grapheme
   */
  public GraphemeBuilder getGrapheme() {
    return grapheme;
  }

  /**
   * @return the codePoints
   */
  public CodePointSequence getCodePoints() {
    return codePoints;
  }

  /**
   * @param grapheme the grapheme to set
   */
  public void setGrapheme(GraphemeBuilder grapheme) {
    this.grapheme = grapheme;
  }

  public GraphemeTrie withGrapheme(GraphemeBuilder grapheme) {
    setGrapheme(grapheme);
    return this;
  }
}
