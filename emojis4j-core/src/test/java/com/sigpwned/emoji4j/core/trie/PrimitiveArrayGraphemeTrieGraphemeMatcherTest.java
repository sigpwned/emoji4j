package com.sigpwned.emoji4j.core.trie;

import com.sigpwned.emoji4j.core.GraphemeMatcher;
import com.sigpwned.emoji4j.core.GraphemeMatcherTest;
import com.sigpwned.emoji4j.core.util.Graphemes;

public class PrimitiveArrayGraphemeTrieGraphemeMatcherTest extends GraphemeMatcherTest {
  public PrimitiveArrayGraphemeTrie primitiveArrayTrie;

  @Override
  public synchronized GraphemeMatcher newGraphemeMatcher(String input) {
    if (primitiveArrayTrie == null)
      primitiveArrayTrie = PrimitiveArrayGraphemeTrie.compile(Graphemes.getDefaultTrie());
    return new GraphemeMatcher(primitiveArrayTrie, input);
  }
}
