package com.sigpwned.emojis4j.maven;

import java.util.Optional;
import com.sigpwned.emojis4j.maven.util.Ages;
import com.sigpwned.emojis4j.maven.util.Genders;
import com.sigpwned.emojis4j.maven.util.SkinTones;

public class PersonHintBuilder {
  // MALE SIGN
  // SKIN TONE ... MALE SIGN
  // MAN
  // MAN ... SKIN TONE
  // SKIN TONE

  // MALE / FEMALE sign is always the last hint for a person. It also only ever appears on single
  // people. It can appear with skin tone.

  // MAN / WOMAN is always the first hint for a person. If there is a skin tone, it always follows
  // this code point directly. If one person in an emoji has a skin tone, then every person does.

  // SKIN TONE can appear first and only, first then MALE / FEMALE SIGN, or after MAN / WOMAN.

  /**
   * When this appears, it is the only person hint for this person.
   */
  public static final int BOY = 0x1F466;

  /**
   * When this appears, it is the only person hint for this person.
   */
  public static final int GIRL = 0x1F467;

  /**
   * When this appears, it is the only person hint for this person.
   */
  public static final int MAN = 0x1F468;

  /**
   * When this appears, it is the only person hint for this person.
   */
  public static final int WOMAN = 0x1F469;

  /**
   * This is never starts a person, but is a gender hint. It can be preceded by skin tone.
   */
  public static final int MALE_SIGN = 0x2642;

  /**
   * This is never starts a person, but is a gender hint. It can be preceded by skin tone.
   */
  public static final int FEMALE_SIGN = 0x2640;

  /**
   * This can start a person
   */
  public static final int SKIN_TONE_LIGHT = 0x1F3FB;

  public static final int SKIN_TONE_MEDIUM_LIGHT = 0x1F3FC;

  public static final int SKIN_TONE_MEDIUM = 0x1F3FD;

  public static final int SKIN_TONE_MEDIUM_DARK = 0x1F3FE;

  public static final int SKIN_TONE_DARK = 0x1F3FF;

  public static Optional<PersonHintBuilder> start(CodePoint cp) {
    PersonHintBuilder result;

    switch (cp.getValue()) {
      case BOY:
        result = new PersonHintBuilder().withAge(Ages.CHILD).withGender(Genders.MALE);
        break;
      case GIRL:
        result = new PersonHintBuilder().withAge(Ages.CHILD).withGender(Genders.FEMALE);
        break;
      case MAN:
        result = new PersonHintBuilder().withAge(Ages.ADULT).withGender(Genders.MALE);
        break;
      case WOMAN:
        result = new PersonHintBuilder().withAge(Ages.ADULT).withGender(Genders.FEMALE);
        break;
      case MALE_SIGN:
        result = new PersonHintBuilder().withGender(Genders.MALE);
        break;
      case FEMALE_SIGN:
        result = new PersonHintBuilder().withGender(Genders.FEMALE);
        break;
      case SKIN_TONE_LIGHT:
        result = new PersonHintBuilder().withSkinTone(SkinTones.LIGHT);
        break;
      case SKIN_TONE_MEDIUM_LIGHT:
        result = new PersonHintBuilder().withSkinTone(SkinTones.MEDIUM_LIGHT);
        break;
      case SKIN_TONE_MEDIUM:
        result = new PersonHintBuilder().withSkinTone(SkinTones.MEDIUM);
        break;
      case SKIN_TONE_MEDIUM_DARK:
        result = new PersonHintBuilder().withSkinTone(SkinTones.MEDIUM_DARK);
        break;
      case SKIN_TONE_DARK:
        result = new PersonHintBuilder().withSkinTone(SkinTones.DARK);
        break;
      default:
        result = null;
    }

    return Optional.ofNullable(result);
  }

  private String age;
  private String gender;
  private String skinTone;

  public PersonHintBuilder() {}

  /**
   * @return the age
   */
  public String getAge() {
    return age;
  }

  /**
   * @param age the age to set
   */
  public void setAge(String age) {
    this.age = age;
  }

  public PersonHintBuilder withAge(String age) {
    setAge(age);
    return this;
  }

  /**
   * @return the gender
   */
  public String getGender() {
    return gender;
  }

  /**
   * @param gender the gender to set
   */
  public void setGender(String gender) {
    this.gender = gender;
  }

  public PersonHintBuilder withGender(String gender) {
    setGender(gender);
    return this;
  }

  /**
   * @return the skinTone
   */
  public String getSkinTone() {
    return skinTone;
  }

  /**
   * @param skinTone the skinTone to set
   */
  public void setSkinTone(String skinTone) {
    this.skinTone = skinTone;
  }

  public PersonHintBuilder withSkinTone(String skinTone) {
    setSkinTone(skinTone);
    return this;
  }
}
