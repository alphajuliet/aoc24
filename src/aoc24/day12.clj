(ns aoc24.day12
  (:require [aoc24.util :as util]
            [clojure.set :as set]))

(defn read-data
  [f]
  (->> f
       util/read-lines))

(defn find-all-regions [grid]
  (let [height (count grid)
        width (count (first grid))
        grid-vec (vec (map vec grid))
        visited (atom #{})
        directions [[0 1] [0 -1] [1 0] [-1 0]]
        valid-coord? (fn [[x y]]
                       (and (>= x 0) (< x height)
                            (>= y 0) (< y width)))
        ;; Depth-first search to find contiguous region
        dfs (fn dfs [x y letter region]
              (if (or (not (valid-coord? [x y]))
                      (contains? @visited [x y])
                      (not= (get-in grid-vec [x y]) letter))
                region
                (let [new-region (conj region [x y])]
                  (swap! visited conj [x y])
                  (reduce (fn [acc [dx dy]]
                            (dfs (+ x dx) (+ y dy) letter acc))
                          new-region
                          directions))))]
    ;; Find regions for each unvisited coordinate
    (reduce (fn [regions [x y]]
              (if (contains? @visited [x y])
                regions
                (let [letter (get-in grid-vec [x y])
                      region (dfs x y letter #{})]
                  (conj regions region))))
            []
            (for [x (range height)
                  y (range width)]
              [x y]))))

(defn perimeter
  "Calculate the length of the perimeter of a set of contiguous coordinates"
  [coord-set]
  (let [dirs [[0 1] [0 -1] [1 0] [-1 0]]]
    (reduce (fn [perimeter [x y]]
              (let [neighbours (map (fn [[dx dy]] [(+ x dx) (+ y dy)]) dirs)
                    cell-perimeter (count (filter #(not (contains? coord-set %)) neighbours))]
                (+ perimeter cell-perimeter)))
            0
            coord-set)))

(defn count-sides
  [region]
  false)

(defn score-region
  "Find the area and perimeter of a region and multiply them"
  [region]
  (let [area (count region)
        perimeter (perimeter region)]
    (* area perimeter)))

(defn score-region2
  "Find the area and sides of a region and multiply them"
  [region]
  (let [area (count region)
        sides (count-sides region)]
    (* area sides)))

(defn part1
  [f]
  (->> f
       read-data
       find-all-regions
       (map score-region)
       (reduce +)))

(defn part2
  [f]
  (->> f
       read-data
       find-all-regions
       (map score-region2)
       (reduce +)))

(comment
  (def test1f "data/day12-test1.txt")
  (def test2f "data/day12-test2.txt")
  (def test3f "data/day12-test3.txt")
  (def test4f "data/day12-test4.txt")
  (def test5f "data/day12-test5.txt")
  (def inputf "data/day12-input.txt")

  (time (part1 test1f))
  (time (part1 test2f))
  (time (part1 test3f))
  (time (part1 inputf))

  (time (part2 test1f))
  (time (part2 test2f))
  (time (part2 test4f))
  (time (part2 test5f))
  (time (part2 inputf)))

;; The End