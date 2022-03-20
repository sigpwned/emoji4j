package com.sigpwned.emojis4j.maven;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Objects;

public class EmojiData {
  /**
   * Is an emoji
   */
  public static final String EMOJI = "Emoji";

  /**
   * Has an emoji presentation by default
   */
  public static final String EMOJI_PRESENTATION = "Emoji_Presentation";

  /**
   * Modifier for an existing emoji
   */
  public static final String EMOJI_COMPONENT = "Emoji_Component";

  /**
   * Modifier for an existing emoji
   */
  public static final String EMOJI_MODIFIER = "Emoji_Modifier";

  /**
   * Can legally be followed by a modifier
   */
  public static final String EMOJI_MODIFIER_BASE = "Emoji_Modifier_Base";

  /**
   * Has a text presentation by default
   */
  public static final String EXTENDED_PICTOGRAPHIC = "Extended_Pictographic";

  public static class Entry {
    public static Entry of(CodePointRange codePoints, String property) {
      return new Entry(codePoints, property);
    }

    private final CodePointRange codePoints;
    private final String property;

    public Entry(CodePointRange codePoints, String property) {
      if (codePoints == null)
        throw new NullPointerException();
      if (property == null)
        throw new NullPointerException();
      this.codePoints = codePoints;
      this.property = property;
    }

    /**
     * @return the codePoints
     */
    public CodePointRange getCodePoints() {
      return codePoints;
    }

    /**
     * @return the property
     */
    public String getProperty() {
      return property;
    }

    @Override
    public int hashCode() {
      return Objects.hash(codePoints, property);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Entry other = (Entry) obj;
      return Objects.equals(codePoints, other.codePoints)
          && Objects.equals(property, other.property);
    }

    @Override
    public String toString() {
      return "Entry [codePoints=" + codePoints + ", property=" + property + "]";
    }
  }

  public static EmojiData of(List<Entry> entries) {
    return new EmojiData(entries);
  }

  private final List<Entry> entries;

  public EmojiData(List<Entry> entries) {
    this.entries = unmodifiableList(entries);
  }

  /**
   * @return the entries
   */
  public List<Entry> getEntries() {
    return entries;
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EmojiData other = (EmojiData) obj;
    return Objects.equals(entries, other.entries);
  }

  @Override
  public String toString() {
    return "EmojiData [entries=" + entries + "]";
  }
}
