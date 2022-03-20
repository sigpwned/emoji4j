package com.sigpwned.emojis4j.maven;

import static java.util.stream.Collectors.toList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EmojiDataLoader {
  public EmojiData load(InputStream in) throws IOException {
    List<EmojiData.Entry> entries;

    try (BufferedReader lines =
        new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
      // Example line:
      // 0030..0039 ; Emoji # E0.0 [10] (0️..9️) digit zero..digit nine
      entries = lines
          .lines().map(DataLine::fromString).filter(dl -> !dl.isBlank()).map(dl -> EmojiData.Entry
              .of(CodePointRange.fromString(dl.getFields().get(0)), dl.getFields().get(1)))
          .collect(toList());
    }

    return EmojiData.of(entries);
  }
}
