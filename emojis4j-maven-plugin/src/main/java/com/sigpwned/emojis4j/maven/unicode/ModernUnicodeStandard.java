package com.sigpwned.emojis4j.maven.unicode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.sigpwned.emojis4j.maven.CodePoint;
import com.sigpwned.emojis4j.maven.CodePointCollection;
import com.sigpwned.emojis4j.maven.CodePointRange;
import com.sigpwned.emojis4j.maven.CodePointSequence;
import com.sigpwned.emojis4j.maven.DataLine;
import com.sigpwned.emojis4j.maven.UnicodeStandard;
import com.sigpwned.emojis4j.maven.UnicodeVersion;

/**
 * Works on Unicode versions 13.0, 14.0
 */
public class ModernUnicodeStandard implements UnicodeStandard {
  public static final UnicodeVersion EARLIEST_MODERN_VERSION = UnicodeVersion.of(13, 0, 0);

  private final UnicodeVersion version;

  public ModernUnicodeStandard(UnicodeVersion version) {
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

  /* default */ URL getEmojiSequenceUrl() {
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("emoji/%s/emoji-sequences.txt", getVersion().toMajorMinorString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji sequence URL", e);
    }
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
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("emoji/%s/emoji-zwj-sequences.txt", getVersion().toMajorMinorString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji ZWJ sequence URL", e);
    }
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
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("emoji/%s/emoji-test.txt", getVersion().toMajorMinorString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji test URL", e);
    }
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
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("%s/ucd/UnicodeData.txt", getVersion().toMajorMinorPatchString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse unicode data sequence URL", e);
    }
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
    try {
      return new URL(DEFAULT_BASE_URL,
          String.format("%s/ucd/emoji/emoji-data.txt", getVersion().toMajorMinorPatchString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji data sequence URL", e);
    }
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
    try {
      return new URL(DEFAULT_BASE_URL, String.format("%s/ucd/emoji/emoji-variation-sequences.txt",
          getVersion().toMajorMinorPatchString()));
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji data sequence URL", e);
    }
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
    return version;
  }
}
