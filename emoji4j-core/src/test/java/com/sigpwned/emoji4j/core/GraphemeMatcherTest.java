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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.sigpwned.emoji4j.core.data.GraphemeEntry;
import com.sigpwned.emoji4j.core.util.Graphemes;

public abstract class GraphemeMatcherTest {
  public abstract GraphemeMatcher newGraphemeMatcher(String input);

  /**
   * Run a simple test with one canned emoji
   */
  @Test
  public void replaceAllTest() {
    GraphemeMatcher m = newGraphemeMatcher("hello 🙂 world");

    assertThat(m.find(), is(true));
    assertThat(m.start(), is(6));
    assertThat(m.grapheme().getName(), is("slightly smiling face"));

    assertThat(m.replaceAll(r -> "EMOJI"), is("hello EMOJI world"));
  }
  
  /**
   * Run a simple test with one canned emoji
   */
  @Test
  public void replaceOneTest() {
    GraphemeMatcher m = newGraphemeMatcher("alpha 🙂 bravo 🙂 charlie");

    assertThat(m.replaceFirst(r -> "EMOJI"), is("alpha EMOJI bravo 🙂 charlie"));
  }

  /**
   * Make sure we find the emoji
   */
  @Test
  public void findTest() {
    GraphemeMatcher m = newGraphemeMatcher("hello 🙂 world");

    assertThat(m.find(), is(true));
    assertThat(m.start(), is(6));
    assertThat(m.grapheme().getName(), is("slightly smiling face"));
  }

  /**
   * We should not match if the whole string is not an emoji
   */
  @Test
  public void matchesNegativeTest() {
    GraphemeMatcher m = newGraphemeMatcher("hello 🙂 world");

    assertThat(m.matches(), is(false));
  }

  /**
   * We should match if the whole string is not an emoji
   */
  @Test
  public void matchesPositiveTest() {
    String s = "🙂";

    GraphemeMatcher m = newGraphemeMatcher(s);

    assertThat(m.matches(), is(true));
    assertThat(m.start(), is(0));
    assertThat(m.end(), is(s.length()));
    assertThat(m.grapheme().getName(), is("slightly smiling face"));
  }

  /**
   * Run a test against every grapheme in the grapheme data.
   */
  @Test
  public void generatedTest() {
    List<String> graphemes = new ArrayList<>();

    StringBuilder text = new StringBuilder();
    for (GraphemeEntry g : Graphemes.getGraphemeData().getGraphemes()) {
      text.append(new String(g.getCanonicalCodePointSequence(), 0,
          g.getCanonicalCodePointSequence().length)).append(" ");
      graphemes.add(g.getName());
      for (int[] alternativeCodePointSequence : g.getAlternativeCodePointSequences()) {
        text.append(
            new String(alternativeCodePointSequence, 0, alternativeCodePointSequence.length))
            .append(" ");
        graphemes.add(g.getName());
      }
    }

    List<String> matches = new ArrayList<>();
    GraphemeMatcher m = newGraphemeMatcher(text.toString());
    while (m.find()) {
      matches.add(m.grapheme().getName());
    }

    assertThat(matches, is(graphemes));
  }
}
