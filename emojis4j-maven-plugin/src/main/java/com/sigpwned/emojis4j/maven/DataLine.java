package com.sigpwned.emojis4j.maven;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A single line read from a file in the Unicode standard
 */
public class DataLine {
  public static final DataLine BLANK = new DataLine(emptyList());

  private static final Pattern SEMICOLON = Pattern.compile(";");

  public static DataLine fromString(String s) {
    s = s.trim();

    if (s.isEmpty())
      return BLANK;

    int index = s.indexOf('#');
    if (index == 0)
      return BLANK;
    if (index != -1)
      s = s.substring(0, index).trim();

    return new DataLine(SEMICOLON.splitAsStream(s).map(String::trim).collect(toList()));
  }

  private final List<String> fields;

  public DataLine(List<String> fields) {
    this.fields = unmodifiableList(fields);
  }

  public boolean isBlank() {
    return getFields().isEmpty();
  }

  public String getField(int index) {
    return getFields().get(index);
  }

  public int size() {
    return getFields().size();
  }

  /**
   * @return the fields
   */
  private List<String> getFields() {
    return fields;
  }
}
