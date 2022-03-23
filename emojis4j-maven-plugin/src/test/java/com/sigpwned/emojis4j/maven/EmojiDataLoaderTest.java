package com.sigpwned.emojis4j.maven;

public class EmojiDataLoaderTest {
  // @Test
  // public void fullTest() throws IOException {
  // EmojiData data;
  // try (InputStream in = Resources.getResource("test-emoji-data-full.txt").openStream()) {
  // data = new EmojiDataLoader().load(in);
  // }
  // assertThat(data.getEntries().size(), is(1229));
  // assertThat(
  // data.getEntries().stream()
  // .filter(e -> e.getProperty().equals(EmojiData.EXTENDED_PICTOGRAPHIC)).count(),
  // is(503L));
  // assertThat(
  // data.getEntries().stream().filter(e -> e.getProperty().equals(EmojiData.EMOJI)).count(),
  // is(394L));
  // assertThat(data.getEntries().stream()
  // .filter(e -> e.getProperty().equals(EmojiData.EMOJI_PRESENTATION)).count(), is(272L));
  // assertThat(data.getEntries().stream()
  // .filter(e -> e.getProperty().equals(EmojiData.EMOJI_MODIFIER_BASE)).count(), is(49L));
  // assertThat(data.getEntries().stream()
  // .filter(e -> e.getProperty().equals(EmojiData.EMOJI_COMPONENT)).count(), is(10L));
  // assertThat(data.getEntries().stream()
  // .filter(e -> e.getProperty().equals(EmojiData.EMOJI_MODIFIER)).count(), is(1L));
  // }
  //
  // @Test
  // public void sampleTest() throws IOException {
  // EmojiData data;
  // try (InputStream in = Resources.getResource("test-emoji-data-sample.txt").openStream()) {
  // data = new EmojiDataLoader().load(in);
  // }
  // assertThat(data,
  // is(EmojiData.of(ImmutableList.of(
  // EmojiData.Entry.of(CodePointRange.of(CodePoint.of(0x0023)), "Emoji"),
  // EmojiData.Entry.of(CodePointRange.of(CodePoint.of(0x231A), CodePoint.of(0x231B)),
  // "Emoji_Presentation"),
  // EmojiData.Entry.of(CodePointRange.of(CodePoint.of(0x1F3FB), CodePoint.of(0x1F3FF)),
  // "Emoji_Modifier"),
  // EmojiData.Entry.of(CodePointRange.of(CodePoint.of(0x261D)), "Emoji_Modifier_Base"),
  // EmojiData.Entry.of(CodePointRange.of(CodePoint.of(0x23)), "Emoji_Component"),
  // EmojiData.Entry.of(CodePointRange.of(CodePoint.of(0xA9)), "Extended_Pictographic")))));
  // }
}
