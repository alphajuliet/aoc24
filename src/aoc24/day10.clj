(ns aoc24.day10
  (:require [aoc24.util :as util]
            [clojure.string :as str]
            [clojure.core.matrix :as m]
            [ubergraph.core :as uber]
            [ubergraph.alg :as alg]))

(defn read-data
  [f]
  (->> f
       util/read-lines
       (map #(str/split % #""))
       (util/mapmap Integer/parseInt)
       (m/matrix)))

(defn get-reachable-neighbours
  [mat rc]
  (let [nn [[-1 0] [0 -1] [0 1] [1 0]]
        coords (map (partial mapv + rc) nn)
        v (apply m/mget mat rc)]
    (->> coords
         (filter #(= (util/safe-mget mat %) (inc v))))))
         
(defn to-graph
  "Read the matrix into a directed graph. Edges are between adjacent cells with a difference of 1."
  [mat]
  (let [g (uber/digraph)
        [rmax cmax] (m/shape mat)]
    (->> (for [i (range rmax)
               j (range cmax)]
           (->> (get-reachable-neighbours mat [i j])
                (map #(vector [i j] %))))
         (reduce uber/add-edges* g))))

(defn all-trails
  [m]
  (let [starts (util/mat-find-all m 0)
        ends (util/mat-find-all m 9)
        g (to-graph m)]
    (->> (for [start starts
               end ends]
            (alg/shortest-path g start end))
         (filter some?)
         (group-by alg/start-of-path))))
        
(defn part1
  [f]
  (let [data (read-data f)]
    (->> data
         all-trails
         vals
         (map count)
         (reduce +))))

(comment
  (def testf "data/day10-test.txt")
  (def inputf "data/day10-input.txt")
  (part1 testf)
  (part1 inputf)
  #_(part2 testf)
  #_(part2 inputf))

;; The End