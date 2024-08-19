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

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.json.JSONWriter;
import com.sigpwned.emoji4j.maven.unicode.CachingUnicodeStandardResolver;
import com.sigpwned.emoji4j.maven.unicode.DefaultUnicodeStandard;
import com.sigpwned.emoji4j.maven.unicode.ModernUnicodeStandardResolver;

/**
 * Generates the graphemes.json file used by emoji4j-core
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GenerateMojo extends AbstractMojo {
  // Current maven project
  @Parameter(defaultValue = "${project}", readonly = true)
  protected MavenProject project;

  // Current maven project
  @Parameter(property = "session")
  protected MavenSession session;

  // Current mojo execution
  @Parameter(property = "mojoExecution")
  protected MojoExecution execution;

  @Parameter(property = "emoji4j.unicodeVersion", defaultValue = "14.0")
  private String unicodeVersion;

  @Parameter(property = "emoji4j.target.directory", defaultValue = "target/generated-resources")
  private String outputDirectory;

  @Parameter(property = "emoji4j.cache.directory", defaultValue = "target/cache/emoji4j")
  private String cacheDirectory;

  @Parameter(property = "emoji4j.target.package", defaultValue = "com.sigpwned.emojis4j")
  private String outputPackage;

  public static final int EMOJI_VARIATION_MARKER = 0xFE0F;

  public static final int TEXT_VARIATION_MARKER = 0xFE0E;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      File outputDirectory =
          new File(session.getCurrentProject().getBasedir(), this.outputDirectory);

      outputDirectory.mkdirs();

      File cacheDirectory = new File(session.getCurrentProject().getBasedir(), this.cacheDirectory);

      cacheDirectory.mkdirs();

      Resource resourceDirectory = new Resource();
      resourceDirectory.setDirectory(outputDirectory.getAbsolutePath());

      UnicodeStandard unicode =
          new DefaultUnicodeStandard(new CachingUnicodeStandardResolver(cacheDirectory,
              new ModernUnicodeStandardResolver(UnicodeVersion.fromString(unicodeVersion))));

      session.getCurrentProject().addResource(resourceDirectory);

      GraphemeCollection legacy = generateLegacyEmoji(unicode);

      GraphemeCollection sequences = generateSequenceEmoji(unicode);

      List<GraphemeBuilder> graphemes =
          legacy.merge(sequences).stream().map(m -> m.getGrapheme()).collect(toList());

      // List<GraphemeBuilder> graphemes =
      // sequences.stream().map(m -> m.getGrapheme()).collect(toList());

      try (Writer fw =
          new OutputStreamWriter(new FileOutputStream(new File(outputDirectory, "graphemes.json")),
              StandardCharsets.UTF_8)) {
        JSONWriter w = new JSONWriter(fw);
        w.object().key("unicodeVersion").value(unicodeVersion).key("graphemes").array();
        for (GraphemeBuilder grapheme : graphemes) {
          w.object();
          w.key("type").value(grapheme.getType());
          w.key("name").value(grapheme.getShortName().toLowerCase());
          w.key("canonicalCodePointSequence").array();
          for (CodePoint cp : grapheme.getCanonicalCodePointSequence())
            w.value(cp.getValue());
          w.endArray();
          if (!grapheme.getAlternativeCodePointSequences().isEmpty()) {
            w.key("alternativeCodePointSequences").array();
            for (CodePointSequence cps : grapheme.getAlternativeCodePointSequences()) {
              w.array();
              for (CodePoint cp : cps)
                w.value(cp.getValue());
              w.endArray();
            }
            w.endArray();
          }
          w.endObject();
        }
        w.endArray().endObject();
      }
    } catch (IOException e) {
      throw new MojoFailureException("Failed to read unicode data", e);
    }
  }

  /**
   * Generates "legacy" emoji from within the relevant Unicode standard. These are single-code point
   * and variation sequence emoji.
   */
  private GraphemeCollection generateLegacyEmoji(UnicodeStandard unicode) throws IOException {
    Set<CodePointSequence> emojiPresentations = new HashSet<>();
    Set<CodePointSequence> emojiComponents = new HashSet<>();
    Set<CodePointSequence> pictographicPresentations = new HashSet<>();
    unicode.processEmojiData((cps, property) -> {
      switch (property) {
        case "Emoji_Presentation":
          for (CodePoint cp : cps)
            emojiPresentations.add(CodePointSequence.of(cp));
          break;
        case "Emoji_Component":
          for (CodePoint cp : cps)
            emojiComponents.add(CodePointSequence.of(cp));
          break;
        case "Extended_Pictographic":
          for (CodePoint cp : cps)
            pictographicPresentations.add(CodePointSequence.of(cp));
          break;
        default:
          // I don't care.
          break;
      }
    });

    Map<CodePointSequence, GraphemeBuilder> result = new LinkedHashMap<>();
    unicode.processEmojiData((cps, property) -> {
      switch (property) {
        case "Emoji_Presentation":
          for (CodePoint cp : cps)
            if (!emojiComponents.contains(CodePointSequence.of(cp)))
              result.put(CodePointSequence.of(cp),
                  new GraphemeBuilder().withType(GraphemeBuilder.EMOJI)
                      .withCanonicalCodePointSequence(CodePointSequence.of(cp)));
          break;
        case "Extended_Pictographic":
          for (CodePoint cp : cps)
            if (!emojiPresentations.contains(CodePointSequence.of(cp)))
              result.put(CodePointSequence.of(cp),
                  new GraphemeBuilder().withType(GraphemeBuilder.PICTOGRAPHIC)
                      .withCanonicalCodePointSequence(CodePointSequence.of(cp)));
          break;
        default:
          // I don't care.
          break;
      }
    });

    unicode.processEmojiVariationSequences((cps, style) -> {
      CodePointSequence unqualified = cps.unqualified();
      GraphemeBuilder grapheme = result.get(unqualified);
      if (grapheme != null) {
        String type;
        switch (cps.getLast().getValue()) {
          case EMOJI_VARIATION_MARKER:
            type = GraphemeBuilder.EMOJI;
            break;
          case TEXT_VARIATION_MARKER:
            type = GraphemeBuilder.PICTOGRAPHIC;
            break;
          default:
            System.err.println("Ignoring unrecognized variation marker " + cps.getLast());
            type = null;
            break;
        }

        if (type == null) {
          // I have no idea what this is.
        } else if (grapheme.getType().equals(type)) {
          grapheme.addAlternativeCodePointSequence(cps);
        } else {
          result.put(cps, new GraphemeBuilder().withType(type).withCanonicalCodePointSequence(cps));
        }
      }
    });

    // Fill in the names
    unicode.processUnicodeData((cp, name) -> {
      for (CodePointSequence cps : new CodePointSequence[] {CodePointSequence.of(cp),
          CodePointSequence.of(cp, CodePoint.of(0xFE0E)),
          CodePointSequence.of(cp, CodePoint.of(0xFE0F))}) {
        Optional.ofNullable(result.get(cps)).ifPresent(g -> {
          g.setShortName(name);
        });
      }
    });

    // There are some characters that emoji-data.txt contains and calls pictographs that are not
    // actually pictographs, e.g. 1F02C. Get rid of those.
    Iterator<GraphemeBuilder> iterator = result.values().iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getShortName() == null)
        iterator.remove();
    }

    return GraphemeCollection.of(result);
  }

  private GraphemeCollection generateSequenceEmoji(UnicodeStandard unicode) throws IOException {
    GraphemeCollection emojiSequences = generateEmojiSequences(unicode);

    GraphemeCollection emojiZwjSequences = generateZwjEmojiSequences(unicode);

    Map<CodePointSequence, GraphemeBuilder> unqualifieds = new HashMap<>();
    for (GraphemeMapping m : emojiSequences)
      if (!unqualifieds.containsKey(m.getCodePoints().unqualified()))
        unqualifieds.put(m.getCodePoints().unqualified(), m.getGrapheme());
    for (GraphemeMapping m : emojiZwjSequences)
      if (!unqualifieds.containsKey(m.getCodePoints().unqualified()))
        unqualifieds.put(m.getCodePoints().unqualified(), m.getGrapheme());

    Map<CodePointSequence, GraphemeBuilder> result = new LinkedHashMap<>();
    for (GraphemeMapping m : emojiSequences)
      result.put(m.getCodePoints(), m.getGrapheme());
    for (GraphemeMapping m : emojiZwjSequences)
      result.put(m.getCodePoints(), m.getGrapheme());

    unicode.processEmojiTest((cps, status) -> {
      CodePointSequence unqualified = cps.unqualified();
      GraphemeBuilder grapheme = unqualifieds.get(unqualified);
      if (status.equals("unqualified") && cps.size() == 1) {
        if (grapheme == null) {
          // This is a pictographic. Add it.
          result.put(cps, new GraphemeBuilder().withType(GraphemeBuilder.PICTOGRAPHIC)
              .withCanonicalCodePointSequence(cps));
        } else {
          if (!grapheme.getType().equals(GraphemeBuilder.EMOJI))
            throw new AssertionError(format("grapheme %s has type %s", cps, grapheme.getType()));
          if (!grapheme.getAllCodePointSequences().contains(cps)
              && !grapheme.getAllCodePointSequences().contains(cps.plus(CodePoint.of(0xFE0F))))
            throw new AssertionError(
                format("grapheme %s does not account for code point %s", grapheme, cps));
        }
      } else if (!grapheme.getAllCodePointSequences().contains(cps)) {
        grapheme.addAlternativeCodePointSequence(cps);
      }
    });

    unicode.processUnicodeData((cp, name) -> {
      for (CodePointSequence cps : new CodePointSequence[] {CodePointSequence.of(cp),
          CodePointSequence.of(cp, CodePoint.of(0xFE0E)),
          CodePointSequence.of(cp, CodePoint.of(0xFE0F))}) {
        Optional.ofNullable(result.get(cps)).ifPresent(g -> {
          g.setShortName(name);
        });
      }
    });

    return GraphemeCollection.of(result);
  }

  private GraphemeCollection generateEmojiSequences(UnicodeStandard unicode) throws IOException {
    Map<CodePointSequence, GraphemeBuilder> emojiSequences = new LinkedHashMap<>();
    unicode.processEmojiSequences((cps, typeField, shortName) -> {
      switch (cps.getType()) {
        case RANGE:
          // These are single-code point sequences
          for (CodePoint cp : cps.asRange())
            emojiSequences.put(CodePointSequence.of(cp),
                new GraphemeBuilder().withCanonicalCodePointSequence(CodePointSequence.of(cp))
                    .withType(GraphemeBuilder.EMOJI).withShortName(shortName));
          break;
        case SEQUENCE:
          emojiSequences.put(cps.asSequence(),
              new GraphemeBuilder().withCanonicalCodePointSequence(cps.asSequence())
                  .withType(GraphemeBuilder.EMOJI).withShortName(shortName));
          break;
        default:
          System.err.println("Ignoring unrecognized code point collection type " + cps.getType());
          break;
      }
    });

    unicode.processUnicodeData((cp, shortName) -> {
      Optional.ofNullable(emojiSequences.get(CodePointSequence.of(cp))).ifPresent(e -> {
        e.setShortName(shortName);
      });
    });

    return GraphemeCollection.of(emojiSequences);
  }

  private GraphemeCollection generateZwjEmojiSequences(UnicodeStandard unicode) throws IOException {
    Map<CodePointSequence, GraphemeBuilder> emojiZwjSequences = new LinkedHashMap<>();
    unicode.processEmojiZwjSequences((cps, typeField, shortName) -> {
      switch (cps.getType()) {
        case RANGE:
          // These are single-code point sequences
          for (CodePoint cp : cps.asRange())
            emojiZwjSequences.put(CodePointSequence.of(cp),
                new GraphemeBuilder().withCanonicalCodePointSequence(CodePointSequence.of(cp))
                    .withType(GraphemeBuilder.EMOJI).withShortName(shortName));
          break;
        case SEQUENCE:
          emojiZwjSequences.put(cps.asSequence(),
              new GraphemeBuilder().withCanonicalCodePointSequence(cps.asSequence())
                  .withType(GraphemeBuilder.EMOJI).withShortName(shortName));
          break;
        default:
          System.err.println("Ignoring unrecognized code point collection type " + cps.getType());
          break;
      }
    });

    unicode.processUnicodeData((cp, shortName) -> {
      Optional.ofNullable(emojiZwjSequences.get(CodePointSequence.of(cp))).ifPresent(e -> {
        e.setShortName(shortName);
      });
    });

    return GraphemeCollection.of(emojiZwjSequences);
  }
}
