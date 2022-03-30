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
package com.sigpwned.emoji4j.maven;

import java.io.IOException;

/**
 * An emoji-centric model of the data in a modern Unicode standard
 */
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
