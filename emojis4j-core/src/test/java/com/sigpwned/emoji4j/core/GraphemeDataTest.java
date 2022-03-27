package com.sigpwned.emoji4j.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.emoji4j.core.util.Graphemes;

public class GraphemeDataTest {
  /**
   * Basic test ensuring we have the expected unicode version and grapheme counts. If you change the
   * unicode version in the build, then you will need to change the unicode version here, too.
   */
  @Test
  public void smokeTest() {
    GraphemeData d = Graphemes.getGraphemeData();

    assertThat(d.getUnicodeVersion(), is("14.0"));

    assertThat(
        d.getGraphemes().stream().filter(g -> g.getType().equals(GraphemeEntry.EMOJI_TYPE)).count(),
        is(3633L));

    assertThat(d.getGraphemes().stream()
        .filter(g -> g.getType().equals(GraphemeEntry.PICTOGRAPHIC_TYPE)).count(), is(993L));
  }
}
