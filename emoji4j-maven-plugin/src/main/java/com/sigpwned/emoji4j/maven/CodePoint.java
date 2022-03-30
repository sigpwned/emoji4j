/*-
 * =================================LICENSE_START==================================
 * emoji4j-maven-plugin
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.emoji4j.maven;

/**
 * A model of a single unicode code point
 */
public class CodePoint implements Comparable<CodePoint> {
  public static CodePoint fromString(String s) {
    return of(Integer.parseInt(s, 16));
  }

  public static CodePoint of(int value) {
    return new CodePoint(value);
  }

  private final int value;

  public CodePoint(int value) {
    this.value = value;
  }

  /**
   * @return the value
   */
  public int getValue() {
    return value;
  }

  public CodePoint next() {
    return of(getValue() + 1);
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CodePoint other = (CodePoint) obj;
    return value == other.value;
  }

  @Override
  public String toString() {
    return Integer.toHexString(getValue()).toUpperCase();
  }

  @Override
  public int compareTo(CodePoint o) {
    return getValue() - o.getValue();
  }
}
