(ns aoc24.day25
  (:require [clojure.string :as str]
            [aoc24.util :as util]))

(defn get-size
  "Count the number of hash characters in a string"
  [s]
  (->> s
       (filter #(= \# %))
       count))

(defn read-data
  [f]
  (let [blocks (-> f slurp (str/split #"\n\n"))
        schems (map str/split-lines blocks)
        locks (->> schems
                   (filter #(= (first %) "#####"))
                   (map util/T)
                   (util/mapmap get-size))
        keys (->> schems
                  (filter #(= (first %) "....."))
                  (map util/T)
                  (util/mapmap get-size))]
    [locks keys]))

(defn check-fit?
  [lock key]
  (every? #(<= % 5) (map + lock key)))

(defn find-fit
  [locks keys]
  (for [i (range (count locks))
        j (range (count keys))
        :when (check-fit? (locks i) (keys j))]
    [i j]))

(defn part1
  [f]
  (->> f
       read-data
       (apply find-fit)
       count))

(comment
  (def testf "data/day25-test.txt")
  (def inputf "data/day25-input.txt")
  
  (part1 testf)
  (part1 inputf))

;; The End