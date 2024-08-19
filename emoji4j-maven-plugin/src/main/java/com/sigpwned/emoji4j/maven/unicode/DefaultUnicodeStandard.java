/*-
 * =================================LICENSE_START==================================
 * emoji4j-maven-plugin
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
package com.sigpwned.emoji4j.maven.unicode;

import static java.util.Objects.requireNonNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.sigpwned.emoji4j.maven.CodePoint;
import com.sigpwned.emoji4j.maven.CodePointCollection;
import com.sigpwned.emoji4j.maven.CodePointRange;
import com.sigpwned.emoji4j.maven.CodePointSequence;
import com.sigpwned.emoji4j.maven.DataLine;
import com.sigpwned.emoji4j.maven.UnicodeStandard;
import com.sigpwned.emoji4j.maven.UnicodeVersion;

/**
 * Works on Unicode versions 13.0, 14.0
 */
public class DefaultUnicodeStandard implements UnicodeStandard {
  public static final UnicodeVersion EARLIEST_MODERN_VERSION = UnicodeVersion.of(13, 0, 0);

  private final UnicodeStandardResolver resolver;

  public DefaultUnicodeStandard(UnicodeStandardResolver resolver) {
    this.resolver = requireNonNull(resolver);
  }

  /* default */ URL getEmojiSequenceUrl() {
    return getResolver().getEmojiSequenceUrl();
  }

  @Override
  public void processEmojiSequences(EmojiSequenceHandler handler) throws IOException {
    try (BufferedReader lines = new BufferedReader(
        new InputStreamReader(getEmojiSequenceUrl().openStream(), StandardCharsets.UTF_8))) {
      lines.lines().map(DataLine::fromString).filter(dl -> !dl.isBlank()).forEach(dl -> {
        CodePointCollection codePoints = CodePointCollection.fromString(dl.getField(0));
        String typeField = dl.getField(1);
        String shortName = dl.getField(2);
        handler.sequence(codePoints, typeField, shortName);
      });
    }
  }

  /* default */ URL getEmojiZwjSequenceUrl() {
    return getResolver().getEmojiZwjSequenceUrl();
  }

  @Override
  public void processEmojiZwjSequences(EmojiZwjSequenceHandler handler) throws IOException {
    try (BufferedReader lines = new BufferedReader(
        new InputStreamReader(getEmojiZwjSequenceUrl().openStream(), StandardCharsets.UTF_8))) {
      lines.lines().map(DataLine::fromString).filter(dl -> !dl.isBlank()).forEach(dl -> {
        CodePointSequence codePoints = CodePointSequence.fromString(dl.getField(0));
        String typeField = dl.getField(1);
        String shortName = dl.getField(2);
        handler.sequence(codePoints, typeField, shortName);
      });
    }
  }

  /* default */ URL getEmojiTestUrl() {
    return getResolver().getEmojiTestUrl();
  }

  @Override
  public void processEmojiTest(EmojiTestHandler handler) throws IOException {
    try (BufferedReader lines = new BufferedReader(
        new InputStreamReader(getEmojiTestUrl().openStream(), StandardCharsets.UTF_8))) {
      lines.lines().map(DataLine::fromString).filter(dl -> !dl.isBlank()).forEach(dl -> {
        CodePointSequence codePoints = CodePointSequence.fromString(dl.getField(0));
        String status = dl.getField(1);
        handler.sequence(codePoints, status);
      });
    }
  }

  /* default */ URL getUnicodeDataUrl() {
    return getResolver().getUnicodeDataUrl();
  }

  @Override
  public void processUnicodeData(UnicodeDataHandler handler) throws IOException {
    try (BufferedReader lines = new BufferedReader(
        new InputStreamReader(getUnicodeDataUrl().openStream(), StandardCharsets.UTF_8))) {
      lines.lines().map(DataLine::fromString).filter(dl -> !dl.isBlank()).forEach(dl -> {
        CodePoint codePoint = CodePoint.fromString(dl.getField(0));
        String shortName = dl.getField(1);
        handler.codePoint(codePoint, shortName);
      });
    }
  }

  /* default */ URL getEmojiDataUrl() {
    return getResolver().getEmojiDataUrl();
  }

  @Override
  public void processEmojiData(EmojiDataHandler handler) throws IOException {
    try (BufferedReader lines = new BufferedReader(
        new InputStreamReader(getEmojiDataUrl().openStream(), StandardCharsets.UTF_8))) {
      lines.lines().map(DataLine::fromString).filter(dl -> !dl.isBlank()).forEach(dl -> {
        CodePointRange codePoints = CodePointRange.fromString(dl.getField(0));
        String property = dl.getField(1);
        handler.sequence(codePoints, property);
      });
    }
  }

  /* default */ URL getEmojiVariationSequencesUrl() {
    return getResolver().getEmojiVariationSequencesUrl();
  }

  @Override
  public void processEmojiVariationSequences(EmojiVariationSequenceHandler handler)
      throws IOException {
    try (BufferedReader lines =
        new BufferedReader(new InputStreamReader(getEmojiVariationSequencesUrl().openStream(),
            StandardCharsets.UTF_8))) {
      lines.lines().map(DataLine::fromString).filter(dl -> !dl.isBlank()).forEach(dl -> {
        CodePointSequence codePoints = CodePointSequence.fromString(dl.getField(0));
        String style = dl.getField(1);
        handler.variation(codePoints, style);
      });
    }
  }

  @Override
  public UnicodeVersion getVersion() {
    return getResolver().getVersion();
  }

  public UnicodeStandardResolver getResolver() {
    return resolver;
  }
}
