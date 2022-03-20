package com.sigpwned.emojis4j.maven;

import static java.util.stream.Collectors.toList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DerivedNamesLoader {
  public DerivedNames load(InputStream in) throws IOException {
    List<DerivedNames.Entry> entries = new ArrayList<>();

    try (BufferedReader lines =
        new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
      // Example line:
      // 0020 ; SPACE
      entries = lines.lines().map(DataLine::fromString).filter(dl -> !dl.isBlank())
          .map(dl -> DerivedNames.Entry.of(CodePointRange.fromString(dl.getFields().get(0)),
              dl.getFields().get(1)))
          .collect(toList());
    }

    return DerivedNames.of(entries);
  }
}
