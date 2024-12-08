(ns aoc24.day07
  (:require [aoc24.util :as util]
            [clojure.edn :as edn]
            [clojure.math.combinatorics :as comb]
            [clojure.string :as str]))

(defn read-data
  "Return a list of maps with result and a binary tree"
  [f]
  (->> f
       util/read-lines
       (map #(str/split % #"[:\s]+"))
       (util/mapmap edn/read-string)))

(defn resolve-tree
  [result [a & b] ops]
  (let [n (count b)]
    (->> (range n)
         (reduce
          (fn [acc i]
            (cond
              (= :add (nth ops i)) (+ acc (nth b i))
              (= :mul (nth ops i)) (* acc (nth b i))
              (= :cat (nth ops i)) (edn/read-string (str acc (nth b i)))
              :else acc))
          a)
         (= result))))

(defn solve-tree
  [[result & inputs]]
  (let [num-ops (dec (count inputs))
        op-combs (comb/selections [:add :mul] num-ops)]
    (->> op-combs
         (map #(resolve-tree result inputs %))
         (util/any? true?))))

(defn solve-tree2
  [[result & inputs]]
  (let [num-ops (dec (count inputs))
        op-combs (comb/selections [:add :mul :cat] num-ops)]
    (->> op-combs
         (map #(resolve-tree result inputs %))
         (util/any? true?))))

(defn part1
  [f]
  (let [data (read-data f)]
    (->> data
         (filter solve-tree)
         (map first)
         (reduce +))))

(defn part2
  [f]
  (let [data (read-data f)]
    (->> data
         (filter solve-tree2)
         (map first)
         (reduce +))))

(comment
  (def testf "data/day07-test.txt")
  (def inputf "data/day07-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 testf)
  (part2 inputf))


;; The Endk
