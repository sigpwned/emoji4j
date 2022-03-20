package com.sigpwned.emoji4j.core;

import java.util.Objects;
import java.util.Optional;

public class PersonHint {
  public static PersonHint of(AgeHint age, GenderHint gender, SkinToneHint skinTone) {
    return new PersonHint(age, gender, skinTone);
  }

  private final AgeHint age;
  private final GenderHint gender;
  private final SkinToneHint skinTone;

  public PersonHint(AgeHint age, GenderHint gender, SkinToneHint skinTone) {
    this.age = age;
    this.gender = gender;
    this.skinTone = skinTone;
  }

  /**
   * @return the age
   */
  public Optional<AgeHint> getAge() {
    return Optional.ofNullable(age);
  }

  /**
   * @return the gender
   */
  public Optional<GenderHint> getGender() {
    return Optional.ofNullable(gender);
  }

  /**
   * @return the skinTone
   */
  public Optional<SkinToneHint> getSkinTone() {
    return Optional.ofNullable(skinTone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(age, gender, skinTone);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PersonHint other = (PersonHint) obj;
    return age == other.age && gender == other.gender && skinTone == other.skinTone;
  }

  @Override
  public String toString() {
    return "PersonHint [age=" + age + ", gender=" + gender + ", skinTone=" + skinTone + "]";
  }
}
