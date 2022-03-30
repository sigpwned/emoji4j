package com.sigpwned.emoji4j.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import com.sigpwned.emoji4j.core.GraphemeData;
import com.sigpwned.emoji4j.core.org.json.JSONObject;
import com.sigpwned.emoji4j.core.org.json.JSONTokener;
import com.sigpwned.emoji4j.core.trie.DefaultGraphemeTrie;

public final class Graphemes {
  private Graphemes() {}

  public static GraphemeData getGraphemeData() {
    JSONObject o;
    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("graphemes.json")) {
      o = new JSONObject(new JSONTokener(in));
    } catch (IOException e) {
      throw new UncheckedIOException("failed to load grapheme data", e);
    }
    return Serialization.deserializeGraphemeData(o);
  }

  private static DefaultGraphemeTrie defaultTrie;

  public static synchronized DefaultGraphemeTrie getDefaultTrie() {
    if (defaultTrie == null)
      defaultTrie = DefaultGraphemeTrie.fromGraphemeData(getGraphemeData());
    return defaultTrie;
  }


}
