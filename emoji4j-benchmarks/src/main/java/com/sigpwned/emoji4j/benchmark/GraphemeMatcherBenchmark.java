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
import com.sigpwned.emoji4j.core.GraphemeMatcher;
import com.sigpwned.emoji4j.core.trie.DefaultGraphemeTrie;
import com.sigpwned.emoji4j.core.util.Graphemes;

@Fork(value = 3) /* jvmArgsAppend = "-XX:+PrintCompilation" */
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
public class GraphemeMatcherBenchmark {
  /**
   * Contains exactly 1MB of "random" data sampled from Twitter streaming API. Visually confirmed to
   * be emoji-rich.
   */
  public String tweets;

  public DefaultGraphemeTrie trie;

  @Setup
  public void setupGraphemeMatcherBenchmark() throws IOException {
    try (
        InputStream in = new GZIPInputStream(Resources.getResource("tweets.txt.gz").openStream())) {
      tweets = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
    }

    trie = DefaultGraphemeTrie.fromGraphemeData(Graphemes.getGraphemeData());
  }

  /*
   * @formatter:off
   * 
   * As of 2022-03-27:
   * 
   * Benchmark                         Mode  Cnt    Score   Error  Units
   * GraphemeMatcherBenchmark.tweets  thrpt   15  440.377 ± 4.055  ops/s
   * 
   * @formatter:on
   */
  @Benchmark
  public void tweets(Blackhole blackhole) {
    int count = 0;
    GraphemeMatcher m = new GraphemeMatcher(trie, tweets);
    while (m.find()) {
      count = count + 1;
    }
    blackhole.consume(count);
  }
}
