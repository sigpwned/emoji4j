package com.sigpwned.emoji4j.core.trie;

import com.sigpwned.emoji4j.core.GraphemeMatcher;
import com.sigpwned.emoji4j.core.GraphemeMatcherTest;
import com.sigpwned.emoji4j.core.util.Graphemes;

public class DefaultGraphemeTrieGraphemeMatcherTest extends GraphemeMatcherTest {
  @Override
  public GraphemeMatcher newGraphemeMatcher(String input) {
    DefaultGraphemeTrie trie = Graphemes.getDefaultTrie();
    return new GraphemeMatcher(trie, input);
  }
}
