(ns aoc24.day08
  (:require [aoc24.util :as util]
            [clojure.core.matrix :as m]
            [clojure.math.combinatorics :as combo]
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
    (->> (vector (mapv + rc1 diff)
                 (mapv - rc2 diff))
         (filter #(in-range? dims %)))))

(defn antinodes2
  "Find all antinodes, including resonances"
  [dims [id1 rc1] [id2 rc2]]
  (let [diff (map - rc1 rc2)]
    (into (take-while #(in-range? dims %) (iterate #(mapv + % diff) rc1))
          (take-while #(in-range? dims %) (iterate #(mapv - % diff) rc2)))))
     

(defn all-antinodes
  "Find all the antinodes of pairs from the list of antennae"
  [antinode-fn dims nodes]
  (->> (for [[n1 n2] (combo/combinations nodes 2)]
         (antinode-fn dims n1 n2))
       (apply concat)))

(defn count-antinodes
  "Count the antinodes from the data with the given antinode function"
  [antinode-fn data]
  (let [nodes (find-nodes data)
        dims (m/shape data)] 
    (->> nodes
         (group-by first)
         (map #(all-antinodes antinode-fn dims (val %)))
         (apply concat)
         set
         count)))

(defn part1
  [f]
  (->> (read-data f)
       (count-antinodes antinodes)))

(defn part2
  [f]
  (->> (read-data f)
       (count-antinodes antinodes2)))

(comment
  (def pp clojure.pprint/pprint)
  (def testf "data/day08-test.txt")
  (def inputf "data/day08-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 testf)
  (part2 inputf))
  
;; The End