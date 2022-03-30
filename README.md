# EMOJI4J

Emoji4j is a high-performance, standards-compliant emoji processor for Java 8 or later.

## Goals

* Comply with the Unicode 14.0 standard for emoji and pictographs
* Provide library support for the most common emoji processing tasks
* Go fast
* Keeps JAR size and dependency footprint small

## Non-Goals

* Support all emoji processing tasks
* Provide complex emoji building support, e.g. adding and removing modifiers
* Provide structured emoji metadata, e.g. person representations, skin color, etc.

## A Brief History of Emoji

According to [the Unicode standard](https://www.unicode.org/reports/tr51/index.html#def_emoji), an emoji is "A colorful pictograph that can be used inline in text."

Using typeset text imagery to convey emotion -- so-called "emoticons," like ":-)" -- date back at least to 1982, when a Carnegie Mellon computer scientist published the following to message to an internal [BBS](https://en.wikipedia.org/wiki/Bulletin_board_system):

    19-Sep-82 11:44    Scott E  Fahlman             :-)
    From: Scott E  Fahlman <Fahlman at Cmu-20c>
     
    I propose that the following character sequence for joke markers:
            
    :-)
            
    Read it sideways.  Actually, it is probably more economical to mark
    things that are NOT jokes, given current trends.  For this, use
            
    :-(
    
Emoji are images appearing in text that convey this same information, but more visually.

Proper emoji are a convergence of a couple related technologies:

* Emoticon "ASCII art" mentioned above
* Proto-emoji image characters on Japanese mobile phones in the 1990s
* Pictograph image characters in fonts like [Wingdings](https://en.wikipedia.org/wiki/Wingdings)

The "modern" emoji era started in 2010, when "emoji" were properly standardized into [Unicode](https://en.wikipedia.org/wiki/Unicode) version [6.0](https://unicode.org/Public/6.0.0/). For more information, see [the timeline](https://emojitimeline.com/).

Due to their convergent nature, there are a couple of different "kinds" of emoji. Each type represents a concept in one grapheme, or visually distinct character.

### Emoji

First, there are proper emoji. A good example of an emoji is "☺️", code points `263A FE0F`, smiling face. Note that it is colorful, and does not visually match the text around it. When reading the standard, these are emoji characters with the `Emoji_Presentation` property, or an emoji sequence qualified with the emoji variation.

### Pictograph

Second, there are pictographs, which technically are not emoji, but are still images captured in Unicode text, so the difference is largely pedantic. The corresponding pictograph to the smiling face emoji above is "☺", code points `263A`, smiling face. Note that it is monochrome, and matches the text around it. This is characteristic of pictographs. When reading the standard, these are emoji characters without the `Emoji_Presentation` property but with the `Extended_Pictographic` property, or an emoji sequence qualified with the text variation.

Not all emoji have a corresponding pictograph. Not all pictograph have a corresponding emoji. They are separate and distinct, but related.

## The Current State of Emoji

The [latest Unicode version](https://unicode.org/Public/14.0.0/) defines [3,633 official emoji](https://blog.emojipedia.org/whats-new-in-unicode-14-0/), plus several hundred additional pictographs. The emoji4j library supports all of these graphemes.

## Code Examples

The workhorse of emoji4j is the `GraphemeMatcher` class, which is modeled after the `Matcher` class. To manually scan a string `text` for all emoji, use the `find()` method:

    GraphemeMatcher m=new GraphemeMatcher(text);
    while(m.find()) {
        System.out.println("Found grapheme "+m.grapheme().getName());
    }

To replace all emoji with their names, one could use this snippet:

    text = new GraphemeMatcher(text).replaceAll(mr -> mr.grapheme().getName());
    
If users care about support only emoji (as opposed to all image graphemes), then they could check the type attribute of the returned `Grapheme`:

    GraphemeMatcher m=new GraphemeMatcher(text);
    while(m.find()) {
        if(m.grapheme().getType() == Grapheme.Type.EMOJI) {
            System.out.println("Found emoji "+m.grapheme().getName());
        }
    }    

## Performance

Performance matters! High performance is an explicitly-stated goal of the emoji4j project.

Performance measurement is a complex topic. These (simple) benchmarks are put together in good faith solely to compare the performance of different libraries and text processing methods.

The benchmarks were built using [JMH](https://openjdk.java.net/projects/code-tools/jmh/). Measurements were taken on an EC2 `m6i.large` instance using `openjdk-17-jdk-headless`.

### Benchmarks

The emoji4j-benchmarks project defines three benchmarks:

* `EmojiJavaBenchmark.tweets` -- Use [emoji-java](https://github.com/vdurmont/emoji-java) to extract all emoji from an emoji-rich text snippet.
* `GraphemeMatcherBenchmark.tweets` -- Use emoji4j to extract all emoji and pictographs from an emoji-rich text snippet.
* `NopBenchmark.tweets` -- Use a simple for loop to iterate over the code points in an emoji-rich text snippet.

The "text snippet" is 1MB of tweet text captured from [the Twitter Streaming API](https://developer.twitter.com/en/docs/twitter-api/v1/tweets/sample-realtime/api-reference/get-statuses-sample). As a result, the benchmark results can loosely be interpreted as MB/s.

### Results

The benchmark results are as follows:

    Benchmark                         Mode  Cnt     Score     Error  Units
    EmojiJavaBenchmark.tweets        thrpt   15    80.258 ±  11.278  ops/s
    GraphemeMatcherBenchmark.tweets  thrpt   15   215.996 ±  21.979  ops/s
    NopBenchmark.tweets              thrpt   15  1358.919 ± 268.917  ops/s

According to the benchmarks, emoji4j runs about 2.7x as fast as emoji-java. However, there is still a lot of performance to gain back versus a simple code point scan.