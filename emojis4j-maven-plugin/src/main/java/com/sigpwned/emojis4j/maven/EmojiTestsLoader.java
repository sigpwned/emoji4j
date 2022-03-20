package com.sigpwned.emojis4j.maven;

import static java.util.stream.Collectors.toList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EmojiTestsLoader {
  public EmojiTests load(InputStream in) throws IOException {
    List<EmojiTests.Entry> entries;

    try (BufferedReader lines =
        new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
      // Example line:
      // 1F600 ; fully-qualified # 😀 E1.0 grinning face
      entries = lines
          .lines().map(DataLine::fromString).filter(dl -> !dl.isBlank()).map(dl -> EmojiTests.Entry
              .of(CodePointSequence.fromString(dl.getFields().get(0)), dl.getFields().get(1)))
          .collect(toList());
    }

    return EmojiTests.of(entries);
  }
}
