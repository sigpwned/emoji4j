package com.sigpwned.emoji4j.core.grapheme;

import static java.util.Collections.unmodifiableList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.sigpwned.emoji4j.core.Grapheme;
import com.sigpwned.emoji4j.core.PersonHint;

public class Emoji extends Grapheme {
  public static Emoji of(int[] coordinates, String name, List<PersonHint> people) {
    return new Emoji(coordinates, name, people);
  }

  private final List<PersonHint> people;

  public Emoji(int[] coordinates, String name, List<PersonHint> people) {
    super(Type.EMOJI, coordinates, name);
    this.people = unmodifiableList(people);
  }

  /**
   * @return the people
   */
  public List<PersonHint> getPeople() {
    return people;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(people);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Emoji other = (Emoji) obj;
    return Objects.equals(people, other.people);
  }

  @Override
  public String toString() {
    return "Emoji [getPeople()=" + getPeople() + ", getCoordinates()="
        + Arrays.toString(getCoordinates()) + ", getName()=" + getName() + "]";
  }
}
