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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import com.sigpwned.emoji4j.core.util.Graphemes;

/**
 * The primary class used to process emoji.
 * 
 * @see Matcher
 */
public class GraphemeMatcher implements GraphemeMatchResult {
  private static final String NOT_MATCHED = "not matched";

  /**
   * The trie containing all possible emoji graphemes.
   */
  private final GraphemeTrie trie;

  /**
   * The text being matched
   */
  private final String text;

  /**
   * The current search index.
   */
  private int index;

  /**
   * The current match state. If {@code true}, then this matcher's internal state reflects the
   * current match. Otherwise, there is no current match.
   */
  private boolean matched;

  /**
   * The (inclusive) start index of the current match, or -1 if there is no current match.
   */
  private int start;

  /**
   * The (exclusive) end index of the current match, or -1 if there is no current match.
   */
  private int end;

  /**
   * If {@link #matched} is {@code true}, then this is the contents of the current match. Otherwise,
   * it should be {@code null}.
   */
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
  }

  public boolean find() {
    matched = false;
    start = end = -1;
    grapheme = null;

    while (index < text.length()) {
      // Is there a grapheme starting at index?
      int cp0 = text().codePointAt(index);
      int cc0 = Character.charCount(cp0);

      GraphemeTrie t = trie().getChild(cp0);
      if (t != null) {
        // There is the beginnings of a match at index! Graphemes may be multiple code points long,
        // and this code point could start a grapheme. Let's see if we find a full grapheme here.
        int index0 = index;
        int offset = cc0;

        if (t.getGrapheme() != null) {
          // We have a grapheme! We always want to take the longest grapheme possible -- e.g.,
          // including skin tones -- so keep searching.
          matched = true;
          start = index;
          end = index + offset;
          grapheme = t.getGrapheme();
        }

        while (index + offset < text.length()) {
          int cpi = text().codePointAt(index + offset);

          t = t.getChild(cpi);
          if (t == null) {
            // The next code point is not the next step in a possibly longer grapheme.
            break;
          }

          int cci = Character.charCount(cpi);

          offset = offset + cci;

          if (t.getGrapheme() != null) {
            // We found a longer grapheme! Store it instead.
            matched = true;
            start = index;
            end = index + offset;
            grapheme = t.getGrapheme();
          }
        }

        if (matched()) {
          // If we did find a match, then resume our search right after the match.
          index = end;
          return true;
        } else {
          // If we did not match, then we need to resume our search at the following code point, not
          // wherever index happens to point right now. We are not Boyer-Moore smart, at least yet.
          // https://en.wikipedia.org/wiki/Boyer%E2%80%93Moore_string-search_algorithm
          index = index0;
        }
      }

      index = index + cc0;
    }

    return false;
  }

  public boolean matches() {
    if (!find())
      return false;
    if (start() == 0 && end() == text.length())
      return true;
    matched = false;
    start = end = -1;
    grapheme = null;
    return false;
  }

  /**
   * Return the current text with the first grapheme replaced with the given string. Note that this
   * matcher is {@link #reset()} first.
   */
  public String replaceFirst(String replacement) {
    return replaceFirst(mr -> replacement);
  }

  /**
   * Return the current text with the first grapheme replaced with the computed string. Note that
   * this matcher is {@link #reset()} first.
   */
  public String replaceFirst(Function<GraphemeMatchResult, String> replacer) {
    return replaceSome(replacer, true);
  }

  /**
   * Return the current text with all graphemes replaced with the given string. Note that this
   * matcher is {@link #reset()} first.
   */
  public String replaceAll(String replacement) {
    return replaceAll(mr -> replacement);
  }

  /**
   * Return the current text with all graphemes replaced with their respsective computed strings.
   * Note that this matcher is {@link #reset()} first.
   */
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

    result.append(text().substring(position, text.length()));

    return result.toString();

  }

  /**
   * Begins the matching process over at the start of the current text
   */
  public void reset() {
    index = 0;

    matched = false;
    start = end = -1;
    grapheme = null;
  }

  /**
   * @return the (inclusive) start index of the current match
   * @throws IllegalStateException if there is no current match
   */
  @Override
  public int start() {
    if (!matched())
      throw new IllegalStateException(NOT_MATCHED);
    return start;
  }

  /**
   * @return the (exclusive) end index of the current match
   * @throws IllegalStateException if there is no current match
   */
  @Override
  public int end() {
    if (!matched())
      throw new IllegalStateException(NOT_MATCHED);
    return end;
  }

  /**
   * @return the text of the current match
   * @throws IllegalStateException if there is no current match
   */
  @Override
  public String group() {
    return text().substring(start(), end());
  }

  /**
   * @return the {@link Grapheme} of the current match
   * @throws IllegalStateException if there is no current match
   */
  @Override
  public Grapheme grapheme() {
    if (!matched())
      throw new IllegalStateException(NOT_MATCHED);
    return grapheme;
  }

  /**
   * @return A {@link Stream} of the remaining grapheme matches. The matcher is NOT {@link #reset()}
   *         first.
   */
  public Stream<GraphemeMatchResult> results() {
    class GraphemeMatchResultIterator implements Iterator<GraphemeMatchResult> {
      // -ve for call to find, 0 for not found, 1 for found
      private Boolean hasNext;

      @Override
      public GraphemeMatchResult next() {
        if (!hasNext())
          throw new NoSuchElementException();
        hasNext = null;
        return toMatchResult();
      }

      @Override
      public boolean hasNext() {
        if (hasNext != null)
          return hasNext.booleanValue();
        hasNext = find();
        return hasNext;
      }

      @Override
      public void forEachRemaining(Consumer<? super GraphemeMatchResult> action) {
        Boolean hn = hasNext;
        if (hn != null && !hn.booleanValue())
          return;

        // Set state to report no more elements on further operations
        hasNext = Boolean.FALSE;

        // Perform a first find if required
        if (hn == null && !find())
          return;

        do {
          action.accept(toMatchResult());
        } while (find());
      }
    }
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
        new GraphemeMatchResultIterator(), Spliterator.ORDERED | Spliterator.NONNULL), false);
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
