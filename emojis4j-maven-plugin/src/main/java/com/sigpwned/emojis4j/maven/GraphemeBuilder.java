package com.sigpwned.emojis4j.maven;

import java.util.Set;
import java.util.TreeSet;

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
    alternativeCodePointSequences.add(alternativeCodePointSequence);
  }

  public GraphemeBuilder withAlternativeCodePointSequence(
      CodePointSequence alternativeCodePointSequence) {
    addAlternativeCodePointSequence(alternativeCodePointSequence);
    return this;
  }
}
