(ns aoc24.day01
  (:require [clojure.string :as str]
            [aoc24.util :as util]))

(def testf "data/day01-test.txt")
(def inputf "data/day01-input.txt")

(defn create-lists
  "Create two sorted lists from the two columns"
  [lines]
  (->> lines
       (mapv #(str/split % #"\s+"))
       util/T
       (map #(mapv Integer/parseInt %))
       (mapv sort)))

(defn part1
  [f]
  (let [[c1 c2] (->> f
                     util/read-lines
                     create-lists)]
    (->> (mapv #(abs (- %1 %2)) c1 c2)
         (apply +))))
       
(comment
  (part1 testf)
  (part1 inputf))

;; The End
