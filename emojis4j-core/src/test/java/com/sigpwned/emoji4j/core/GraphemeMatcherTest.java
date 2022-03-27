package com.sigpwned.emoji4j.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

public class GraphemeMatcherTest {
  @Test
  public void test() {
    GraphemeMatcher m = new GraphemeMatcher("hello 🙂 world");

    assertThat(m.find(), is(true));
    assertThat(m.start(), is(6));
    assertThat(m.getGrapheme().getName(), is("slightly smiling face"));

    assertThat(m.replaceAll(r -> "SMILE"), is("hello SMILE world"));
  }
}
