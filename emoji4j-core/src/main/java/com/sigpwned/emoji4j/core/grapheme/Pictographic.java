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
package com.sigpwned.emoji4j.core.grapheme;

import com.sigpwned.emoji4j.core.Grapheme;

/**
 * A formal pictograph grapheme
 */
public class Pictographic extends Grapheme {
  public static Pictographic of(int[] coordinates, String name) {
    return new Pictographic(coordinates, name);
  }

  public Pictographic(int[] coordinates, String name) {
    super(Type.PICTOGRAPHIC, coordinates, name);
  }
}
