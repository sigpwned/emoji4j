package com.sigpwned.emojis4j.maven;

import java.util.Objects;

public class GraphemeBuilder {
  public static final String EMOJI = "Emoji";

  public static final String PICTOGRAPHIC = "Pictographic";

  private String type;
  private String shortName;

  public GraphemeBuilder() {}

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


  @Override
  public int hashCode() {
    return Objects.hash(shortName, type);
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
    return Objects.equals(shortName, other.shortName) && Objects.equals(type, other.type);
  }

  @Override
  public String toString() {
    return "GraphemeBuilder [type=" + type + ", shortName=" + shortName + "]";
  }
}
