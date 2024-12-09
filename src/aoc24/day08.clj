(ns aoc24.day08
  (:require [aoc24.util :as util]
            [clojure.core.matrix :as m]
            [clojure.math.combinatorics :as combo]
            [clojure.set :as set]
            [clojure.string :as str]))

(defn in-range?
  [[maxr maxc] [r c]]
  (and (<= 0 r (dec maxr)) (<= 0 c (dec maxc))))

(defn find-nodes
  "Find and label all the nodes in the matrix"
  [mat]
  (for [[r c] (m/index-seq mat)
        :let [node (m/mget mat r c)]
        :when (not= "." node)]
    [node [r c]]))

(defn read-data
  [f]
  (->> f
       util/read-lines
       (map #(str/split % #""))
       (m/matrix)))

(defn antinodes
  "Find the antinodes of two given antennae"
  [dims [id1 rc1] [id2 rc2]]
  {:pre (= id1 id2)}
  (let [diff (map - rc1 rc2)]
    (->> (vector ["#" (mapv + rc1 diff)]
                 ["#" (mapv - rc2 diff)])
         (filter #(in-range? dims (second %))))))

(defn all-antinodes
  "Find all the antinodes of pairs from the list of antennae"
  [dims nodes]
  (->> (for [[n1 n2] (combo/combinations nodes 2)]
         (antinodes dims n1 n2))
       (apply concat)))

(defn part1
  [f]
  (let [data (read-data f)
        nodes (find-nodes data)
        dims (m/shape data)] 
    (->> nodes
         (group-by first)
         (map #(all-antinodes dims (val %)))
         (apply concat)
         set
         count)))

(comment
  (def pp clojure.pprint/pprint)
  (def testf "data/day08-test.txt")
  (def inputf "data/day08-input.txt")
  (part1 testf)
  (part1 inputf))
  

;; The End
