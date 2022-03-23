package com.sigpwned.emojis4j.maven;

import java.io.IOException;

public interface UnicodeStandard {
  public UnicodeVersion getVersion();

  @FunctionalInterface
  public static interface EmojiSequenceHandler {
    public void sequence(CodePointCollection codePoints, String typeField, String shortName);
  }

  public void processEmojiSequences(EmojiSequenceHandler handler) throws IOException;

  @FunctionalInterface
  public static interface EmojiZwjSequenceHandler {
    public void sequence(CodePointSequence codePoints, String typeField, String shortName);
  }

  public void processEmojiZwjSequences(EmojiZwjSequenceHandler handler) throws IOException;

  @FunctionalInterface
  public static interface EmojiTestHandler {
    public void sequence(CodePointSequence codePoints, String status);
  }

  public void processEmojiTest(EmojiTestHandler handler) throws IOException;

  @FunctionalInterface
  public static interface UnicodeDataHandler {
    public void codePoint(CodePoint codePoint, String shortName);
  }

  public void processUnicodeData(UnicodeDataHandler handler) throws IOException;

  @FunctionalInterface
  public static interface EmojiDataHandler {
    public void sequence(CodePointRange codePoints, String property);
  }

  public void processEmojiData(EmojiDataHandler handler) throws IOException;

  @FunctionalInterface
  public static interface EmojiVariationSequenceHandler {
    public void variation(CodePointSequence codePoints, String style);
  }

  public void processEmojiVariationSequences(EmojiVariationSequenceHandler handler)
      throws IOException;
}
