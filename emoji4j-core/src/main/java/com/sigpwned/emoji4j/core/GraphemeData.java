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

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Objects;
import com.sigpwned.emoji4j.core.annotation.Generated;
import com.sigpwned.emoji4j.core.data.GraphemeEntry;

public class GraphemeData {
  public static GraphemeData of(String unicodeVersion, List<GraphemeEntry> graphemes) {
    return new GraphemeData(unicodeVersion, graphemes);
  }

  private final String unicodeVersion;
  private final List<GraphemeEntry> graphemes;

  public GraphemeData(String unicodeVersion, List<GraphemeEntry> graphemes) {
    this.unicodeVersion = unicodeVersion;
    this.graphemes = unmodifiableList(graphemes);
  }

  /**
   * @return the unicodeVersion
   */
  public String getUnicodeVersion() {
    return unicodeVersion;
  }

  /**
   * @return the graphemes
   */
  public List<GraphemeEntry> getGraphemes() {
    return graphemes;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(graphemes, unicodeVersion);
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
    GraphemeData other = (GraphemeData) obj;
    return Objects.equals(graphemes, other.graphemes)
        && Objects.equals(unicodeVersion, other.unicodeVersion);
  }

  @Override
  @Generated
  public String toString() {
    return "GraphemeData [unicodeVersion=" + unicodeVersion + ", graphemes=" + graphemes + "]";
  }
}
