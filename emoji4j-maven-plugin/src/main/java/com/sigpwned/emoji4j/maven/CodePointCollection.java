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

import java.util.stream.Stream;

/**
 * A model of multiple unicode code points
 */
public abstract class CodePointCollection implements Iterable<CodePoint> {
  public static CodePointCollection fromString(String s) {
    s = s.trim();
    if (s.contains(" "))
      return CodePointSequence.fromString(s);
    return CodePointRange.fromString(s);
  }

  public static enum Type {
    RANGE, SEQUENCE;
  }

  public abstract Type getType();

  public CodePointRange asRange() {
    return (CodePointRange) this;
  }

  public CodePointSequence asSequence() {
    return (CodePointSequence) this;
  }

  public abstract int size();

  public abstract Stream<CodePoint> stream();
}
