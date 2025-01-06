(ns aoc24.day23
  (:require [aoc24.util :as util]
            [clojure.set :as set]
            [clojure.string :as str]
            [ubergraph.alg :as alg]
            [ubergraph.core :as uber]))

(defn read-data
  [f]
  (->> f
       util/read-lines
       (map (partial re-seq #"\w{2}"))))

(defn create-net
  "Create a hash map of each node's neighbours"
  [conns]
  (reduce (fn [net [a b]]
            (-> net
                (assoc a (conj (get net a #{}) b))
                (assoc b (conj (get net b #{}) a))))
          {}
          conns))

(defn find-subnets
  "Find all subnets of size 3 in a graph where graph is a map of sets"
  [graph]
  (->> (for [[v1 neighbors] graph
             v2 neighbors
             :when (neg? (compare v1 v2))  ; Ensure v1 < v2 lexicographically
             :let [common-neighbors (set/intersection neighbors (get graph v2))]
             v3 common-neighbors
             :when (neg? (compare v2 v3))] ; Ensure v2 < v3 lexicographically
         #{v1 v2 v3})
       (into #{})))

(defn find-maximal-cliques
  [conns]
  (-> (uber/graph)
      (uber/add-edges* conns)
      (alg/maximal-cliques)))

(defn part1
  [f]
  (let [data (read-data f)]
    (->> data
         create-net
         find-subnets
         (map #(apply str %))
         (util/count-if #(re-matches #"t.....|..t...|....t." %)))))

(defn part2
  [f]
  (let [conns (read-data f)]
    (->> conns
         find-maximal-cliques
         (apply max-key count)
         (apply sorted-set)
         (str/join ","))))

(comment
  (def testf "data/day23-test.txt")
  (def inputf "data/day23-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 testf)
  (part2 inputf))

;; The End