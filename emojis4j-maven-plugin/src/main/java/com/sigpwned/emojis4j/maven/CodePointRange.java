package com.sigpwned.emojis4j.maven;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Closed interval, [from, to]
 */
public class CodePointRange extends CodePointCollection
    implements Comparable<CodePointRange>, Iterable<CodePoint> {
  public static CodePointRange fromString(String s) {
    String[] parts = s.split("\\.\\.", 3);
    if (parts.length == 1)
      return of(CodePoint.fromString(parts[0]));
    if (parts.length == 2)
      return of(CodePoint.fromString(parts[0]), CodePoint.fromString(parts[1]));
    throw new IllegalArgumentException("invalid range " + s);
  }

  public static CodePointRange of(CodePoint cp) {
    return of(cp, cp);
  }

  public static CodePointRange of(CodePoint from, CodePoint to) {
    return new CodePointRange(from, to);
  }

  private final CodePoint from;
  private final CodePoint to;

  public CodePointRange(CodePoint from, CodePoint to) {
    if (from == null)
      throw new NullPointerException();
    if (to == null)
      throw new NullPointerException();
    if (from.compareTo(to) > 0)
      throw new IllegalArgumentException("from > to");
    this.from = from;
    this.to = to;
  }

  @Override
  public Type getType() {
    return Type.RANGE;
  }

  /**
   * @return the from
   */
  public CodePoint getFrom() {
    return from;
  }

  /**
   * @return the to
   */
  public CodePoint getTo() {
    return to;
  }

  @Override
  public Stream<CodePoint> stream() {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(),
        Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED),
        false);
  }

  @Override
  public Iterator<CodePoint> iterator() {
    return new Iterator<CodePoint>() {
      private CodePoint cp = getFrom();

      @Override
      public boolean hasNext() {
        return cp.compareTo(getTo()) <= 0;
      }

      @Override
      public CodePoint next() {
        CodePoint result = cp;
        cp = cp.next();
        return result;
      }
    };
  }

  public boolean contains(CodePoint cp) {
    return getFrom().compareTo(cp) <= 0 && cp.compareTo(getTo()) <= 0;
  }

  @Override
  public int size() {
    return getTo().getValue() - getFrom().getValue() + 1;
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CodePointRange other = (CodePointRange) obj;
    return Objects.equals(from, other.from) && Objects.equals(to, other.to);
  }

  @Override
  public String toString() {
    if (getTo().equals(getFrom()))
      return getFrom().toString();
    else
      return getFrom() + ".." + getTo();
  }

  @Override
  public int compareTo(CodePointRange o) {
    int result = getFrom().compareTo(o.getFrom());
    if (result == 0)
      result = getTo().compareTo(o.getTo());
    return result;
  }
}
