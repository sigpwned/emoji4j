package com.sigpwned.emojis4j.maven;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import com.google.common.collect.Sets;

public class GraphemeBuilder {
  public static final String EMOJI = "emoji";

  public static final String PICTOGRAPHIC = "pictographic";

  private String type;
  private String shortName;
  private CodePointSequence canonicalCodePointSequence;
  private Set<CodePointSequence> alternativeCodePointSequences;

  public GraphemeBuilder() {
    this.alternativeCodePointSequences = new TreeSet<>();
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  public GraphemeBuilder withType(String type) {
    setType(type);
    return this;
  }

  /**
   * @return the shortName
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * @param shortName the shortName to set
   */
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public GraphemeBuilder withShortName(String shortName) {
    setShortName(shortName);
    return this;
  }

  /**
   * @return the canonicalCodePointSequence
   */
  public CodePointSequence getCanonicalCodePointSequence() {
    return canonicalCodePointSequence;
  }

  /**
   * @param canonicalCodePointSequence the canonicalCodePointSequence to set
   */
  public void setCanonicalCodePointSequence(CodePointSequence canonicalCodePointSequence) {
    if (canonicalCodePointSequence == null && getCanonicalCodePointSequence() != null)
      throw new NullPointerException();
    if (canonicalCodePointSequence != null && getCanonicalCodePointSequence() != null
        && !canonicalCodePointSequence.equals(getCanonicalCodePointSequence()))
      throw new IllegalArgumentException(
          format("cannot reset canonical code point sequence from %s to %s",
              getCanonicalCodePointSequence(), canonicalCodePointSequence));
    if (canonicalCodePointSequence != null
        && getAlternativeCodePointSequences().contains(canonicalCodePointSequence))
      throw new IllegalArgumentException(
          format("cannot add canonical code point sequence %s already in alternatives %s",
              canonicalCodePointSequence, getAlternativeCodePointSequences()));
    this.canonicalCodePointSequence = canonicalCodePointSequence;
  }

  public GraphemeBuilder withCanonicalCodePointSequence(
      CodePointSequence canonicalCodePointSequence) {
    setCanonicalCodePointSequence(canonicalCodePointSequence);
    return this;
  }

  /**
   * @return the alternativeCodePointSequences
   */
  public Set<CodePointSequence> getAlternativeCodePointSequences() {
    return alternativeCodePointSequences;
  }

  public void addAlternativeCodePointSequence(CodePointSequence alternativeCodePointSequence) {
    if (getCanonicalCodePointSequence() != null
        && alternativeCodePointSequences.contains(getCanonicalCodePointSequence()))
      throw new IllegalStateException(
          "New alternative matches current canonical code point sequence "
              + alternativeCodePointSequence);
    alternativeCodePointSequences.add(alternativeCodePointSequence);
  }

  public void addAlternativeCodePointSequences(
      Collection<CodePointSequence> alternativeCodePointSequences) {
    for (CodePointSequence alternativeCodePointSequence : alternativeCodePointSequences)
      addAlternativeCodePointSequence(alternativeCodePointSequence);
  }

  public GraphemeBuilder withAlternativeCodePointSequence(
      CodePointSequence alternativeCodePointSequence) {
    addAlternativeCodePointSequence(alternativeCodePointSequence);
    return this;
  }

  public GraphemeBuilder withAlternativeCodePointSequences(
      Collection<CodePointSequence> alternativeCodePointSequences) {
    addAlternativeCodePointSequences(alternativeCodePointSequences);
    return this;
  }

  public Set<CodePointSequence> getAllCodePointSequences() {
    Set<CodePointSequence> result = new LinkedHashSet<>();
    if (getCanonicalCodePointSequence() != null)
      result.add(getCanonicalCodePointSequence());
    result.addAll(getAlternativeCodePointSequences());
    return unmodifiableSet(result);
  }

  public GraphemeBuilder merge(GraphemeBuilder other) {
    if (other == null)
      throw new NullPointerException();
    if (!Objects.equals(getType(), other.getType()))
      throw new IllegalArgumentException(
          format("Cannot merge type %s to type %s", getType(), other.getType()));
    if (!Objects.equals(getShortName(), other.getShortName()))
      throw new IllegalArgumentException(format("Cannot merge shortName %s to shortName %s",
          getShortName(), other.getShortName()));
    if (!Objects.equals(getCanonicalCodePointSequence(), other.getCanonicalCodePointSequence()))
      throw new IllegalArgumentException(
          format("Cannot merge canonicalCodePointSequence %s to canonicalCodePointSequence %s",
              getCanonicalCodePointSequence(), other.getCanonicalCodePointSequence()));
    if (equals(other))
      return this;
    return new GraphemeBuilder().withType(getType()).withShortName(getShortName())
        .withCanonicalCodePointSequence(getCanonicalCodePointSequence())
        .withAlternativeCodePointSequences(Sets.union(getAlternativeCodePointSequences(),
            other.getAlternativeCodePointSequences()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(alternativeCodePointSequences, canonicalCodePointSequence, shortName, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GraphemeBuilder other = (GraphemeBuilder) obj;
    return Objects.equals(alternativeCodePointSequences, other.alternativeCodePointSequences)
        && Objects.equals(canonicalCodePointSequence, other.canonicalCodePointSequence)
        && Objects.equals(shortName, other.shortName) && Objects.equals(type, other.type);
  }

  @Override
  public String toString() {
    return "GraphemeBuilder [type=" + type + ", shortName=" + shortName
        + ", canonicalCodePointSequence=" + canonicalCodePointSequence
        + ", alternativeCodePointSequences=" + alternativeCodePointSequences + "]";
  }
}
