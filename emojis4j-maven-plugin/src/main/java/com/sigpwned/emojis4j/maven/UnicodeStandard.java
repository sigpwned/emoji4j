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
  public static interface UnicodeDataHandler {
    public void codePoint(CodePoint codePoint, String shortName);
  }

  public void processUnicodeData(UnicodeDataHandler handler) throws IOException;
}
