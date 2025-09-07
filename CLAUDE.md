# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is an Advent of Code 2024 repository implemented primarily in Clojure. It contains solutions for all 25 days of the challenge, along with utility functions and search algorithms commonly used in competitive programming problems.

## Development Commands

### REPL Development
```bash
# Start Clojure REPL with project dependencies
clj

# Start REPL with development profile (increased heap size)
clj -M:dev

# Run specific day solutions interactively in REPL
# Each day file includes commented examples at the bottom
```

### Running Solutions
```bash
# Run individual day solutions by evaluating the comment blocks
# Example for day 1:
clj -M -e "(require '[aoc24.day01 :as d01]) (d01/part1 \"data/day01-input.txt\")"

# Run with Babashka (if available)
bb -e "(require '[aoc24.day01 :as d01]) (d01/part1 \"data/day01-input.txt\")"
```

### Testing
```bash
# Run all tests using the test runner
clj -M:test

# Run tests with exec-fn (alternative approach)
clj -X:test
```

## Code Architecture

### Core Structure

- **`src/aoc24/`**: Main namespace containing daily solutions
  - `day01.clj` through `day25.clj`: Individual day implementations
  - `util.clj`: Comprehensive utility functions library
  - `search.clj`: Graph search algorithms (BFS, shortest path, A*)

- **`data/`**: Input files for each day's challenges
  - `dayXX-input.txt`: Actual challenge input
  - `dayXX-test.txt`: Test/example input

### Key Dependencies

The project uses several specialized Clojure libraries:

- **`instaparse`**: Grammar-based parsing for complex input formats (used in days 13, 14, 19, 24)
- **`core.matrix`**: Matrix operations and linear algebra (day 13 uses 2x2 linear equations)
- **`math.combinatorics`**: Combinatorial functions for generating permutations/combinations
- **`ubergraph`**: Advanced graph algorithms including pathfinding and clique detection
- **`data.priority-map`**: Priority queues for efficient search algorithms

### Solution Patterns

Each day follows a consistent structure:

1. **Input parsing**: Custom functions to transform text input into workable data structures
2. **Core logic**: Usually split into `part1` and `part2` functions
3. **Testing**: Comment blocks at the end with example usage on both test and input files

### Utility Functions (`util.clj`)

Comprehensive utility library organized by category:
- **Numeric**: `clamp`, mathematical operations
- **Collections**: `argmax`, `argmin`, `rotate`, `transpose` (via `T`), advanced collection manipulation
- **Maps**: `map-kv`, `map-key`, key/value transformations  
- **Strings**: `str->num`, `left-pad`, string rotation
- **Matrix operations**: `mat-find-all`, `safe-mget` for bounds-safe matrix access
- **Logic utilities**: `count-if`, `take-until`, `take-upto`
- **File I/O**: `read-lines` for standard input processing

### Search Algorithms (`search.clj`)

Implements several pathfinding algorithms from Nic McPhee's A* search library:
- **`breadth-first-search`**: Standard BFS implementation
- **`shortest-path`**: Dijkstra's algorithm using priority queues
- **`heuristic-search`**: A* search with heuristic function
- **`find-all-paths`**: Finds all optimal paths (used in day 16)
- **`extract-path`** and **`extract-all-paths`**: Path reconstruction utilities

## Development Workflow

### Adding New Solutions

1. Create new day file: `src/aoc24/dayXX.clj`
2. Follow the established namespace pattern: `(ns aoc24.dayXX (:require [aoc24.util :as util]))`
3. Add input files to `data/` directory
4. Use existing utility functions where possible, especially from `util.clj`
5. Include comment block with test cases at the end

### Common Patterns Used

- **State management**: Heavy use of `reduce` for threading state through computations
- **Matrix processing**: Convert inputs to coordinate-based representations using `util/mat-find-all`
- **Parsing**: Use `instaparse` for complex grammar-based input parsing
- **Graph problems**: Leverage `ubergraph` for pathfinding and network analysis
- **Immutable updates**: Consistent use of `update`, `assoc`, and functional transformations

### Problem Categories by Day

- **Input parsing**: Days 13, 14, 19, 24 (using `instaparse`)
- **Graph traversal**: Days 6, 10, 16, 18, 20 (using custom and `ubergraph` algorithms)
- **Matrix/grid problems**: Days 4, 6, 8, 12, 15
- **Mathematical**: Days 7, 11, 13 (combinatorics, linear algebra)
- **Logic circuits**: Day 24 (circuit simulation)
- **Network analysis**: Day 23 (clique detection)

## Important Notes

- Many Part 2 solutions remain incomplete due to complexity/time constraints
- The repository uses both regular Clojure CLI and has Babashka support
- Claude 3.5 Sonnet was used as a coding assistant for complex algorithms (noted in README)
- Input data files are stored in `data/` directory but are not version controlled
- Development includes clj-kondo and LSP support for IDE integration
