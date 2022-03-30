package com.sigpwned.emoji4j.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.sigpwned.emoji4j.core.data.GraphemeData;
import com.sigpwned.emoji4j.core.trie.DefaultGraphemeTrie;

public class Graphemes {
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
