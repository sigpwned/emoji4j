package com.sigpwned.emojis4j.maven;

import java.util.Objects;

/**
 * An immutable mapping of one code point sequence to one grapheme
 */
public class GraphemeMapping {
  public static GraphemeMapping of(CodePointSequence codePoints, GraphemeBuilder grapheme) {
    return new GraphemeMapping(codePoints, grapheme);
  }

  private final CodePointSequence codePoints;
  private final GraphemeBuilder grapheme;

  public GraphemeMapping(CodePointSequence codePoints, GraphemeBuilder grapheme) {
    this.codePoints = codePoints;
    this.grapheme = grapheme;
  }

  /**
   * @return the codePoints
   */
  public CodePointSequence getCodePoints() {
    return codePoints;
  }

  /**
   * @return the grapheme
   */
  public GraphemeBuilder getGrapheme() {
    return grapheme;
  }

  @Override
  public int hashCode() {
    return Objects.hash(codePoints, grapheme);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GraphemeMapping other = (GraphemeMapping) obj;
    return Objects.equals(codePoints, other.codePoints) && Objects.equals(grapheme, other.grapheme);
  }

  @Override
  public String toString() {
    return "GraphemeMapping [codePoints=" + codePoints + ", grapheme=" + grapheme + "]";
  }
}
