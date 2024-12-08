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
  [m [r c] [dr dc] n]
  (reduce (fn [acc i]
            (let [r' (+ r (* i dr))
                  c' (+ c (* i dc))
                  [rmax cmax] (m/shape m)]
              (if (and (<= 0 r' (dec rmax))
                       (<= 0 c' (dec cmax)))
                (str acc (m/mget m r' c'))
                acc)))
          ""
          (range n)))

(defn get-all-strings
  "Return the n-letter strings in every direction from the starting point. Return nil if they
   exceed the boundaries"
  [m rc n]
  (let [dirs [[-1 -1] [-1 0] [-1 1]
              [0 -1] [0 1]
              [1 -1] [1 0] [1 1]]]
    (->> dirs
         (map #(get-string m rc % n))
         (filter #(= "XMAS" %)))))

(defn safe-mget
  "Try and get a matrix value at a coord. Return nil if it fails."
  [m [r c]]
  (let [[rmax cmax] (m/shape m)]
    (if (and (<= 0 r (dec rmax))
             (<= 0 c (dec cmax)))
      (m/mget m r c)
      nil)))

(defn get-diagonal-letters
  "Get the diagonally adjacent letters, clockwise from top left"
  [m [r c]]
  (let [dirs [[-1 -1] [-1 1] [1 1] [1 -1]]]
    (->> dirs
         (map #(map + [r c] %))
         (map #(safe-mget m %)))))

(defn allowed?
  [coll]
  (let [s (str/join coll)]
    (or (= s "MMSS")
        (= s "MSSM")
        (= s "SMMS")
        (= s "SSMM"))))
         
(defn part1
  [f]
  (let [m (read-data f)]
    (->> (find-all m "X")
         (map #(get-all-strings m % 4))
         flatten
         count)))

(defn part2
  [f]
  (let [m (read-data f)]
    (->> (find-all m "A")
         (map #(get-diagonal-letters m %))
         (util/count-if allowed?))))
    
(comment
  (def pp clojure.pprint/pprint)

  (def testf "data/day04-test.txt")
  (def inputf "data/day04-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 testf)
  (part2 inputf))
  
;; The End