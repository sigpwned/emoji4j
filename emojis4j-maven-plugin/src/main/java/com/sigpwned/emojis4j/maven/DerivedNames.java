package com.sigpwned.emojis4j.maven;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Objects;

public class DerivedNames {
  public static class Entry {
    public static Entry of(CodePointRange codePoints, String name) {
      return new Entry(codePoints, name);
    }

    private final CodePointRange codePoints;
    private final String name;

    public Entry(CodePointRange codePoints, String name) {
      if (codePoints == null)
        throw new NullPointerException();
      if (name == null)
        throw new NullPointerException();
      this.codePoints = codePoints;
      this.name = name;
    }

    /**
     * @return the codePoints
     */
    public CodePointRange getCodePoints() {
      return codePoints;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }

    @Override
    public int hashCode() {
      return Objects.hash(codePoints, name);
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
      return Objects.equals(codePoints, other.codePoints) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
      return "Entry [codePoints=" + codePoints + ", name=" + name + "]";
    }
  }

  public static DerivedNames of(List<Entry> entries) {
    return new DerivedNames(entries);
  }

  private final List<Entry> entries;

  public DerivedNames(List<Entry> entries) {
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
    DerivedNames other = (DerivedNames) obj;
    return Objects.equals(entries, other.entries);
  }

  @Override
  public String toString() {
    return "DerivedNames [entries=" + entries + "]";
  }
}
