package com.sigpwned.emojis4j.maven;

import static java.util.Collections.unmodifiableMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GraphemeCollection implements Iterable<GraphemeMapping> {
  public static GraphemeCollection of(Map<CodePointSequence, GraphemeBuilder> elements) {
    return new GraphemeCollection(elements);
  }

  private final Map<CodePointSequence, GraphemeBuilder> elements;

  public GraphemeCollection(Map<CodePointSequence, GraphemeBuilder> elements) {
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
