package com.sigpwned.emojis4j.maven.util;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.sigpwned.emojis4j.maven.CodePoint;
import com.sigpwned.emojis4j.maven.CodePointSequence;
import com.sigpwned.emojis4j.maven.GraphemeBuilder;
import com.sigpwned.emojis4j.maven.GraphemeCollection;
import com.sigpwned.emojis4j.maven.GraphemeMapping;
import com.sigpwned.emojis4j.maven.GraphemeTrie;

public final class CodeGeneration {
  private CodeGeneration() {}

  public static String generateCodePointsFile(String packageName, String fileName,
      GraphemeCollection graphemes) {
    StringWriter result = new StringWriter();
    try (PrintWriter out = new PrintWriter(result)) {
      out.printf("package %s;\n", packageName);
      out.printf("\n");
      out.printf("import com.sigpwned.emojis4j.*;\n");
      out.printf("\n");
      out.printf("public final class %s {\n", fileName);
      out.printf("    private %s() {}\n", fileName);
      out.printf("\n");
      for (GraphemeMapping e : graphemes) {
        CodePointSequence cps = e.getCodePoints();
        GraphemeBuilder es = e.getGrapheme();
        out.printf("    public static final int[] %s = %s;\n",
            shortNameToVariableName(cps, es.getShortName()), codePointSequenceToIntArray(cps));
        out.printf("\n");
      }
      out.printf("}");
    }
    return result.toString();
  }

  public static String generateTrieFile(String packageName, String fileName, GraphemeTrie trie,
      Function<GraphemeMapping, String> referenceGenerator) {
    StringWriter result = new StringWriter();
    try (PrintWriter out = new PrintWriter(result)) {
      out.printf("package %s;\n", packageName);
      out.printf("\n");
      out.printf("import com.sigpwned.emoji4j.core.GraphemeTrie;\n");
      out.printf("\n");
      out.printf("public final class %s {\n", fileName);
      out.printf("    private %s() {}\n", fileName);
      out.printf("\n");
      out.printf("    public static final GraphemeTrie ROOT = \n");
      out.printf("        %s;\n", generateTrie(trie, referenceGenerator));
      out.printf("}");
    }
    return formatSource(result.toString());
  }

  public static String generateTrie(GraphemeTrie trie,
      Function<GraphemeMapping, String> referenceGenerator) {
    if (trie.listChildren().isEmpty()) {
      return format("new GraphemeTrie(%s)",
          referenceGenerator.apply(GraphemeMapping.of(trie.getCodePoints(), trie.getGrapheme())));
    } else if (trie.getGrapheme() == null) {
      return format("new GraphemeTrie(%s, %s)",
          intArray(trie.listChildren().stream().mapToInt(CodePoint::getValue).toArray()),
          format("new GraphemeTrie[] {%s}",
              trie.listChildren().stream()
                  .map(cp -> CodeGeneration.generateTrie(trie.getChild(cp), referenceGenerator))
                  .collect(joining(", "))));

    } else {
      return format("new GraphemeTrie(%s, %s, %s)",
          intArray(trie.listChildren().stream().mapToInt(CodePoint::getValue).toArray()),
          format("new GraphemeTrie[] {%s}",
              trie.listChildren().stream()
                  .map(cp -> CodeGeneration.generateTrie(trie.getChild(cp), referenceGenerator))
                  .collect(joining(", "))),
          referenceGenerator.apply(GraphemeMapping.of(trie.getCodePoints(), trie.getGrapheme())));
    }
  }

  public static <T> List<T> append(List<T> xs, T x) {
    List<T> result = new ArrayList<>(xs);
    result.add(x);
    return unmodifiableList(result);
  }

  public static String formatSource(String source) {
    try {
      return new Formatter().formatSource(source);
    } catch (FormatterException e) {
      throw new UncheckedIOException(new IOException("Failed to format source code", e));
    }
  }

  public static String generateGraphemeFile(String packageName, String fileName,
      GraphemeCollection graphemes) {
    return generateGraphemeFile(packageName, fileName, graphemes,
        CodeGeneration::graphemeMappingToIntArray);
  }

  public static String generateGraphemeFile(String packageName, String fileName,
      GraphemeCollection graphemes, Function<GraphemeMapping, String> codePointsCodeGenerator) {
    StringWriter result = new StringWriter();
    try (PrintWriter out = new PrintWriter(result)) {
      out.printf("package %s;\n", packageName);
      out.printf("\n");
      out.printf("import com.sigpwned.emoji4j.core.grapheme.Emoji;\n");
      out.printf("import com.sigpwned.emoji4j.core.grapheme.Pictographic;\n");
      out.printf("import com.sigpwned.emojis4j.*;\n");
      out.printf("\n");
      out.printf("public final class %s {\n", fileName);
      out.printf("    private %s() {}\n", fileName);
      out.printf("\n");
      for (GraphemeMapping e : graphemes) {
        CodePointSequence cps = e.getCodePoints();
        GraphemeBuilder es = e.getGrapheme();
        String type = e.getGrapheme().getType();
        out.printf("    public static final %s %s = %s.of(%s, \"%s\");\n", type,
            shortNameToVariableName(cps, es.getShortName()), type, codePointsCodeGenerator.apply(e),
            unescapeShortName(es.getShortName()).toUpperCase());
        out.printf("\n");
      }
      out.printf("}");
    }
    return result.toString();
  }

  private static final Pattern HEX_ESCAPE = Pattern.compile("\\\\x\\{([0-9a-fA-F]+)\\}");

  private static String unescapeShortName(String shortName) {
    return HEX_ESCAPE.matcher(shortName)
        .replaceAll(m -> new String(new int[] {Integer.parseInt(m.group(1), 16)}, 0, 1));
  }

  private static final Pattern NON_ALPHANUM = Pattern.compile("[^a-zA-Z0-9]+");

  private static final Pattern TRAILING_UNDERSCORE = Pattern.compile("_+$");

  public static String variableName(GraphemeMapping m) {
    return shortNameToVariableName(m.getCodePoints(), m.getGrapheme().getShortName());
  }

  public static String shortNameToVariableName(CodePointSequence cps, String shortName) {
    String suffix;
    if (cps.size() == 2 && cps.getLast().getValue() == 0xFE0F)
      suffix = "_EMOJI_VARIATION";
    else if (cps.size() == 2 && cps.getLast().getValue() == 0xFE0E)
      suffix = "_TEXT_VARIATION";
    else
      suffix = "";

    shortName = unescapeShortName(shortName).toUpperCase();

    if (shortName.equals("KEYCAP: #")) {
      return "KEYCAP_HASH" + suffix;
    } else if (shortName.equals("KEYCAP: *")) {
      return "KEYCAP_STAR" + suffix;
    } else {
      return TRAILING_UNDERSCORE.matcher(NON_ALPHANUM.matcher(shortName).replaceAll(m -> "_"))
          .replaceAll("") + suffix;
    }
  }

  private static String graphemeMappingToIntArray(GraphemeMapping m) {
    return codePointSequenceToIntArray(m.getCodePoints());
  }

  private static String codePointSequenceToIntArray(CodePointSequence s) {
    return intArray(s.stream().mapToInt(CodePoint::getValue).toArray());
  }

  private static String intArray(int[] xs) {
    return String.format("new int[]{%s}", Arrays.stream(xs)
        .mapToObj(x -> "0x" + Integer.toHexString(x).toUpperCase()).collect(joining(", ")));
  }
}
