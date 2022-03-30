/*-
 * =================================LICENSE_START==================================
 * emoji4j-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.emoji4j.core;

import java.util.function.Function;
import java.util.regex.Matcher;
import com.sigpwned.emoji4j.core.util.Graphemes;

/**
 * The primary class used to process emoji.
 * 
 * @see Matcher
 */
public class GraphemeMatcher implements GraphemeMatchResult {
  private static final String NOT_MATCHED = "not matched";
  
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
      int cp0 = text().codePointAt(index);
      int cc0 = Character.charCount(cp0);

      GraphemeTrie t = trie().getChild(cp0);
      if (t != null) {
        int offset = cc0;

        if (t.getGrapheme() != null) {
          matched = true;
          start = index;
          end = index + offset;
          grapheme = t.getGrapheme();
        }

        while (index + offset < length) {
          int cpi = text().codePointAt(index + offset);

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

        if (matched()) {
          index = end;
          return true;
        }
      }

      index = index + cc0;
    }

    return false;
  }

  public boolean matches() {
    if (!find())
      return false;
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

    int position = 0;
    while (find()) {
      result.append(text().substring(position, start()));
      result.append(replacer.apply(this));
      position = end();
      if (firstOnly)
        break;
    }

    result.append(text().substring(position, length));

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
    if (!matched())
      throw new IllegalStateException(NOT_MATCHED);
    return start;
  }

  @Override
  public int end() {
    if (!matched())
      throw new IllegalStateException(NOT_MATCHED);
    return end;
  }

  @Override
  public String group() {
    return text().substring(start(), end());
  }

  @Override
  public Grapheme grapheme() {
    if (!matched())
      throw new IllegalStateException(NOT_MATCHED);
    return grapheme;
  }

  public GraphemeMatchResult toMatchResult() {
    if (!matched())
      throw new IllegalStateException(NOT_MATCHED);
    final int thestart = start();
    final int theend = end();
    final String thegroup = group();
    final Grapheme thegrapheme = grapheme();
    return new GraphemeMatchResult() {
      @Override
      public int start() {
        return thestart;
      }

      @Override
      public int end() {
        return theend;
      }

      @Override
      public String group() {
        return thegroup;
      }

      @Override
      public Grapheme grapheme() {
        return thegrapheme;
      }
    };
  }

  private boolean matched() {
    return matched;
  }

  /**
   * @return the trie
   */
  private GraphemeTrie trie() {
    return trie;
  }

  /**
   * @return the text
   */
  private String text() {
    return text;
  }
}
