(ns aoc24.day02
  (:require [clojure.string :as str]
            [aoc24.util :as util]))

(def testf "data/day02-test.txt")
(def inputf "data/day02-input.txt")

(defn read-data
  "Read and process the lines of data"
  [f]
  (->> f
       util/read-lines
       (mapv #(str/split % #" "))
       (util/mapmap Integer/parseInt)))

(defn is-safe?
  "A collection is safe if it's either monotonically increasing or decreasing
   and the difference is no more than 3"
  [coll]
  (let [deltas (map - (rest coll) (butlast coll))]
    (and (or (every? pos-int? deltas)
             (every? neg-int? deltas))
         (every? #(<= (abs %) 3) deltas))))

(defn part1
  [f]
  (->> f
       read-data
       (util/count-if is-safe?)))

(comment
  (part1 testf)
  (part1 inputf)
  
  (part2 testf))