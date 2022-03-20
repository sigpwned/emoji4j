package com.sigpwned.emojis4j.maven.util;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import com.sigpwned.emojis4j.maven.CodePoint;
import com.sigpwned.emojis4j.maven.CodePointSequence;
import com.sigpwned.emojis4j.maven.PersonHintBuilder;

public final class People {
  private People() {}

  /**
   * When this appears, it is the only person hint for this person.
   */
  public static final int BOY_CODE_POINT = 0x1F466;

  /**
   * When this appears, it is the only person hint for this person.
   */
  public static final int GIRL_CODE_POINT = 0x1F467;

  /**
   * When this appears, it is the only person hint for this person.
   */
  public static final int MAN_CODE_POINT = 0x1F468;

  /**
   * When this appears, it is the only person hint for this person.
   */
  public static final int WOMAN_CODE_POINT = 0x1F469;

  private static final Set<CodePoint> PEOPLE;
  static {
    Set<CodePoint> people = new HashSet<>(4);
    people.add(CodePoint.of(BOY_CODE_POINT));
    people.add(CodePoint.of(GIRL_CODE_POINT));
    people.add(CodePoint.of(MAN_CODE_POINT));
    people.add(CodePoint.of(WOMAN_CODE_POINT));
    PEOPLE = unmodifiableSet(people);
  }

  /**
   * This is never starts a person, but is a gender hint. It can be preceded by skin tone.
   */
  public static final int MALE_SIGN_CODE_POINT = 0x2642;

  /**
   * This is never starts a person, but is a gender hint. It can be preceded by skin tone.
   */
  public static final int FEMALE_SIGN_CODE_POINT = 0x2640;

  private static final Set<CodePoint> SIGNS;
  static {
    Set<CodePoint> signs = new HashSet<>(2);
    signs.add(CodePoint.of(MALE_SIGN_CODE_POINT));
    signs.add(CodePoint.of(FEMALE_SIGN_CODE_POINT));
    SIGNS = unmodifiableSet(signs);
  }

  /**
   * This can start a person
   */
  public static final int SKIN_TONE_LIGHT_CODE_POINT = 0x1F3FB;

  public static final int SKIN_TONE_MEDIUM_LIGHT_CODE_POINT = 0x1F3FC;

  public static final int SKIN_TONE_MEDIUM_CODE_POINT = 0x1F3FD;

  public static final int SKIN_TONE_MEDIUM_DARK_CODE_POINT = 0x1F3FE;

  public static final int SKIN_TONE_DARK_CODE_POINT = 0x1F3FF;

  public static final Pattern MAN_PATTERN = Pattern.compile("\\bman\\b", Pattern.CASE_INSENSITIVE);

  public static final Pattern WOMAN_PATTERN =
      Pattern.compile("\\bwoman\\b", Pattern.CASE_INSENSITIVE);

  public static final Pattern PERSON_PATTERN =
      Pattern.compile("\\bperson\\b", Pattern.CASE_INSENSITIVE);

  private static final Set<CodePoint> SKIN_TONES;
  static {
    Set<CodePoint> skinTones = new HashSet<>(2);
    skinTones.add(CodePoint.of(SKIN_TONE_LIGHT_CODE_POINT));
    skinTones.add(CodePoint.of(SKIN_TONE_MEDIUM_LIGHT_CODE_POINT));
    skinTones.add(CodePoint.of(SKIN_TONE_MEDIUM_CODE_POINT));
    skinTones.add(CodePoint.of(SKIN_TONE_MEDIUM_DARK_CODE_POINT));
    skinTones.add(CodePoint.of(SKIN_TONE_DARK_CODE_POINT));
    SKIN_TONES = unmodifiableSet(skinTones);
  }

  public static List<PersonHintBuilder> detect(CodePointSequence cps, String shortName) {
    int countPeopleCodePoints = cps.count(PEOPLE);
    int countSignCodePoints = cps.count(SIGNS);
    int countSkinToneCodePoints = cps.count(SKIN_TONES);

    boolean hasPeopleCodePoints = countPeopleCodePoints > 0;
    boolean hasSignCodePoints = countSignCodePoints > 0;
    boolean hasSkinToneCodePoints = countSkinToneCodePoints > 0;

    List<PersonHintBuilder> result;
    if (hasSignCodePoints && !hasPeopleCodePoints) {
      // There is only ever one male or female sign. It may or may not be accompanied by a skin tone
      // marker. It should never co-appear with a person marker.
      if (countSignCodePoints != 1)
        throw new AssertionError(
            format("Code point sequence %s has %d signs", countSignCodePoints));

      CodePoint sign = cps.stream().filter(SIGNS::contains).findFirst().get();

      CodePoint skinTone = cps.stream().filter(SKIN_TONES::contains).findFirst().orElse(null);

      result = singletonList(new PersonHintBuilder().withGender(gender(sign))
          .withSkinTone(Optional.ofNullable(skinTone).map(People::skinTone).orElse(null)));
    } else if (hasPeopleCodePoints && !hasSignCodePoints) {
      // There may be multiple person markers. If there is a skin tone marker, the person marker
      // will precede it. If any person has skin tone information, then every person does.
      if (countSkinToneCodePoints != 0 && countSkinToneCodePoints != countPeopleCodePoints)
        throw new AssertionError(
            format("Code point sequence %s has skin tone count %d for people count %d", cps,
                countSkinToneCodePoints, countPeopleCodePoints));

      PersonHintBuilder person = null;

      result = new ArrayList<>(countPeopleCodePoints);
      for (CodePoint cp : cps) {
        if (PEOPLE.contains(cp)) {
          // This starts a new person.
          if (person != null)
            result.add(person);
          person = new PersonHintBuilder().withAge(age(cp)).withGender(gender(cp));
        } else if (SKIN_TONES.contains(cp)) {
          if (person == null)
            throw new IllegalStateException(
                format("Code point sequence %s has skin tone marker without active person", cps));
          person.withSkinTone(skinTone(cp));
        }
      }

      if (person != null)
        result.add(person);
    } else if (!hasPeopleCodePoints && !hasSignCodePoints) {
      // We may have skin tone in this case. If so, we'll only have one. If there is any information
      // about gender or age to have, it'll be in the name.
      if (countSkinToneCodePoints > 1)
        if (countSkinToneCodePoints != 0 && countSkinToneCodePoints != countPeopleCodePoints)
          throw new AssertionError(format("Code point sequence %s has multiple skin tone count %d",
              cps, countSkinToneCodePoints));

      boolean hasManName = MAN_PATTERN.matcher(shortName).find();
      boolean hasWomanName = WOMAN_PATTERN.matcher(shortName).find();
      boolean hasPersonName = PERSON_PATTERN.matcher(shortName).find();

      if (hasSkinToneCodePoints || hasManName || hasWomanName || hasPersonName) {
        // What about police officer without skin tone?
        // Do we care about man/woman order in name?
        if (hasManName || hasWomanName) {
          result = new ArrayList<>(2);
          if (hasManName) {
            result.add(new PersonHintBuilder().withAge(Ages.ADULT).withGender(Genders.MALE)
                .withSkinTone(cps.stream().filter(SKIN_TONES::contains).map(People::skinTone)
                    .findFirst().orElse(null)));
          }
          if (hasWomanName) {
            result.add(new PersonHintBuilder().withAge(Ages.ADULT).withGender(Genders.FEMALE)
                .withSkinTone(cps.stream().filter(SKIN_TONES::contains).map(People::skinTone)
                    .findFirst().orElse(null)));
          }
        } else {
          PersonHintBuilder person = new PersonHintBuilder();
          if (hasSkinToneCodePoints)
            person.withSkinTone(
                skinTone(cps.stream().filter(SKIN_TONES::contains).findFirst().get()));
          result = singletonList(person);
        }
      } else {
        result = emptyList();
      }
    } else {
      throw new AssertionError(format("Code point sequence %s has people and signs", cps));
    }

    return unmodifiableList(result);
  }

  /* default */ static String age(CodePoint cp) {
    switch (cp.getValue()) {
      case BOY_CODE_POINT:
      case GIRL_CODE_POINT:
        return Ages.CHILD;
      case MAN_CODE_POINT:
      case WOMAN_CODE_POINT:
        return Ages.ADULT;
      default:
        throw new IllegalArgumentException("no age information in code point" + cp);
    }
  }

  /* default */ static String gender(CodePoint cp) {
    switch (cp.getValue()) {
      case MALE_SIGN_CODE_POINT:
      case BOY_CODE_POINT:
      case MAN_CODE_POINT:
        return Genders.MALE;
      case FEMALE_SIGN_CODE_POINT:
      case GIRL_CODE_POINT:
      case WOMAN_CODE_POINT:
        return Genders.FEMALE;
      default:
        throw new IllegalArgumentException("no gender information in code point" + cp);
    }
  }

  /* default */ static String skinTone(CodePoint cp) {
    switch (cp.getValue()) {
      case SKIN_TONE_LIGHT_CODE_POINT:
        return SkinTones.LIGHT;
      case SKIN_TONE_MEDIUM_LIGHT_CODE_POINT:
        return SkinTones.MEDIUM_LIGHT;
      case SKIN_TONE_MEDIUM_CODE_POINT:
        return SkinTones.MEDIUM;
      case SKIN_TONE_MEDIUM_DARK_CODE_POINT:
        return SkinTones.MEDIUM_DARK;
      case SKIN_TONE_DARK_CODE_POINT:
        return SkinTones.DARK;
      default:
        throw new IllegalArgumentException("no skin tone information in code point" + cp);
    }
  }
}
