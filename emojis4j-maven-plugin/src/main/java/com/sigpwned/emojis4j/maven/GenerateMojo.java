package com.sigpwned.emojis4j.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
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
import com.sigpwned.emojis4j.maven.unicode.ModernUnicodeStandard;
import com.sigpwned.emojis4j.maven.util.CodeGeneration;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
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

  @Parameter(property = "emojis4j.target.directory", defaultValue = "target/generated-sources")
  private String outputDirectory;

  @Parameter(property = "emojis4j.target.package", defaultValue = "com.sigpwned.emojis4j")
  private String outputPackage;

  /**
   * The final output destination for code generation
   */
  private File packageDirectory;

  public static final String CODE_POINTS = "CodePoints";

  public static final String EMOJI_SEQUENCES = "EmojiSequences";

  public static final String EMOJI_SEQUENCES_CODE_POINTS = "EmojiSequences" + CODE_POINTS;

  public static final String EMOJI_SEQUENCES_JAVA = EMOJI_SEQUENCES + ".java";

  public static final String EMOJI_SEQUENCES_CODE_POINTS_JAVA =
      EMOJI_SEQUENCES_CODE_POINTS + ".java";

  public static final String EMOJI_ZWJ_SEQUENCES = "EmojiZwjSequences";

  public static final String EMOJI_ZWJ_SEQUENCES_CODE_POINTS = "EmojiZwjSequences" + CODE_POINTS;

  public static final String EMOJI_ZWJ_SEQUENCES_JAVA = EMOJI_ZWJ_SEQUENCES + ".java";

  public static final String EMOJI_ZWJ_SEQUENCES_CODE_POINTS_JAVA =
      EMOJI_ZWJ_SEQUENCES_CODE_POINTS + ".java";

  public static final String PICTOGRAPHICS = "Pictographics";

  public static final String PICTOGRAPHICS_JAVA = PICTOGRAPHICS + ".java";

  public static final String VARIATION_SEQUENCES = "VariationSequences";

  public static final String VARIATION_SEQUENCES_JAVA = VARIATION_SEQUENCES + ".java";

  public static final String EMOJI_TRIE = "EmojiTrie";

  public static final String EMOJI_TRIE_JAVA = EMOJI_TRIE + ".java";

  public static final int EMOJI_VARIATION_MARKER = 0xFE0F;

  public static final int TEXT_VARIATION_MARKER = 0xFE0E;

  public static final CodePoint ZWJ = CodePoint.of(0x200D);

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    UnicodeStandard unicode = new ModernUnicodeStandard(UnicodeVersion.fromString(unicodeVersion));
    try {
      File outputDirectory =
          new File(session.getCurrentProject().getBasedir(), this.outputDirectory);

      session.getCurrentProject().addCompileSourceRoot(this.outputDirectory);

      packageDirectory = outputDirectory;
      for (String packageName : outputPackage.split("\\.")) {
        packageDirectory = new File(packageDirectory, packageName);
      }

      packageDirectory.mkdirs();

      GraphemeCollection emojiSequences = generateEmojiSequences(unicode);

      Files.writeString(new File(packageDirectory, EMOJI_SEQUENCES_JAVA).toPath(),
          CodeGeneration.generateGraphemeFile(outputPackage, EMOJI_SEQUENCES, emojiSequences));

      GraphemeCollection emojiZwjSequences = generateZwjEmojiSequences(unicode);

      Files.writeString(new File(packageDirectory, EMOJI_ZWJ_SEQUENCES_JAVA).toPath(),
          CodeGeneration.generateGraphemeFile(outputPackage, EMOJI_ZWJ_SEQUENCES,
              emojiZwjSequences));

      GraphemeCollection test = generateEmojiTest(unicode, emojiSequences, emojiZwjSequences);

      GraphemeTrie trie = new GraphemeTrie();
      for (GraphemeMapping m : emojiSequences)
        trie.put(m.getCodePoints(), m.getGrapheme());
      for (GraphemeMapping m : emojiZwjSequences)
        trie.put(m.getCodePoints(), m.getGrapheme());
      for (GraphemeMapping m : test)
        trie.put(m.getCodePoints(), m.getGrapheme());

      Files.writeString(new File(packageDirectory, EMOJI_TRIE_JAVA).toPath(),
          CodeGeneration.generateTrieFile(outputPackage, EMOJI_TRIE, trie, gm -> {
            if (gm.getCodePoints().contains(ZWJ)) {
              return EMOJI_ZWJ_SEQUENCES + "." + CodeGeneration.variableName(gm);
            } else {
              return EMOJI_SEQUENCES + "." + CodeGeneration.variableName(gm);
            }
          }));

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
                new GraphemeBuilder().withType(GraphemeBuilder.EMOJI).withShortName(shortName));
          break;
        case SEQUENCE:
          emojiSequences.put(cps.asSequence(),
              new GraphemeBuilder().withType(GraphemeBuilder.EMOJI).withShortName(shortName));
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
                new GraphemeBuilder().withType(GraphemeBuilder.EMOJI).withShortName(shortName));
          break;
        case SEQUENCE:
          emojiZwjSequences.put(cps.asSequence(),
              new GraphemeBuilder().withType(GraphemeBuilder.EMOJI).withShortName(shortName));
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
      GraphemeCollection emojiSequences, GraphemeCollection emojiZwjSequences) throws IOException {
    Map<CodePointSequence, GraphemeBuilder> unqualifieds = new HashMap<>();
    for (GraphemeMapping m : emojiSequences)
      unqualifieds.put(m.getCodePoints().unqualified(), m.getGrapheme());
    for (GraphemeMapping m : emojiZwjSequences)
      unqualifieds.put(m.getCodePoints().unqualified(), m.getGrapheme());

    Map<CodePointSequence, GraphemeBuilder> test = new LinkedHashMap<>();
    unicode.processEmojiTest((cps, status) -> {
      CodePointSequence unqualified = cps.unqualified();
      GraphemeBuilder grapheme = Optional.ofNullable(unqualifieds.get(unqualified)).orElseThrow(
          () -> new AssertionError("Failed to resolve test code point sequence " + cps));
      test.put(cps, grapheme);
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
                new GraphemeBuilder().withType(GraphemeBuilder.PICTOGRAPHIC));
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

  private GraphemeCollection generateEmojiVariationSequences(UnicodeStandard unicode)
      throws IOException {
    Set<CodePointSequence> emojiPresentations = new HashSet<>();
    unicode.processEmojiSequences((cps, typeField, shortName) -> {
      switch (cps.getType()) {
        case RANGE:
          // These are never a sequence, since they're one code point long
          break;
        case SEQUENCE:
          if (cps.asSequence().size() == 2
              && cps.asSequence().getLast().getValue() == EMOJI_VARIATION_MARKER)
            emojiPresentations.add(cps.asSequence());
          break;
        default:
          System.err.println("Ignoring unrecognized code point collection type " + cps.getType());
          break;
      }
    });

    // Grab the code points that are pictographs and not emoji presentation by default
    Map<CodePointSequence, GraphemeBuilder> variationSequences = new LinkedHashMap<>();
    unicode.processEmojiVariationSequences((cps, style) -> {
      if (!emojiPresentations.contains(cps)) {
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
        if (type != null)
          variationSequences.put(cps, new GraphemeBuilder().withType(type));
      }
    });

    // Backfill the correct short names for everyone
    unicode.processUnicodeData((cp, shortName) -> {
      Optional.ofNullable(
          variationSequences.get(CodePointSequence.of(cp, CodePoint.of(EMOJI_VARIATION_MARKER))))
          .ifPresent(e -> {
            e.setShortName(shortName);
          });
      Optional
          .ofNullable(
              variationSequences.get(CodePointSequence.of(cp, CodePoint.of(TEXT_VARIATION_MARKER))))
          .ifPresent(e -> {
            e.setShortName(shortName);
          });
    });

    return GraphemeCollection.of(variationSequences);
  }
}
