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

import java.util.Objects;

/**
 * An immutable mapping of one code point sequence to one grapheme
 */
public class GraphemeMapping {
  public static GraphemeMapping of(CodePointSequence codePoints, GraphemeBuilder grapheme) {
    return new GraphemeMapping(codePoints, grapheme);
  }

  private final CodePointSequence codePoints;
  private final GraphemeBuilder grapheme;

  public GraphemeMapping(CodePointSequence codePoints, GraphemeBuilder grapheme) {
    this.codePoints = codePoints;
    this.grapheme = grapheme;
  }

  /**
   * @return the codePoints
   */
  public CodePointSequence getCodePoints() {
    return codePoints;
  }

  /**
   * @return the grapheme
   */
  public GraphemeBuilder getGrapheme() {
    return grapheme;
  }

  @Override
  public int hashCode() {
    return Objects.hash(codePoints, grapheme);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GraphemeMapping other = (GraphemeMapping) obj;
    return Objects.equals(codePoints, other.codePoints) && Objects.equals(grapheme, other.grapheme);
  }

  @Override
  public String toString() {
    return "GraphemeMapping [codePoints=" + codePoints + ", grapheme=" + grapheme + "]";
  }
}
