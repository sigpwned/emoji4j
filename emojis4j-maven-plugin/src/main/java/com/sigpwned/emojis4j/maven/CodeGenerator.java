package com.sigpwned.emojis4j.maven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.sigpwned.emojis4j.maven.util.CodeGeneration;

public class CodeGenerator {
  private final Path outputDirectory;
  private final String packageName;
  private final Path packageDirectory;

  public CodeGenerator(Path outputDirectory, String packageName) throws IOException {
    this.outputDirectory = outputDirectory;
    this.packageName = packageName;

    Path packageDirectory = outputDirectory;
    for (String packageNamePart : packageName.split("\\."))
      packageDirectory = packageDirectory.resolve(packageNamePart);
    this.packageDirectory = packageDirectory;

    Files.createDirectories(getPackageDirectory());
  }

  public void generateGraphemeFile(String className, GraphemeCollection gs) throws IOException {
    Files.writeString(getPackageDirectory().resolve(className + ".java"), CodeGeneration
        .formatSource(CodeGeneration.generateGraphemeFile(getPackageName(), className, gs)));
  }

  public static final String EMOJI_SEQUENCES = "EmojiSequences";

  public static final String EMOJI_ZWJ_SEQUENCES = "EmojiZwjSequences";

  public static final CodePoint ZWJ = CodePoint.of(0x200D);

  public void generateTrieFile(String className, GraphemeTrie trie) throws IOException {
    String triePackageName = getPackageName() + ".trie";

    Path triePackageDirectory = getPackageDirectory().resolve("trie");

    Files.createDirectories(triePackageDirectory);

    for (CodePoint cp : trie.listChildren()) {
      String childClassName = className + cp.toString();
      GraphemeTrie child = trie.getChild(cp);
      Files.writeString(triePackageDirectory.resolve(childClassName + ".java"),
          CodeGeneration.formatSource(
              CodeGeneration.generateTrieFile(triePackageName, childClassName, child, gm -> {
                if (gm.getCodePoints().contains(ZWJ)) {
                  return getPackageName() + "." + EMOJI_ZWJ_SEQUENCES + "."
                      + CodeGeneration.variableName(gm);
                } else {
                  return getPackageName() + "." + EMOJI_SEQUENCES + "."
                      + CodeGeneration.variableName(gm);
                }
              })));
    }

  }

  /**
   * @return the outputDirectory
   */
  public Path getOutputDirectory() {
    return outputDirectory;
  }

  /**
   * @return the packageName
   */
  public String getPackageName() {
    return packageName;
  }

  private Path getPackageDirectory() {
    return packageDirectory;
  }
}
