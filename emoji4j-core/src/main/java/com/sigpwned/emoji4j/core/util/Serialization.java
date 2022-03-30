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
package com.sigpwned.emoji4j.core.util;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.emoji4j.core.GraphemeData;
import com.sigpwned.emoji4j.core.data.GraphemeEntry;
import com.sigpwned.emoji4j.core.org.json.JSONArray;
import com.sigpwned.emoji4j.core.org.json.JSONObject;

public final class Serialization {
  private Serialization() {}

  public static GraphemeData deserializeGraphemeData(JSONObject o) {
    String unicodeVersion = o.getString("unicodeVersion");
    List<GraphemeEntry> graphemes = deserializeGraphemeEntries(o.getJSONArray("graphemes"));
    return GraphemeData.of(unicodeVersion, graphemes);
  }

  public static List<GraphemeEntry> deserializeGraphemeEntries(JSONArray xs) {
    List<GraphemeEntry> result = new ArrayList<>(xs.length());
    for (int i = 0; i < xs.length(); i++)
      result.add(deserializeGraphemeEntry(xs.getJSONObject(i)));
    return result;
  }

  public static GraphemeEntry deserializeGraphemeEntry(JSONObject o) {
    String name = o.getString("name");
    String type = o.getString("type");

    int[] canonicalCodePointSequence =
        deserializeCodePointSequence(o.getJSONArray("canonicalCodePointSequence"));

    int[][] alternativeCodePointSequences = o.has("alternativeCodePointSequences")
        ? deserializeCodePointSequences(o.getJSONArray("alternativeCodePointSequences"))
        : new int[0][];

    return GraphemeEntry.of(name, type, canonicalCodePointSequence, alternativeCodePointSequences);
  }

  public static int[][] deserializeCodePointSequences(JSONArray xs) {
    int length = xs.length();
    if (length == 0)
      return new int[0][];
    int[][] result = new int[length][];
    for (int i = 0; i < length; i++)
      result[i] = deserializeCodePointSequence(xs.getJSONArray(i));
    return result;
  }

  public static int[] deserializeCodePointSequence(JSONArray xs) {
    int length = xs.length();
    if (length == 0)
      throw new IllegalArgumentException("empty sequence");
    int[] result = new int[length];
    for (int i = 0; i < length; i++)
      result[i] = xs.getInt(i);
    return result;
  }
}
