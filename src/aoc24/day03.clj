(ns aoc24.day03
  (:require [aoc24.util :as util]))

(defn find-muls
  [line]
  (->> line
       (re-seq #"mul\(([0-9]+),([0-9]+)\)")
       (map (fn [[_ x y]] (* (Integer/parseInt x) (Integer/parseInt y))))
       (reduce +)))

(defn part1
  [f]
  (->> f
       util/read-lines
       (map find-muls)
       (reduce +)))

(comment
  (def testf "data/day03-test.txt")
  (def inputf "data/day03-input.txt")
  (part1 testf)
  (part1 inputf))
  
;; The End