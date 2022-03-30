package com.sigpwned.emojis4j.maven;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import com.google.common.collect.Streams;

/**
 * An immutable mapping of code point sequences to graphemes
 */
public class GraphemeCollection implements Iterable<GraphemeMapping> {
  public static GraphemeCollection of(Map<CodePointSequence, GraphemeBuilder> elements) {
    return new GraphemeCollection(elements);
  }

  private final Map<CodePointSequence, GraphemeBuilder> elements;

  public GraphemeCollection(Map<CodePointSequence, GraphemeBuilder> elements) {
    // Are the graphemes valid?
    for (GraphemeBuilder grapheme : elements.values()) {
      if (grapheme.getCanonicalCodePointSequence() == null)
        throw new IllegalArgumentException("grapheme is missing canonical code point sequence");
      if (grapheme.getShortName() == null)
        throw new IllegalArgumentException(
            format("grapheme %s is missing name", grapheme.getCanonicalCodePointSequence()));
      if (grapheme.getType() == null)
        throw new IllegalArgumentException(
            format("grapheme %s is missing type", grapheme.getCanonicalCodePointSequence()));
    }

    // Is the mapping valid?
    for (Map.Entry<CodePointSequence, GraphemeBuilder> e : elements.entrySet()) {
      CodePointSequence cps = e.getKey();
      GraphemeBuilder grapheme = e.getValue();
      if (!grapheme.getCanonicalCodePointSequence().equals(e.getKey()))
        throw new IllegalArgumentException(
            format("grapheme mapping canonical code point sequence %s does not match mapping %s",
                grapheme.getCanonicalCodePointSequence(), cps));
    }

    // Do the code point sequences overlap?
    Map<CodePointSequence, Long> counts =
        elements.values().stream().flatMap(g -> g.getAllCodePointSequences().stream())
            .collect(groupingBy(Function.identity(), counting())).entrySet().stream()
            .filter(e -> e.getValue() > 1L).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    if (!counts.isEmpty())
      throw new IllegalArgumentException(
          format("the following code point sequences appear under multiple graphemes: %s",
              counts.keySet()));

    this.elements = unmodifiableMap(elements);
  }

  public Optional<GraphemeBuilder> get(CodePointSequence cps) {
    return Optional.ofNullable(getElements().get(cps));
  }

  public Stream<GraphemeMapping> stream() {
    return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), 0), false);
  }

  @Override
  public Iterator<GraphemeMapping> iterator() {
    Iterator<Map.Entry<CodePointSequence, GraphemeBuilder>> iterator =
        getElements().entrySet().iterator();
    return new Iterator<GraphemeMapping>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public GraphemeMapping next() {
        Map.Entry<CodePointSequence, GraphemeBuilder> e = iterator.next();
        return GraphemeMapping.of(e.getKey(), e.getValue());
      }
    };
  }

  public GraphemeCollection merge(GraphemeCollection other) {
    return GraphemeCollection.of(
        Streams.concat(getElements().entrySet().stream(), other.getElements().entrySet().stream())
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, GraphemeBuilder::merge,
                LinkedHashMap::new)));
  }

  public GraphemeCollection join(GraphemeCollection other,
      BinaryOperator<GraphemeBuilder> mergeFunction) {
    throw new UnsupportedOperationException();
  }

  public int size() {
    return getElements().size();
  }

  /**
   * @return the elements
   */
  private Map<CodePointSequence, GraphemeBuilder> getElements() {
    return elements;
  }
}
