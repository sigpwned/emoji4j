package com.sigpwned.emoji4j.core;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Objects;
import com.sigpwned.emoji4j.core.annotation.Generated;
import com.sigpwned.emoji4j.core.data.GraphemeEntry;

public class GraphemeData {
  public static GraphemeData of(String unicodeVersion, List<GraphemeEntry> graphemes) {
    return new GraphemeData(unicodeVersion, graphemes);
  }

  private final String unicodeVersion;
  private final List<GraphemeEntry> graphemes;

  public GraphemeData(String unicodeVersion, List<GraphemeEntry> graphemes) {
    this.unicodeVersion = unicodeVersion;
    this.graphemes = unmodifiableList(graphemes);
  }

  /**
   * @return the unicodeVersion
   */
  public String getUnicodeVersion() {
    return unicodeVersion;
  }

  /**
   * @return the graphemes
   */
  public List<GraphemeEntry> getGraphemes() {
    return graphemes;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(graphemes, unicodeVersion);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GraphemeData other = (GraphemeData) obj;
    return Objects.equals(graphemes, other.graphemes)
        && Objects.equals(unicodeVersion, other.unicodeVersion);
  }

  @Override
  @Generated
  public String toString() {
    return "GraphemeData [unicodeVersion=" + unicodeVersion + ", graphemes=" + graphemes + "]";
  }
}
