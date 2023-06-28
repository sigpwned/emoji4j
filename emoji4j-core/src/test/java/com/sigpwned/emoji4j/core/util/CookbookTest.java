/*-
 * =================================LICENSE_START==================================
 * emoji4j-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2023 Andy Boothe
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
package com.sigpwned.emoji4j.core.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.junit.Test;
import com.sigpwned.emoji4j.core.GraphemeMatchResult;
import com.sigpwned.emoji4j.core.GraphemeMatcher;

public abstract class CookbookTest {
  public abstract GraphemeMatcher newGraphemeMatcher(String input);

  public boolean isStringAllEmoji(String input) {
    AtomicBoolean result = new AtomicBoolean(input.isEmpty());
    newGraphemeMatcher(input).results().forEachOrdered(new Consumer<GraphemeMatchResult>() {
      private boolean onto = true;
      private int lastEnd = 0;

      @Override
      public void accept(GraphemeMatchResult m) {
        if (m.start() != lastEnd)
          onto = false;
        result.set(onto && m.end() == input.length());
        lastEnd = m.end();
      }
    });
    return result.get();
  }

  @Test
  public void isStringAllEmojiTest_Positive() {
    String woman1 = "👩";
    String woman2 = "👩🏼";
    String woman3 = "👩🏽";
    String woman4 = "👩🏾";
    String woman5 = "👩🏿";

    String women = String.join("", new String[] {woman1, woman2, woman3, woman4, woman5});
    
    assertThat(isStringAllEmoji(women), is(true));
  }

  @Test
  public void isStringAllEmojiTest_Negative1() {
    String woman1 = "👩";
    String woman2 = "👩🏼";
    String woman3 = "👩🏽";
    String woman4 = "👩🏾";
    String woman5 = "👩🏿";

    String women = String.join(" ", new String[] {woman1, woman2, woman3, woman4, woman5});
    
    assertThat(isStringAllEmoji(women), is(false));
  }

  @Test
  public void isStringAllEmojiTest_Negative2() {
    assertThat(isStringAllEmoji("Hello, world!"), is(false));
  }
}
