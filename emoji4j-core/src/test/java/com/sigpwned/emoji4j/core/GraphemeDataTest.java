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
import org.junit.Test;
import com.sigpwned.emoji4j.core.data.GraphemeEntry;
import com.sigpwned.emoji4j.core.util.Graphemes;

public class GraphemeDataTest {
  /**
   * Basic test ensuring we have the expected unicode version and grapheme counts. If you change the
   * unicode version in the build, then you will need to change the unicode version here, too.
   */
  @Test
  public void smokeTest() {
    GraphemeData d = Graphemes.getGraphemeData();

    assertThat(d.getUnicodeVersion(), is("15.0"));

    assertThat(
        d.getGraphemes().stream().filter(g -> g.getType().equals(GraphemeEntry.EMOJI_TYPE)).count(),
        is(3664L));

    assertThat(d.getGraphemes().stream()
        .filter(g -> g.getType().equals(GraphemeEntry.PICTOGRAPHIC_TYPE)).count(), is(1002L));
  }
}
