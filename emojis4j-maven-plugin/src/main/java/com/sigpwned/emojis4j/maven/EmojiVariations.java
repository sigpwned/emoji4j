package com.sigpwned.emojis4j.maven;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Objects;

public class EmojiVariations {
  public static final String TEXT_STYLE = "text style";

  public static final String EMOJI_STYLE = "emoji style";

  public static class Entry {
    public static Entry of(CodePointSequence codePointSequence, String variation) {
      return new Entry(codePointSequence, variation);
    }

    private final CodePointSequence codePointSequence;
    private final String variation;

    public Entry(CodePointSequence codePointSequence, String variation) {
      this.codePointSequence = codePointSequence;
      this.variation = variation;
    }

    /**
     * @return the codePointSequence
     */
    public CodePointSequence getCodePointSequence() {
      return codePointSequence;
    }

    /**
     * @return the variation
     */
    public String getVariation() {
      return variation;
    }

    @Override
    public int hashCode() {
      return Objects.hash(codePointSequence, variation);
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
      return Objects.equals(codePointSequence, other.codePointSequence)
          && Objects.equals(variation, other.variation);
    }

    @Override
    public String toString() {
      return "Entry [codePointSequence=" + codePointSequence + ", variation=" + variation + "]";
    }
  }

  public static EmojiVariations of(List<Entry> entries) {
    return new EmojiVariations(entries);
  }

  private final List<Entry> entries;

  public EmojiVariations(List<Entry> entries) {
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
    EmojiVariations other = (EmojiVariations) obj;
    return Objects.equals(entries, other.entries);
  }

  @Override
  public String toString() {
    return "EmojiVariations [entries=" + entries + "]";
  }
}
