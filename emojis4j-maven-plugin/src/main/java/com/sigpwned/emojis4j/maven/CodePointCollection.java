package com.sigpwned.emojis4j.maven;

import java.util.stream.Stream;

/**
 * A model of multiple unicode code points
 */
public abstract class CodePointCollection implements Iterable<CodePoint> {
  public static CodePointCollection fromString(String s) {
    s = s.trim();
    if (s.contains(" "))
      return CodePointSequence.fromString(s);
    return CodePointRange.fromString(s);
  }

  public static enum Type {
    RANGE, SEQUENCE;
  }

  public abstract Type getType();

  public CodePointRange asRange() {
    return (CodePointRange) this;
  }

  public CodePointSequence asSequence() {
    return (CodePointSequence) this;
  }

  public abstract int size();

  public abstract Stream<CodePoint> stream();
}
