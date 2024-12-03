# Advent of Code 2024

My advent-ures in code in AOC 2024, predominantly in Clojure, but occasionally in something
different.

There is some commentary on the challenges below. Look at the source(s) for more detail.

## Day 1

A gentle introduction with matching across sorted lists in part 1, and a special little calculation in part 2 with help from Clojure's nifty `frequencies` function. Nothing too strenuous to kick things off.

## Day 2

Part 1 is a couple of straightforward tests over the difference vectors. Part 2 requires some fiddling to generate all the candidates, but you can still use the part 1 test function. So far, so good, wading in the relative shallows. The drop will come soon.

## Day 3

With some regex, part 1 is easy. For part 2 we need to thread some state throughout both the individual lines and also the entire sequence of lines. Because of immutable data my go-to for managing state is `reduce`, so I used it twice in nested functions to give me the answer.