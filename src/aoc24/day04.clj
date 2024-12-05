(ns aoc24.day04
  (:require [aoc24.util :as util] 
            [clojure.string :as str]
            [clojure.core.matrix :as m]))

(defn read-data
  [f]
  (->> f
       util/read-lines
       (map #(str/split % #""))
       m/matrix))

(defn find-all
  "Find all the indices of a in mat"
  [mat a]
  (for [[r c] (m/index-seq mat)
        :when (= a (m/mget mat r c))]
    [r c]))

(defn get-string
  "Get the string in a given direction from the starting position"
  [m [r c] [dr dc]]
  (reduce (fn [acc i]
            (let [r' (+ r (* i dr))
                  c' (+ c (* i dc))
                  [rmax cmax] (m/shape m)]
              (if (and (<= 0 r' (dec rmax))
                       (<= 0 c' (dec cmax)))
                (str acc (m/mget m r' c'))
                acc)))
          ""
          (range 4)))

(defn get-all-strings
  "Return the 4-letter strings in every direction from the starting point. Return nil if they
   exceed the boundaries"
  [m rc]
  (let [dirs [[-1 -1] [-1 0] [-1 1]
              [0 -1] [0 1]
              [1 -1] [1 0] [1 1]]]
    (->> dirs
         (map #(get-string m rc %))
         (filter #(= "XMAS" %)))))

(defn part1
  [f]
  (let [m (read-data f)]
    (->> (find-all m "X")
         (map #(get-all-strings m %))
         flatten
         count)))
    
(comment
  (def testf "data/day04-test.txt")
  (def test0f "data/day04-test0.txt")
  (def inputf "data/day04-input.txt")

  (time (part1 testf))
  (time (part1 inputf)))
  
;; The End