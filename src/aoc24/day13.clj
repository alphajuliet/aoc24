(ns aoc24.day13
  (:require [aoc24.util :as util]
            [instaparse.core :as insta]
            [clojure.edn :as edn]
            [clojure.math :as math]
            [clojure.core.matrix :as m]))

(m/set-current-implementation :vectorz)

(def parse-rules
  (insta/parser 
   "<rules> := rule+
    rule := buttonA <nl> buttonB <nl> prize <nl>+
    buttonA := <'Button A: '> shift
    buttonB := <'Button B: '> shift
    prize := <'Prize: '> coord
    <shift> := <'X+'> number <', '> <'Y+'> number
    <coord> := <'X='> number <', Y='> number
    number := #'\\d+'
    <nl> := '\\n'"))

(defn transform-rules
  [data]
  (insta/transform
   {:rule vector
    :buttonA #(vector %1 %2)
    :buttonB #(vector %1 %2)
    :prize #(vector %1 %2)
    :number edn/read-string}
   data))

(defn read-data
  [f]
  (->> f
       slurp
       parse-rules
       transform-rules))

(defn integer-enough?
  "Is this close enough to be an integer?"
  [x]
  (< (abs (- x (math/round x))) 0.001))

(defn solve-eqn
  [[a b target]]
  (let [m (m/transpose [a b])
        b' (m/transpose target)
        x (m/mmul (m/inverse m) b')]
    (if (every? integer-enough? x)
      (m/mul [3 1] (mapv math/round x))
      nil)))

(defn part1
  [f]
  (->> f
       read-data
       (keep solve-eqn)
       flatten
       (reduce + 0)))

(defn part2
  [f]
  (->> f
       read-data
       (map #(update % 2 (partial mapv (partial + 1e13))))
       (keep solve-eqn)
       flatten
       (reduce + 0)))

(comment
  (def testf "data/day13-test.txt")
  (def inputf "data/day13-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 testf)
  (part2 inputf))

;; The End