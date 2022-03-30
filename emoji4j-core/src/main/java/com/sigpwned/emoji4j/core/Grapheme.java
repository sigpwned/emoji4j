/*-
 * =================================LICENSE_START==================================
 * emoji4j-core
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
package com.sigpwned.emoji4j.core;

import java.util.Arrays;
import java.util.Objects;
import com.sigpwned.emoji4j.core.annotation.Generated;

/**
 * A model for a single, visually-distinct image character.
 */
public class Grapheme {
  public static enum Type {
    EMOJI, PICTOGRAPHIC;
  }

  private final Type type;
  private final int[] coordinates;
  private final String name;

  public Grapheme(Type type, int[] coordinates, String name) {
    this.type = type;
    this.coordinates = coordinates;
    this.name = name;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the coordinates
   */
  public int[] getCoordinates() {
    return coordinates;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(coordinates);
    result = prime * result + Objects.hash(name, type);
    return result;
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Grapheme other = (Grapheme) obj;
    return Arrays.equals(coordinates, other.coordinates) && Objects.equals(name, other.name)
        && type == other.type;
  }

  @Override
  @Generated
  public String toString() {
    return new String(coordinates, 0, coordinates.length);
  }
}
