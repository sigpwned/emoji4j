package com.sigpwned.emojis4j.maven;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CodePointSequence extends CodePointCollection implements Iterable<CodePoint> {
  private static final Pattern SPACE = Pattern.compile(" ");

  public static CodePointSequence fromString(String s) {
    return of(SPACE.splitAsStream(s.trim()).map(CodePoint::fromString).collect(toList()));
  }

  public static CodePointSequence of(CodePoint element) {
    return of(singletonList(element));
  }

  public static CodePointSequence of(List<CodePoint> elements) {
    return new CodePointSequence(elements);
  }

  private final List<CodePoint> elements;

  public CodePointSequence(List<CodePoint> elements) {
    if (elements.isEmpty())
      throw new IllegalArgumentException("no elements");
    this.elements = unmodifiableList(elements);
  }

  @Override
  public Type getType() {
    return Type.SEQUENCE;
  }

  public CodePoint getFirst() {
    return getElements().get(0);
  }

  public CodePoint getLast() {
    return getElements().get(size() - 1);
  }

  public CodePointSequence getPrefix(int count) {
    if (count <= 0)
      throw new IllegalArgumentException("count must be positive");
    if (count > size())
      throw new IllegalArgumentException("count must be no more than size");
    return of(new ArrayList<>(getElements().subList(0, count)));
  }

  /**
   * @return the elements
   */
  public List<CodePoint> getElements() {
    return elements;
  }

  @Override
  public Stream<CodePoint> stream() {
    return getElements().stream();
  }

  @Override
  public int size() {
    return getElements().size();
  }

  public CodePointSequence plus(CodePoint cp) {
    return plus(CodePointSequence.of(cp));
  }

  public CodePointSequence plus(CodePointSequence cps) {
    ArrayList<CodePoint> codePoints = new ArrayList<>(size() + cps.size());
    codePoints.addAll(getElements());
    codePoints.addAll(cps.getElements());
    return of(codePoints);
  }

  public int[] toArray() {
    return stream().mapToInt(CodePoint::getValue).toArray();
  }

  /**
   * Returns the number of times any of the given code points appear in this sequence
   */
  public int count(Set<CodePoint> cps) {
    return Math.toIntExact(stream().filter(cps::contains).count());
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CodePointSequence other = (CodePointSequence) obj;
    return Objects.equals(elements, other.elements);
  }

  @Override
  public String toString() {
    return getElements().stream().map(Objects::toString).collect(joining(" "));
  }

  @Override
  public Iterator<CodePoint> iterator() {
    return getElements().iterator();
  }
}
