# EMOJI4J

Emoji4j is a high-performance, standards-compliant emoji processor for Java 8 or later.

## Goals

Provide a high-level library that:

* Complies with the Unicode 14.0 standard for emoji
* Provides support for the most common emoji processing tasks
* Runs quickly
* Keeps JAR size and dependency footprint small

## Non-Goals

* Support all emoji processing tasks
* Provide complex emoji building support, e.g. adding and removing modifiers
* Provide emoji metadata, e.g. person representations, skin color, etc.

## What is an Emoji?

TODO

### Emoji vs Pictograph

## Examples

The workhorse of emoji4j is the `GraphemeMatcher` class, which is modeled after the `Matcher` class. To manually scan a string `text` for all emoji, use the `find()` method:

    GraphemeMatcher m=new GraphemeMatcher(text);
    while(m.find()) {
        System.out.println("Found emoji "+m.grapheme().getName());
    }

To replace all emoji with their names, one could use this snippet:

    text = new GraphemeMatcher(text).replaceAll(mr -> mr.grapheme().getName());

## Performance

In benchmarks, emoji4j runs about 4.2x faster than [emoji-java](https://github.com/vdurmont/emoji-java).