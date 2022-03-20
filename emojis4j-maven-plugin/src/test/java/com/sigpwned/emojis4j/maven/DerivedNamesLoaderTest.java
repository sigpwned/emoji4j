package com.sigpwned.emojis4j.maven;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class DerivedNamesLoaderTest {
  @Test
  public void fullTest() throws IOException {
    DerivedNames derivedNames;
    try (InputStream in = Resources.getResource("test-derived-names-full.txt").openStream()) {
      derivedNames = new DerivedNamesLoader().load(in);
    }
    assertThat(derivedNames.getEntries().size(), is(43834));
  }

  @Test
  public void sampleTest() throws IOException {
    DerivedNames data;
    try (InputStream in = Resources.getResource("test-derived-names-sample.txt").openStream()) {
      data = new DerivedNamesLoader().load(in);
    }
    assertThat(data, is(DerivedNames.of(ImmutableList
        .of(DerivedNames.Entry.of(CodePointRange.of(CodePoint.of(0x0020)), "SPACE")))));
  }
}
