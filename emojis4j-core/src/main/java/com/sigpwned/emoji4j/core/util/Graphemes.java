package com.sigpwned.emoji4j.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.sigpwned.emoji4j.core.GraphemeData;
import com.sigpwned.emoji4j.core.trie.HashMapGraphemeTrie;

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

  private static HashMapGraphemeTrie defaultTrie;

  public static synchronized HashMapGraphemeTrie getDefaultTrie() {
    if (defaultTrie == null)
      defaultTrie = HashMapGraphemeTrie.fromGraphemeData(getGraphemeData());
    return defaultTrie;
  }


}
