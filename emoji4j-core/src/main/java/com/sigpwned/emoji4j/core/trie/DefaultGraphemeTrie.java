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
package com.sigpwned.emoji4j.core.trie;

import com.sigpwned.emoji4j.core.Grapheme;
import com.sigpwned.emoji4j.core.GraphemeData;
import com.sigpwned.emoji4j.core.GraphemeTrie;
import com.sigpwned.emoji4j.core.data.GraphemeEntry;
import com.sigpwned.emoji4j.core.grapheme.Emoji;
import com.sigpwned.emoji4j.core.grapheme.Pictographic;
import com.sigpwned.emoji4j.core.org.apache.commons.lang.IntHashMap;

public class DefaultGraphemeTrie implements GraphemeTrie {
  public static DefaultGraphemeTrie fromGraphemeData(GraphemeData gs) {
    DefaultGraphemeTrie result = new DefaultGraphemeTrie();
    for (GraphemeEntry g : gs.getGraphemes()) {
      Grapheme grapheme;
      switch (g.getType()) {
        case GraphemeEntry.EMOJI_TYPE:
          grapheme = new Emoji(g.getCanonicalCodePointSequence(), g.getName());
          break;
        case GraphemeEntry.PICTOGRAPHIC_TYPE:
          grapheme = new Pictographic(g.getCanonicalCodePointSequence(), g.getName());
          break;
        default:
          throw new IllegalArgumentException("unrecognized grapheme entry type " + g.getType());
      }

      result.put(g.getCanonicalCodePointSequence(), grapheme);
      for (int[] alternativeCodePointSequence : g.getAlternativeCodePointSequences())
        result.put(alternativeCodePointSequence, grapheme);
    }
    return result;
  }

  private final IntHashMap<DefaultGraphemeTrie> children;
  private Grapheme grapheme;

  public DefaultGraphemeTrie() {
    this.children = new IntHashMap<>();
  }

  @Override
  public DefaultGraphemeTrie getChild(int codePoint) {
    return children.get(codePoint);
  }

  private void put(int[] codePointSequence, Grapheme grapheme) {
    DefaultGraphemeTrie ti = this;
    for (int codePoint : codePointSequence)
      ti = ti.getOrCreateChild(codePoint);
    ti.setGrapheme(grapheme);
  }

  private DefaultGraphemeTrie getOrCreateChild(int codePoint) {
    DefaultGraphemeTrie result = children.get(codePoint);
    if (result == null) {
      result = new DefaultGraphemeTrie();
      children.put(codePoint, result);
    }
    return result;
  }

  public int size() {
    return children.size();
  }

  /**
   * @return the grapheme
   */
  @Override
  public Grapheme getGrapheme() {
    return grapheme;
  }

  /**
   * @param grapheme the grapheme to set
   */
  private void setGrapheme(Grapheme grapheme) {
    this.grapheme = grapheme;
  }
}
