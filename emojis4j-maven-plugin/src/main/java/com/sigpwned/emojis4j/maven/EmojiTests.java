package com.sigpwned.emojis4j.maven;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Objects;

public class EmojiTests {
  public static final String COMPONENT = "component";

  public static final String FULLY_QUALIFIED = "fully-qualified";

  public static final String MINIMALLY_QUALIFIED = "minimally-qualified";

  public static final String UNQUALIFIED = "unqualified";

  public static class Entry {
    public static Entry of(CodePointSequence codePointSequence, String variation) {
      return new Entry(codePointSequence, variation);
    }

    private final CodePointSequence codePointSequence;
    private final String status;

    public Entry(CodePointSequence codePointSequence, String status) {
      this.codePointSequence = codePointSequence;
      this.status = status;
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
    public String getStatus() {
      return status;
    }

    @Override
    public int hashCode() {
      return Objects.hash(codePointSequence, status);
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
          && Objects.equals(status, other.status);
    }

    @Override
    public String toString() {
      return "Entry [codePointSequence=" + codePointSequence + ", status=" + status + "]";
    }
  }

  public static EmojiTests of(List<Entry> entries) {
    return new EmojiTests(entries);
  }

  private final List<Entry> entries;

  public EmojiTests(List<Entry> entries) {
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
    EmojiTests other = (EmojiTests) obj;
    return Objects.equals(entries, other.entries);
  }

  @Override
  public String toString() {
    return "EmojiTests [entries=" + entries + "]";
  }
}
