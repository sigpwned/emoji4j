package com.sigpwned.emojis4j.maven;

import java.util.Objects;

/**
 * A Unicode model semver
 */
public class UnicodeVersion implements Comparable<UnicodeVersion> {
  public static UnicodeVersion fromString(String s) {
    if (s == null)
      throw new NullPointerException();
    if (s.isEmpty())
      throw new IllegalArgumentException("empty");

    String[] parts = s.split("\\.", 3);

    int major = parts.length >= 1 ? Integer.parseInt(parts[0]) : 0;
    int minor = parts.length >= 2 ? Integer.parseInt(parts[1]) : 0;
    int patch = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;

    return new UnicodeVersion(major, minor, patch);
  }

  public static UnicodeVersion of(int major, int minor, int patch) {
    return new UnicodeVersion(major, minor, patch);
  }

  private final int major;
  private final int minor;
  private final int patch;

  public UnicodeVersion(int major, int minor, int patch) {
    if (major < 0)
      throw new IllegalArgumentException("major must be non-negative");
    if (minor < 0)
      throw new IllegalArgumentException("minor must be non-negative");
    if (patch < 0)
      throw new IllegalArgumentException("patch must be non-negative");
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  /**
   * @return the major
   */
  public int getMajor() {
    return major;
  }

  /**
   * @return the minor
   */
  public int getMinor() {
    return minor;
  }

  /**
   * @return the patch
   */
  public int getPatch() {
    return patch;
  }

  @Override
  public int hashCode() {
    return Objects.hash(major, minor, patch);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UnicodeVersion other = (UnicodeVersion) obj;
    return major == other.major && minor == other.minor && patch == other.patch;
  }

  public String toMajorString() {
    return Integer.toString(getMajor());
  }

  public String toMajorMinorString() {
    return toMajorString() + "." + getMinor();
  }

  public String toMajorMinorPatchString() {
    return toMajorMinorString() + "." + getPatch();
  }

  @Override
  public String toString() {
    return toMajorMinorPatchString();
  }

  @Override
  public int compareTo(UnicodeVersion o) {
    int result = getMajor() - o.getMajor();
    if (result == 0)
      result = getMinor() - o.getMinor();
    if (result == 0)
      result = getPatch() - o.getPatch();
    return result;
  }
}
