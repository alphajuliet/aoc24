# Advent of Code 2024

My advent-ures in code in AOC 2024, predominantly in my favourite language Clojure, but occasionally in something different if I'm feeling adventurous.

Below is some commentary on the challenges. Look at the source(s) for more detail.

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

Now we're getting into some fibre. Getting the data into a good structure is the first and often the critical decision in tackling the problem. I overdid this one slightly in part 1 by unnecessarily tracking the labels of each node and antinode but it didn't impact much. Part 2 was a simple extension of the part 1 solution, using the excellent `iterate` and `take-while` functions, and a predicate that checks that the generated coordinates are within the bounds of the grid.  Did I say that I really like Clojure?

## Day 9

Part 1 is a little fiddly but ok. Part 2 requires a different approach by dealing with larger chunks. I understand the algorithm but I haven't worked out how to code it elegantly in Clojure yet. I'll keep thinking about that while I charge on.

## Day 10

This is pretty standard fare for AoC: finding paths in matrices under various constraints. These constraints are simple so it was easy to turn the matrix into a directed graph in [ubergraph](https://github.com/Engelberg/ubergraph) and use graph algorithms to find paths from the trailheads to the trail ends. For part 1 we only need the shortest paths (they are all the same length anyway), and in part 2 we need all the paths from each trail start to end. There is (sadly) no `ubergraph` function for that so I had to roll my own with my pal Claude.

## Day 11

Ok, some simple rules in part 1, we run it over 25 iterations, easy gold star. Part 2 is the first case this year that precludes a brute force approach because we have some exponential growth going on. We need to be cleverer. Poking around Reddit shows some suggestions. I'll park this one and come back to it when I make the time to think more deeply about it.

## Day 12

Part 1 is a matter of working out which cells are on the perimeter, i.e. have one more neighbours that are outside the region. Part 2 is more challenging because we need to find merged cell edges to form sides. A Reddit tip is that the number of sides equals the number of corners, so that gives two approaches. Both require some careful thinking about all the cases. Parked for now.

## Day 13

This one is a little easier than I thought. The problem can be expressed as a simple 2x2 linear equation, so I pull out my trusty `clojure.core.matrix` library to solve it. Part 2 adds a very large number to the output but it doesn't change the approach and the matrix library can handle it, no problem.

But wait, how did I transform each wordy input statement into a matrix? Well, I could do it with lots of regexs and splits but for elegance I like to use the `instaparse` library. The input grammar is simple and so generating some vectors isn't too much of a stretch, and I get to practice my EBNF.

## Day 14

So, part 1 is a bit like day 6 in that we simulate a bunch of robots traversing a torus, and then count their positions after 100 steps. Part 2 is very undefined, so I resorted to Reddit for some ideas, and settled on the safety score going below a certain threshold. It took some trial and error to set that limit, but it worked. Oh, and I got to make a parser again.

## Day 15

I thought carefully about this one, and how to best represent the warehouse and how to work out which boxes move where. I contemplated some weird recursive solution but had an epiphany and realised that I could just use a regular expression replacement (`str/replace`) on the strings themselves. That was very compact but still needed to be wrapped in code to `update` the immutable data, and a `reduce` to transit through all the moves. I was very happy with that... until I saw Part 2, and realised my clever little regex scheme will not work. Back to square one. Parked...

## Day 16

Ok, here's the first major pathfinding test, with a twist, literally in that rotations count in the cost. I had a generic shortest path algorithm ready to go---thanks [Nic McPhee](https://github.com/NicMcPhee/a-star-search/blob/master/src/search/algorithms.clj). I had it working on the base case with a simple cost, but now I needed to thread the orientation state through the various functions.  It was going to be a major pain so I called on my pal Claude (3.5 Sonnet) to help out. Claude did a fantastic job of updating the functions, and also then fixed a `NullPointerException` where it missed something. Suitably impressed, I solved part 1. Part 2 needed some work to capture all the shortest paths and then the set of all the visited locations. Again, Claude helped here because this was proving painful. 

## Day 17

I enjoy writing these little microcode machines so part 1 was fun. Part 2 not so much. I kinda have a vague idea of how to tackle this from my reading on Reddit, but it requires some search algorithm, and will end up with some large integers. It needs some deeper thought. I'd rather charge on for now, so this one is parked. 

## Day 18

For this one, I called on my generic pathfinding algorithm, which worked fine for part 1. Part 2 needs more finesse because we need to consider all the paths from start to end and then work out when none of them are valid any more. I had a solution but it ran out of memory. I couldn't optimise it enough so resorted to good old brute force, while I went out and drank beer. It worked, but it's far from an elegant solution.

## Day 19

As this is a parsing problem we can roll out `instaparse`. The initial list holds the symbols, from which we can build a simple grammar, and the other list consists of the words to push through the parser. This is pretty trivial for part 1 once we've built a grammar. In Part 2 I anticipated using the handy `parses` function that returns all the matching parse trees for a given word. Perfect. Except that it runs out of heap space, despite my efforts, and so I can't complete this one yet. Memoisation might help but I'm not planning to go diving into the parser library to work out how.
