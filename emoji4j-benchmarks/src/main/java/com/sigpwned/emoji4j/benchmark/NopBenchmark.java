package com.sigpwned.emoji4j.benchmark;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;

@Fork(value = 3) /* jvmArgsAppend = "-XX:+PrintCompilation" */
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
public class NopBenchmark {
  /**
   * Contains exactly 1MB of "random" data sampled from Twitter streaming API. Visually confirmed to
   * be emoji-rich.
   */
  public String tweets;

  @Setup
  public void setupEmojiJavaBenchmark() throws IOException {
    try (
        InputStream in = new GZIPInputStream(Resources.getResource("tweets.txt.gz").openStream())) {
      tweets = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
    }
  }

  /*
   * @formatter:off
   * 
   * As of 2022-03-27:
   * 
   * Benchmark                         Mode  Cnt    Score   Error  Units
   * EmojiJavaBenchmark.tweets        thrpt   15  105.708 ± 0.401  ops/s
   * 
   * @formatter:on
   */
  @Benchmark
  public void tweets(Blackhole blackhole) {
    int count = 0;

    int index = 0;
    while (index < tweets.length()) {
      int codePoint = tweets.codePointAt(index);

      count = count + 1;

      index = index + Character.charCount(codePoint);
    }

    blackhole.consume(count);
  }
}
