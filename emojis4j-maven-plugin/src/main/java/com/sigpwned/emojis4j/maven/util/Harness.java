package com.sigpwned.emojis4j.maven.util;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import com.sigpwned.emojis4j.maven.CodePoint;
import com.sigpwned.emojis4j.maven.CodePointSequence;
import com.sigpwned.emojis4j.maven.PersonHintBuilder;
import com.sigpwned.emojis4j.maven.UnicodeStandard;
import com.sigpwned.emojis4j.maven.UnicodeVersion;
import com.sigpwned.emojis4j.maven.unicode.ModernUnicodeStandard;

public class Harness {
  private static final String UNICODE_BASE_URL = "https://unicode.org/Public";

  public static final String unicodeVersion = "14.0";

  public static class EmojiSequence {
    private String status;
    private String shortName;
    private String typeField;
    private boolean emojiFamily;
    private boolean extendedPictographFamily;
    private boolean explicitTextVariation;
    private boolean explicitEmojiVariation;
    private boolean implicitEmojiVariation;

    public EmojiSequence() {}

    /**
     * @return the status
     */
    public String getStatus() {
      return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
      this.status = status;
    }

    public EmojiSequence withStatus(String status) {
      setStatus(status);
      return this;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
      return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
      this.shortName = shortName;
    }

    public EmojiSequence withShortName(String shortName) {
      setShortName(shortName);
      return this;
    }

    /**
     * @return the typeField
     */
    public String getTypeField() {
      return typeField;
    }

    /**
     * @param typeField the typeField to set
     */
    public void setTypeField(String typeField) {
      this.typeField = typeField;
    }

    public EmojiSequence withTypeField(String typeField) {
      setTypeField(typeField);
      return this;
    }

    /**
     * @return the emojiFamily
     */
    public boolean isEmojiFamily() {
      return emojiFamily;
    }

    /**
     * @param emojiFamily the emojiFamily to set
     */
    public void setEmojiFamily(boolean emojiFamily) {
      this.emojiFamily = emojiFamily;
    }

    public EmojiSequence withEmojiFamily(boolean emojiFamily) {
      setEmojiFamily(emojiFamily);
      return this;
    }

    /**
     * @return the extendedPictographFamily
     */
    public boolean isExtendedPictographFamily() {
      return extendedPictographFamily;
    }

    /**
     * @param extendedPictographFamily the extendedPictographFamily to set
     */
    public void setExtendedPictographFamily(boolean extendedPictographFamily) {
      this.extendedPictographFamily = extendedPictographFamily;
    }

    public EmojiSequence withExtendedPictographFamily(boolean extendedPictographFamily) {
      setExtendedPictographFamily(extendedPictographFamily);
      return this;
    }

    /**
     * @return the explicitTextVariation
     */
    public boolean isExplicitTextVariation() {
      return explicitTextVariation;
    }

    /**
     * @param explicitTextVariation the explicitTextVariation to set
     */
    public void setExplicitTextVariation(boolean explicitTextVariation) {
      this.explicitTextVariation = explicitTextVariation;
    }

    public EmojiSequence withExplicitTextVariation(boolean explicitTextVariation) {
      setExplicitTextVariation(explicitTextVariation);
      return this;
    }

    /**
     * @return the explicitEmojiVariation
     */
    public boolean isExplicitEmojiVariation() {
      return explicitEmojiVariation;
    }

    /**
     * @param explicitEmojiVariation the explicitEmojiVariation to set
     */
    public void setExplicitEmojiVariation(boolean explicitEmojiVariation) {
      this.explicitEmojiVariation = explicitEmojiVariation;
    }

    public EmojiSequence withExplicitEmojiVariation(boolean explicitEmojiVariation) {
      setExplicitEmojiVariation(explicitEmojiVariation);
      return this;
    }

    /**
     * @return the implicitEmojiVariation
     */
    public boolean isImplicitEmojiVariation() {
      return implicitEmojiVariation;
    }

    /**
     * @param implicitEmojiVariation the implicitEmojiVariation to set
     */
    public void setImplicitEmojiVariation(boolean implicitEmojiVariation) {
      this.implicitEmojiVariation = implicitEmojiVariation;
    }

    public EmojiSequence withImplicitEmojiVariation(boolean implicitEmojiVariation) {
      setImplicitEmojiVariation(implicitEmojiVariation);
      return this;
    }

    @Override
    public int hashCode() {
      return Objects.hash(status, typeField);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      EmojiSequence other = (EmojiSequence) obj;
      return Objects.equals(status, other.status) && Objects.equals(typeField, other.typeField);
    }
  }

  @FunctionalInterface
  public static interface EmojiSequenceTreeWalkHandler {
    public void visit(CodePointSequence codePoints, EmojiSequenceNode node);
  }

  public static class EmojiSequenceNode {
    private final Map<CodePoint, EmojiSequenceNode> children;
    private EmojiSequence sequence;

    public EmojiSequenceNode() {
      this.children = new HashMap<>();
    }

    private Optional<EmojiSequenceNode> get(CodePoint codePoint) {
      return Optional.ofNullable(children.get(codePoint));
    }

    private EmojiSequenceNode getOrCreate(CodePoint codePoint) {
      return get(codePoint).orElseGet(() -> {
        EmojiSequenceNode result = new EmojiSequenceNode();
        children.put(codePoint, result);
        return result;
      });
    }

    private Optional<EmojiSequenceNode> resolve(CodePointSequence cps, boolean vivify) {
      EmojiSequenceNode ni = this;
      for (CodePoint cp : cps) {
        if (vivify)
          ni = ni.getOrCreate(cp);
        else
          ni = ni.get(cp).orElse(null);
        if (ni == null)
          return Optional.empty();
      }
      return Optional.of(ni);
    }

    public Optional<EmojiSequence> get(CodePointSequence cps) {
      return resolve(cps, false).flatMap(EmojiSequenceNode::getSequence);
    }

    public EmojiSequence getOrCreate(CodePointSequence cps, Supplier<EmojiSequence> supplier) {
      EmojiSequenceNode node = resolve(cps, true).get();
      if (node.getSequence().isEmpty())
        node.setSequence(supplier.get());
      return node.getSequence().get();
    }

    public void put(CodePointSequence cps, EmojiSequence sequence) {
      resolve(cps, true).get().setSequence(sequence);
    }

    public Set<CodePoint> listChildren() {
      return unmodifiableSet(children.keySet());
    }

    private Optional<EmojiSequence> getSequence() {
      return Optional.ofNullable(sequence);
    }

    private void setSequence(EmojiSequence sequence) {
      if (this.sequence != null)
        throw new IllegalStateException("sequence already set");
      this.sequence = sequence;
    }

    public int size() {
      return children.values().stream().mapToInt(EmojiSequenceNode::size).sum()
          + (getSequence().isPresent() ? 1 : 0);
    }

    public void walk(EmojiSequenceTreeWalkHandler handler) {
      for (Map.Entry<CodePoint, EmojiSequenceNode> entry : children.entrySet()) {
        entry.getValue().walk2(CodePointSequence.of(entry.getKey()), handler);
      }
    }

    private void walk2(CodePointSequence codePoints, EmojiSequenceTreeWalkHandler handler) {
      handler.visit(codePoints, this);
      for (Map.Entry<CodePoint, EmojiSequenceNode> entry : children.entrySet()) {
        entry.getValue().walk2(codePoints.plus(entry.getKey()), handler);
      }
    }
  }

  public static final CodePoint TEXT_VARIATION_TAG = CodePoint.of(0xFE0E);

  public static final CodePoint EMOJI_VARIATION_TAG = CodePoint.of(0xFE0F);


  public static void main(String[] args) throws Exception {
    EmojiSequenceNode root = new EmojiSequenceNode();

    String packageName = "com.sigpwned.emoji4j.core.util";

    UnicodeVersion version = UnicodeVersion.fromString("14.0");

    UnicodeStandard unicode = new ModernUnicodeStandard(version);

    Map<CodePointSequence, EmojiSequence> sequences = new LinkedHashMap<>();

    unicode.processEmojiSequences((cps, typeField, shortName) -> {
      switch (cps.getType()) {
        case RANGE:
          // These are single-code point sequences
          for (CodePoint cp : cps.asRange())
            sequences.put(CodePointSequence.of(cp),
                new EmojiSequence().withTypeField(typeField).withShortName(shortName));
          // root.getOrCreate(CodePointSequence.of(cp), EmojiSequence::new).withTypeField(typeField)
          // .withEmojiFamily(true).withShortName(shortName);
          break;
        case SEQUENCE:
          sequences.put(cps.asSequence(),
              new EmojiSequence().withTypeField(typeField).withShortName(shortName));
          // root.getOrCreate(cps.asSequence(), EmojiSequence::new).withTypeField(typeField)
          // .withEmojiFamily(true).withShortName(shortName);
          break;
        default:
          System.err.println("Ignoring unrecognized code point collection type " + cps.getType());
          break;
      }
    });

    // unicode.processEmojiZwjSequences((cps, typeField, shortName) -> {
    // root.getOrCreate(cps, EmojiSequence::new).withTypeField(typeField).withEmojiFamily(true)
    // .withShortName(shortName);
    //
    // });

    unicode.processUnicodeData((cp, shortName) -> {
      root.get(CodePointSequence.of(cp)).ifPresent(n -> {
        n.setShortName(shortName);
      });
    });

    System.setOut(new PrintStream(new FileOutputStream("/Users/aboothe/EmojiSequences.java")));

    System.out.printf("package %s;\n", packageName);
    System.out.printf("public final class EmojiSequences {\n");
    System.out.printf("    private EmojiSequences() {}\n");
    System.out.printf("\n");
    for (Map.Entry<CodePointSequence, EmojiSequence> e : sequences.entrySet()) {
      System.out.printf("    public static final Emoji %s = Emoji.of(%s, \"%s\", %s);\n",
          Harness.shortNameToVariableName(e.getValue().getShortName()),
          codePointSequenceToIntArray(e.getKey()), e.getValue().getShortName(),
          codePointSequenceToPeople(e.getKey(), e.getValue().getShortName()));
      System.out.printf("\n");
    }
    System.out.printf("}");

    // // 144697
    // // System.out
    // // .println(derivedNames.getEntries().stream().mapToInt(e ->
    // e.getCodePoints().size()).sum());
    //
    // // System.out.println(emojis.size());
    // // System.out.println(emojiData.getEntries().stream().flatMap(e ->
    // e.getCodePoints().stream())
    // // .mapToInt(cp -> cp.getValue()).distinct().count());
    //
    // System.out.println(root.size());
    //
    // Set<CodePointSequence> before = new HashSet<>();
    // root.walk((cps, n) -> {
    // n.getSequence().ifPresent(s -> {
    // if (!s.isExplicitTextVariation() && s.isEmojiFamily() && !s.isExtendedPictographFamily()) {
    // before.add(cps);
    // }
    // });
    // });
    //
    // if (true) {
    // EmojiData emojiData;
    // try (InputStream in =
    // new URL(UNICODE_BASE_URL + "/" + unicodeVersion + ".0/ucd/emoji/emoji-data.txt")
    // .openStream()) {
    // emojiData = new EmojiDataLoader().load(in);
    // }
    //
    // int pictographs = 0;
    // for (EmojiData.Entry entry : emojiData.getEntries()) {
    // switch (entry.getProperty()) {
    // case EmojiData.EMOJI:
    // // for (CodePoint cp : entry.getCodePoints())
    // // root.getOrCreate(CodePointSequence.of(cp), EmojiSequence::new).withEmojiFamily(true);
    // break;
    // case EmojiData.EMOJI_COMPONENT:
    // case EmojiData.EMOJI_MODIFIER:
    // // These explicitly are not emojis
    // break;
    // case EmojiData.EMOJI_MODIFIER_BASE:
    // // Don't care
    // break;
    // case EmojiData.EMOJI_PRESENTATION:
    // for (CodePoint cp : entry.getCodePoints())
    // root.getOrCreate(CodePointSequence.of(cp), EmojiSequence::new)
    // .withImplicitEmojiVariation(true);
    // break;
    // case EmojiData.EXTENDED_PICTOGRAPHIC:
    // for (CodePoint cp : entry.getCodePoints())
    // root.getOrCreate(CodePointSequence.of(cp), EmojiSequence::new)
    // .withExtendedPictographFamily(true);
    // pictographs = pictographs + entry.getCodePoints().size();
    // break;
    // default:
    // // I have no idea what this is.
    // System.err.println("Ignoring unrecognized property " + entry.getProperty());
    // break;
    // }
    // }
    // }
    //
    // DerivedNames derivedNames;
    // try (InputStream in =
    // new URL(UNICODE_BASE_URL + "/" + unicodeVersion + ".0/ucd/extracted/DerivedName.txt")
    // .openStream()) {
    // derivedNames = new DerivedNamesLoader().load(in);
    // }
    //
    // for (DerivedNames.Entry entry : derivedNames.getEntries()) {
    // for (CodePoint cp : entry.getCodePoints()) {
    // CodePointSequence cps = CodePointSequence.of(cp);
    // root.get(cps).ifPresent(es -> es.withShortName(entry.getName()));
    // root.get(cps.plus(CodePointSequence.of(TEXT_VARIATION_TAG)))
    // .ifPresent(es -> es.withShortName(entry.getName()));
    // root.get(cps.plus(CodePointSequence.of(EMOJI_VARIATION_TAG)))
    // .ifPresent(es -> es.withShortName(entry.getName()));
    // }
    // }
    //
    // if (true) {
    // EmojiVariations emojiVariations;
    // try (InputStream in = new URL(
    // UNICODE_BASE_URL + "/" + unicodeVersion + ".0/ucd/emoji/emoji-variation-sequences.txt")
    // .openStream()) {
    // emojiVariations = new EmojiVariationsLoader().load(in);
    // }
    //
    // for (EmojiVariations.Entry entry : emojiVariations.getEntries()) {
    // EmojiSequence prefix = root
    // .get(entry.getCodePointSequence().getPrefix(entry.getCodePointSequence().size() - 1))
    // .orElse(null);
    //
    // EmojiSequence variation = null;
    // switch (entry.getVariation()) {
    // case EmojiVariations.EMOJI_STYLE:
    // variation = root.getOrCreate(entry.getCodePointSequence(), EmojiSequence::new)
    // .withEmojiFamily(true).withExplicitEmojiVariation(true);
    // break;
    // case EmojiVariations.TEXT_STYLE:
    // // We don't care about these. These are explicitly text, not emoji.
    // variation = root.getOrCreate(entry.getCodePointSequence(), EmojiSequence::new)
    // .withExplicitTextVariation(true);
    // break;
    // default:
    // // I have no idea what this is.
    // System.err.println("Ignoring unrecognized variation " + entry.getVariation());
    // break;
    // }
    //
    // if (prefix != null)
    // if (variation != null)
    // variation.setShortName(prefix.getShortName());
    // }
    // }
    //
    // root.walk((cps, n) -> {
    // n.getSequence().ifPresent(s -> {
    // if (cps.getLast().equals(EMOJI_VARIATION_TAG)
    // && root.get(cps.getPrefix(cps.size() - 1)).isPresent())
    // s.setExplicitEmojiVariation(true);
    // if (cps.getLast().equals(TEXT_VARIATION_TAG)
    // && root.get(cps.getPrefix(cps.size() - 1)).isPresent())
    // s.setExplicitTextVariation(true);
    // });
    // });

    System.setOut(new PrintStream(new FileOutputStream("/Users/aboothe/Foobar.java")));

    AtomicInteger count = new AtomicInteger();
    System.out.println("public class Foobar {");
    Set<CodePointSequence> after = new HashSet<>();
    root.walk((cps, n) -> {
      n.getSequence().ifPresent(s -> {
        if (s.getShortName() != null) {
          System.out.printf(" public static final Emoji %s = Emoji.of(%s, \"%s\", %s);\n",
              shortNameToVariableName(s), codePointSequenceToIntArray(cps), s.getShortName(),
              codePointSequenceToPeople(cps, s.getShortName()));
          System.out.println();
          after.add(cps);
          count.incrementAndGet();
        } else {
          throw new IllegalStateException("no name");
        }
      });
    });
    System.out.println("}");

    // System.out.println(count.get());

    // System.out.println(after.size());

    //
    // EmojiTests emojiTests;
    // try (InputStream in =
    // new URL(UNICODE_BASE_URL + "/emoji/" + unicodeVersion + "/emoji-test.txt").openStream()) {
    // emojiTests = new EmojiTestsLoader().load(in);
    // }
    //
  }

  private static final Pattern NON_ALPHANUM = Pattern.compile("[^a-zA-Z0-9]+");

  private static String shortNameToVariableName(String shortName) {
    return NON_ALPHANUM.matcher(shortName.toUpperCase()).replaceAll(m -> "_");
  }

  private static String shortNameToVariableName(EmojiSequence s) {
    String result = shortNameToVariableName(s.getShortName());
    if (s.isExplicitEmojiVariation())
      result = result + "_EMOJI_VARIATION";
    else if (s.isExplicitTextVariation())
      result = result + "_TEXT_VARIATION";
    while (result.endsWith("_"))
      result = result.substring(0, result.length() - 1);
    return result;
  }

  private static String codePointSequenceToIntArray(CodePointSequence s) {
    return String.format("new int[]{%s}", s.getElements().stream()
        .map(cp -> "0x" + Integer.toHexString(cp.getValue()).toUpperCase()).collect(joining(", ")));
  }

  private static String codePointSequenceToPeople(CodePointSequence cps, String shortName) {
    List<PersonHintBuilder> pbs = People.detect(cps, shortName);

    if (pbs.isEmpty())
      return "emptyList()";

    return format("asList(%s)", pbs.stream().map(pb -> {
      // age gender skintone
      return format("PersonHint.of(%s, %s, %s)",
          Optional.ofNullable(pb.getAge()).map(s -> "AgeHint." + s).orElse("null"),
          Optional.ofNullable(pb.getGender()).map(s -> "GenderHint." + s).orElse("null"),
          Optional.ofNullable(pb.getSkinTone()).map(s -> "SkinTone." + s).orElse("null"));
    }).collect(joining(", ")));
  }

  private static <T> Set<T> subtract(Set<T> first, Set<T> second) {
    Set<T> result = new HashSet<>(first);
    result.removeAll(second);
    return result;
  }

}
