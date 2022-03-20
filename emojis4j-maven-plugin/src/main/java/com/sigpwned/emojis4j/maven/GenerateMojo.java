package com.sigpwned.emojis4j.maven;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

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

  @Parameter(property = "emojis4j.target.directory", defaultValue = "generated-sources/emojis4j")
  private String outputDirectory;

  @Parameter(property = "emojis4j.target.package", defaultValue = "com.sigpwned.emojis4j")
  private String outputPackage;

  private static final String UNICODE_BASE_URL = "https://unicode.org/Public";

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      DerivedNames derivedNames;
      try (InputStream in =
          new URL(UNICODE_BASE_URL + "/" + unicodeVersion + ".0/ucd/extracted/DerivedName.txt")
              .openStream()) {
        derivedNames = new DerivedNamesLoader().load(in);
      }

      EmojiData emojiData;
      try (InputStream in =
          new URL(UNICODE_BASE_URL + "/" + unicodeVersion + ".0/ucd/emoji/emoji-data.txt")
              .openStream()) {
        emojiData = new EmojiDataLoader().load(in);
      }

      EmojiVariations emojiVariations;
      try (InputStream in = new URL(
          UNICODE_BASE_URL + "/" + unicodeVersion + ".0/ucd/emoji/emoji-variation-sequences.txt")
              .openStream()) {
        emojiVariations = new EmojiVariationsLoader().load(in);
      }

      EmojiTests emojiTests;
      try (InputStream in =
          new URL(UNICODE_BASE_URL + "/emoji/" + unicodeVersion + "/emoji-test.txt").openStream()) {
        emojiTests = new EmojiTestsLoader().load(in);
      }

      EmojiSequences emojiSequences;
      try (InputStream in =
          new URL(UNICODE_BASE_URL + "/emoji/" + unicodeVersion + "/emoji-sequences.txt")
              .openStream()) {
        emojiSequences = new EmojiSequencesLoader().load(in);
      }

      EmojiZwjSequences emojiZwjSequences;
      try (InputStream in =
          new URL(UNICODE_BASE_URL + "/emoji/" + unicodeVersion + "/emoji-zwj-sequences.txt")
              .openStream()) {
        emojiZwjSequences = new EmojiZwjSequencesLoader().load(in);
      }

    } catch (IOException e) {
      throw new MojoFailureException("Failed to read unicode data", e);
    }
  }
}
