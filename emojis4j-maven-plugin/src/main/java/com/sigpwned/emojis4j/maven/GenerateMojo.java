package com.sigpwned.emojis4j.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.json.JSONWriter;
import com.sigpwned.emojis4j.maven.unicode.ModernUnicodeStandard;

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

  @Parameter(property = "emojis4j.unicodeVersion", defaultValue = "14.0")
  private String unicodeVersion;

  @Parameter(property = "emojis4j.target.directory", defaultValue = "target/generated-resources")
  private String outputDirectory;

  @Parameter(property = "emojis4j.target.package", defaultValue = "com.sigpwned.emojis4j")
  private String outputPackage;

  public static final int EMOJI_VARIATION_MARKER = 0xFE0F;

  public static final int TEXT_VARIATION_MARKER = 0xFE0E;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    UnicodeStandard unicode = new ModernUnicodeStandard(UnicodeVersion.fromString(unicodeVersion));
    try {
      File outputDirectory =
          new File(session.getCurrentProject().getBasedir(), this.outputDirectory);

      outputDirectory.mkdirs();

      session.getCurrentProject().addCompileSourceRoot(this.outputDirectory);

      GraphemeCollection emojiSequences = generateEmojiSequences(unicode);

      GraphemeCollection emojiZwjSequences = generateZwjEmojiSequences(unicode);

      GraphemeCollection extendedPictographicSequences = generateExtendedPictographics(unicode);

      GraphemeCollection test = generateEmojiTest(unicode, emojiSequences, emojiZwjSequences,
          extendedPictographicSequences);
      for (GraphemeMapping m : test)
        if (!m.getCodePoints().equals(m.getGrapheme().getCanonicalCodePointSequence()))
          m.getGrapheme().addAlternativeCodePointSequence(m.getCodePoints());

      GraphemeCollection variations =
          generateEmojiVariationSequences(unicode, emojiSequences, extendedPictographicSequences);
      for (GraphemeMapping m : variations)
        if (!m.getCodePoints().equals(m.getGrapheme().getCanonicalCodePointSequence()))
          m.getGrapheme().addAlternativeCodePointSequence(m.getCodePoints());

      List<GraphemeBuilder> graphemes = new ArrayList<>();
      emojiSequences.stream().map(GraphemeMapping::getGrapheme).forEach(graphemes::add);
      emojiZwjSequences.stream().map(GraphemeMapping::getGrapheme).forEach(graphemes::add);
      extendedPictographicSequences.stream().map(GraphemeMapping::getGrapheme)
          .forEach(graphemes::add);
      graphemes.sort(Comparator.comparing(GraphemeBuilder::getType)
          .thenComparing(GraphemeBuilder::getCanonicalCodePointSequence));

      try (FileWriter fw =
          new FileWriter(new File(outputDirectory, "emoji.json"), StandardCharsets.UTF_8)) {
        JSONWriter w = new JSONWriter(fw);
        w.object().key("graphemes").array();
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

      // GraphemeTrie trie = new GraphemeTrie();
      // for (GraphemeMapping m : emojiSequences)
      // trie.put(m.getCodePoints(), m.getGrapheme());
      // for (GraphemeMapping m : emojiZwjSequences)
      // trie.put(m.getCodePoints(), m.getGrapheme());
      // for (GraphemeMapping m : test)
      // trie.put(m.getCodePoints(), m.getGrapheme());

      // g.generateTrieFile("EmojiTrie", trie);

      // GraphemeCollection pictographics = generateExtendedPictographics(unicode);
      //
      // Files.writeString(new File(packageDirectory, PICTOGRAPHICS_JAVA).toPath(),
      // CodeGeneration.generateGraphemeFile(outputPackage, PICTOGRAPHICS, pictographics));
      //
      // GraphemeCollection variationSequences = generateEmojiVariationSequences(unicode);
      //
      // Files.writeString(new File(packageDirectory, VARIATION_SEQUENCES_JAVA).toPath(),
      // CodeGeneration.generateGraphemeFile(outputPackage, VARIATION_SEQUENCES,
      // variationSequences));
    } catch (IOException e) {
      throw new MojoFailureException("Failed to read unicode data", e);
    }
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

  private GraphemeCollection generateEmojiTest(UnicodeStandard unicode,
      GraphemeCollection emojiSequences, GraphemeCollection emojiZwjSequences,
      GraphemeCollection extendedPictographics) throws IOException {
    Map<CodePointSequence, GraphemeBuilder> unqualifieds = new HashMap<>();
    for (GraphemeMapping m : extendedPictographics)
      if (!unqualifieds.containsKey(m.getCodePoints().unqualified()))
        unqualifieds.put(m.getCodePoints().unqualified(), m.getGrapheme());
    for (GraphemeMapping m : emojiSequences)
      if (!unqualifieds.containsKey(m.getCodePoints().unqualified()))
        unqualifieds.put(m.getCodePoints().unqualified(), m.getGrapheme());
    for (GraphemeMapping m : emojiZwjSequences)
      if (!unqualifieds.containsKey(m.getCodePoints().unqualified()))
        unqualifieds.put(m.getCodePoints().unqualified(), m.getGrapheme());

    Map<CodePointSequence, GraphemeBuilder> test = new LinkedHashMap<>();
    unicode.processEmojiTest((cps, status) -> {
      CodePointSequence unqualified = cps.unqualified();
      GraphemeBuilder grapheme = Optional.ofNullable(unqualifieds.get(unqualified)).orElseThrow(
          () -> new AssertionError("Failed to resolve test code point sequence " + cps));
      if (grapheme.getType().equals(GraphemeBuilder.EMOJI))
        test.put(cps, grapheme);
      else {
        System.err.println("SKIP " + cps);
      }
    });

    return GraphemeCollection.of(test);
  }

  private GraphemeCollection generateExtendedPictographics(UnicodeStandard unicode)
      throws IOException {
    // These code points are emoji presentation by default
    Set<CodePoint> emojiPresentations = new HashSet<>();
    unicode.processEmojiData((cps, property) -> {
      if (property.equals("Emoji_Presentation"))
        for (CodePoint cp : cps)
          emojiPresentations.add(cp);
    });

    // Grab the code points that are pictographs and not emoji presentation by default
    Map<CodePointSequence, GraphemeBuilder> pictographicSequences = new LinkedHashMap<>();
    unicode.processEmojiData((cps, property) -> {
      if (property.equals("Extended_Pictographic"))
        for (CodePoint cp : cps) {
          if (!emojiPresentations.contains(cp))
            pictographicSequences.put(CodePointSequence.of(cp),
                new GraphemeBuilder().withCanonicalCodePointSequence(CodePointSequence.of(cp))
                    .withType(GraphemeBuilder.PICTOGRAPHIC));
        }
    });

    // Backfill the correct short names for everyone
    unicode.processUnicodeData((cp, shortName) -> {
      Optional.ofNullable(pictographicSequences.get(CodePointSequence.of(cp))).ifPresent(e -> {
        e.setShortName(shortName);
      });
    });

    // Remove any code points without names. They are reserved for future use, and have no use
    // today.
    Iterator<Map.Entry<CodePointSequence, GraphemeBuilder>> iterator =
        pictographicSequences.entrySet().iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getValue().getShortName() == null)
        iterator.remove();
    }

    return GraphemeCollection.of(pictographicSequences);
  }

  private GraphemeCollection generateEmojiVariationSequences(UnicodeStandard unicode,
      GraphemeCollection emojiSequences, GraphemeCollection extendedPictographics)
      throws IOException {
    Map<CodePointSequence, GraphemeBuilder> unqualifieds = new HashMap<>();
    for (GraphemeMapping m : emojiSequences)
      if (!m.getCodePoints().isQualified())
        unqualifieds.put(m.getCodePoints(), m.getGrapheme());
    for (GraphemeMapping m : extendedPictographics)
      if (!m.getCodePoints().isQualified())
        unqualifieds.put(m.getCodePoints(), m.getGrapheme());

    // Grab the code points that are pictographs and not emoji presentation by default
    Map<CodePointSequence, GraphemeBuilder> variationSequences = new LinkedHashMap<>();
    unicode.processEmojiVariationSequences((cps, style) -> {
      CodePointSequence unqualified = cps.unqualified();
      GraphemeBuilder grapheme = unqualifieds.get(unqualified);
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

        if (Objects.equals(grapheme.getType(), type)) {
          variationSequences.put(cps, grapheme);
        }
      }
    });

    return GraphemeCollection.of(variationSequences);
  }
}
