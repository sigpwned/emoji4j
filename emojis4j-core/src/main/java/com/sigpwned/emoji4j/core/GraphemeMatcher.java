package com.sigpwned.emoji4j.core;

import java.util.function.Function;
import com.sigpwned.emoji4j.core.util.Graphemes;

public class GraphemeMatcher implements GraphemeMatchResult {
  private final GraphemeTrie trie;
  private final String text;
  private int length;
  private int index;
  private boolean matched;
  private int start;
  private int end;
  private Grapheme grapheme;

  public GraphemeMatcher(String text) {
    this(Graphemes.getDefaultTrie(), text);
  }

  public GraphemeMatcher(GraphemeTrie trie, String text) {
    if (trie == null)
      throw new NullPointerException();
    if (text == null)
      throw new NullPointerException();
    this.trie = trie;
    this.text = text;
    this.length = text.length();
  }

  public boolean find() {
    matched = false;
    start = end = -1;
    grapheme = null;

    while (index < length) {
      // Is there a grapheme starting at offset?
      int cp0 = getText().codePointAt(index);
      int cc0 = Character.charCount(cp0);

      GraphemeTrie t = getTrie().getChild(cp0);
      if (t != null) {
        int offset = cc0;

        if (t.getGrapheme() != null) {
          matched = true;
          start = index;
          end = index + offset;
          grapheme = t.getGrapheme();
        }

        while (index + offset < length) {
          int cpi = getText().codePointAt(index + offset);

          t = t.getChild(cpi);
          if (t == null)
            break;

          int cci = Character.charCount(cpi);

          offset = offset + cci;

          if (t.getGrapheme() != null) {
            matched = true;
            start = index;
            end = index + offset;
            grapheme = t.getGrapheme();
          }
        }

        if (isMatched()) {
          index = index + offset;
          return true;
        }
      }

      index = index + cc0;
    }

    return false;
  }

  public boolean matches() {
    if (find())
      if (start() == 0 && end() == length)
        return true;
    matched = false;
    start = end = -1;
    grapheme = null;
    return false;
  }

  public String replaceFirst(String replacement) {
    return replaceFirst(mr -> replacement);
  }

  public String replaceFirst(Function<GraphemeMatchResult, String> replacer) {
    return replaceSome(replacer, true);
  }

  public String replaceAll(String replacement) {
    return replaceAll(mr -> replacement);
  }

  public String replaceAll(Function<GraphemeMatchResult, String> replacer) {
    return replaceSome(replacer, false);
  }

  private String replaceSome(Function<GraphemeMatchResult, String> replacer, boolean firstOnly) {
    reset();

    StringBuilder result = new StringBuilder();

    int start = 0;
    while (find()) {
      result.append(getText().substring(start, start()));
      result.append(replacer.apply(this));
      start = end();
      if (firstOnly)
        break;
    }

    result.append(getText().substring(start, length));

    return result.toString();

  }

  public void reset() {
    index = 0;

    matched = false;
    start = end = -1;
    grapheme = null;
  }

  @Override
  public int start() {
    if (!isMatched())
      throw new IllegalStateException("not matched");
    return start;
  }

  @Override
  public int end() {
    if (!isMatched())
      throw new IllegalStateException("not matched");
    return end;
  }

  @Override
  public String group() {
    return getText().substring(start(), end());
  }

  @Override
  public Grapheme getGrapheme() {
    if (!isMatched())
      throw new IllegalStateException("not matched");
    return grapheme;
  }

  private boolean isMatched() {
    return matched;
  }

  /**
   * @return the trie
   */
  private GraphemeTrie getTrie() {
    return trie;
  }

  /**
   * @return the text
   */
  private String getText() {
    return text;
  }
}
