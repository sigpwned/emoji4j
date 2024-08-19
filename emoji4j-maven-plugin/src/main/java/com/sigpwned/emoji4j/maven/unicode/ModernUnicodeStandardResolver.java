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

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.sigpwned.emoji4j.maven.UnicodeVersion;

/**
 * Works on Unicode versions 13.0, 14.0
 */
public class ModernUnicodeStandardResolver implements UnicodeStandardResolver {
  public static final UnicodeVersion EARLIEST_MODERN_VERSION = UnicodeVersion.of(13, 0, 0);

  private final UnicodeVersion version;

  public ModernUnicodeStandardResolver(UnicodeVersion version) {
    if (version.compareTo(EARLIEST_MODERN_VERSION) < 0)
      throw new IllegalArgumentException("earliest modern version is " + EARLIEST_MODERN_VERSION);
    this.version = version;
  }

  /* default */ static final URL DEFAULT_BASE_URL;
  static {
    try {
      DEFAULT_BASE_URL = new URL("https://unicode.org/Public/");
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse default Unicode URL", e);
    }
  }

  @Override
  public URL getEmojiSequenceUrl() {
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("emoji/%s/emoji-sequences.txt", getVersion().toMajorMinorString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji sequence URL", e);
    }
  }

  @Override
  public URL getEmojiZwjSequenceUrl() {
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("emoji/%s/emoji-zwj-sequences.txt", getVersion().toMajorMinorString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji ZWJ sequence URL", e);
    }
  }

  @Override
  public URL getEmojiTestUrl() {
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("emoji/%s/emoji-test.txt", getVersion().toMajorMinorString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji test URL", e);
    }
  }

  @Override
  public URL getUnicodeDataUrl() {
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("%s/ucd/UnicodeData.txt", getVersion().toMajorMinorPatchString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse unicode data sequence URL", e);
    }
  }

  @Override
  public URL getEmojiDataUrl() {
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("%s/ucd/emoji/emoji-data.txt", getVersion().toMajorMinorPatchString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji data sequence URL", e);
    }
  }

  @Override
  public URL getEmojiVariationSequencesUrl() {
    try {
      return new URL(DEFAULT_BASE_URL, String.format("%s/ucd/emoji/emoji-variation-sequences.txt",
          getVersion().toMajorMinorPatchString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji data sequence URL", e);
    }
  }

  public UnicodeVersion getVersion() {
    return version;
  }
}
