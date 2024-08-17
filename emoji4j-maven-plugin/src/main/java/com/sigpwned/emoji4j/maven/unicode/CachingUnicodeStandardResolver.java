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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.sigpwned.emoji4j.maven.UnicodeVersion;

public class CachingUnicodeStandardResolver implements UnicodeStandardResolver {
  private final File cacheDir;
  private final UnicodeStandardResolver delegate;
  private final File emojiSequenceFile;
  private final File emojiZwjSequenceFile;
  private final File emojiTestFile;
  private final File unicodeDataFile;
  private final File emojiDataFile;
  private final File emojiVariationSequences;

  public CachingUnicodeStandardResolver(File cacheDir, UnicodeStandardResolver delegate) {
    this.cacheDir = requireNonNull(cacheDir);
    this.delegate = requireNonNull(delegate);
    if (!cacheDir.isDirectory())
      throw new IllegalArgumentException("cache directory must be a directory");

    this.emojiSequenceFile = new File(cacheDir, "emoji-sequences.txt");
    this.emojiZwjSequenceFile = new File(cacheDir, "emoji-zwj-sequences.txt");
    this.emojiTestFile = new File(cacheDir, "emoji-test.txt");
    this.unicodeDataFile = new File(cacheDir, "UnicodeData.txt");
    this.emojiDataFile = new File(cacheDir, "emoji-data.txt");
    this.emojiVariationSequences = new File(cacheDir, "emoji-variation-sequences.txt");
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
    return getCachedUrl(emojiSequenceFile, getDelegate().getEmojiSequenceUrl());
  }

  @Override
  public URL getEmojiZwjSequenceUrl() {
    return getCachedUrl(emojiZwjSequenceFile, getDelegate().getEmojiZwjSequenceUrl());
  }

  @Override
  public URL getEmojiTestUrl() {
    return getCachedUrl(emojiTestFile, getDelegate().getEmojiTestUrl());
  }

  @Override
  public URL getUnicodeDataUrl() {
    return getCachedUrl(unicodeDataFile, getDelegate().getUnicodeDataUrl());
  }

  @Override
  public URL getEmojiDataUrl() {
    return getCachedUrl(emojiDataFile, getDelegate().getEmojiDataUrl());
  }

  @Override
  public URL getEmojiVariationSequencesUrl() {
    return getCachedUrl(emojiVariationSequences, getDelegate().getEmojiVariationSequencesUrl());
  }

  @Override
  public UnicodeVersion getVersion() {
    return getDelegate().getVersion();
  }

  @SuppressWarnings("unused")
  private File getCacheDir() {
    return cacheDir;
  }

  private UnicodeStandardResolver getDelegate() {
    return delegate;
  }

  private static URL getCachedUrl(File file, URL url) {
    if (!file.isFile()) {
      try (FileOutputStream out = new FileOutputStream(file)) {
        try (InputStream in = url.openStream()) {
          transfer(in, out);
        }
      } catch (IOException e) {
        throw new UncheckedIOException("Failed to cache emoji sequence file", e);
      }
    }
    try {
      return file.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new UncheckedIOException("Failed to parse emoji sequence cache URL", e);
    }
  }

  private static void transfer(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[8192];
    for (int nread = in.read(buffer); nread != -1; nread = in.read(buffer))
      out.write(buffer, 0, nread);
  }
}
