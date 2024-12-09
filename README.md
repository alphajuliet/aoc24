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

## Day 4

So, I initially misread the question and coded up a much more complex (and interesting) solution. I eventually realised my error on the test data, went back to the beginning and did it right for part 1. Part 2, however, seems fine (thanks again `frequencies`) but I'm somehow overshooting the answer. Hmm. I'll be back to it later.

Ok, dumb mistake in using `frequencies`, I need to compare that opposite characters are different. Fixed.

## Day 5

I recognised early that this was about partial orderings, and [partially ordered sets](https://en.wikipedia.org/wiki/Partially_ordered_set) or "posets". A graph is the obvious data structure but rather than using my go-to of `ubergraph` I just did it with hash maps of sets with the given integers and made up comparators to check the partial orderings. Part 1 was a bit more code than I'd like, but it came in useful for part 2 when the comparator was already available for re-sorting the invalid orderings. This was a good day.

I also introduced transducers for fun in the top-level functions. I may need them later when I have larger collections.

## Day 6

Part 1 is a familiar process of simulating movement through an environment based on simple rules, so I pulled out the `reduce` for state management to traverse the room. Convering the input map into coordinates was the little wrinkle that took some time early on. Part 2 is a good puzzle and I can kinda see how to do it logically, but I'm not yet convinced I can find the correct and complete list of candidate obstacle locations and certainly not how to translate that into code. This one goes on hold for now.

## Day 7

Originally, I was going to make a fancy binary tree and then cleverly insert combinations of operations to see what made the result. I then realised the operations are a simple sequence over the list so I more cleverly left things as vectors and did a fold (a.k.a. my mate `reduce`) over the input numbers for each combination of operators and see where they equalled the result. This caused a slight combinatorial explosion in part 2 with the extra operator, but we got there. I give a nod also to `clojure.math.combinatorics` for the `selection` function.

## Day 8

Now we're getting into some fibre. Getting the data into a good structure is the first and often the critical decision in tackling the problem. I overdid this one slightly in part 1 by unnecessarily tracking the labels of each node and antinode but it didn't impact much.
