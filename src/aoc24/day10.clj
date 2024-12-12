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
        v (apply m/mget mat rc)]
    (->> nn
         (map (partial mapv + rc))
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

(defn all-shortest-trails
  "Find all the shortest path from the 0s to the 9s, grouped by trailheads"
  [m]
  (let [starts (util/mat-find-all m 0)
        ends (util/mat-find-all m 9)
        g (to-graph m)]
    (->> (for [start starts
               end ends]
           (alg/shortest-path g start end))
         (filter some?) ; filter out the nils
         (group-by alg/start-of-path))))

(defn all-paths
  "Find all paths from start to end in a directed graph."
  [g start end]
  (letfn [(path-finder [current-path]
            (let [current-node (last current-path)]
              (if (= current-node end)
                [current-path]
                (->> (uber/successors g current-node)
                     (remove (set current-path))  ; Prevent cycles
                     (mapcat #(path-finder (conj current-path %)))
                     seq))))]
    (path-finder [start])))

(defn all-trails
  [m]
  (let [starts (util/mat-find-all m 0)
        ends (util/mat-find-all m 9)
        g (to-graph m)]
    (->> (for [start starts
               end ends]
           (all-paths g start end))
         (filter some?)
         (apply concat)
         (group-by first))))

(defn part1
  [f]
  (->> f
       read-data
       all-shortest-trails
       vals
       (map count)
       (reduce +)))

(defn part2
  [f]
  (->> f
       read-data
       all-trails
       vals
       (map count)
       (reduce +)))

(comment
  (def testf "data/day10-test.txt")
  (def inputf "data/day10-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 testf)
  (part2 inputf))

;; The End