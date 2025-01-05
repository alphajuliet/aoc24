(ns aoc24.day22
  (:require [aoc24.util :as util]
            [clojure.edn :as edn]
            [clojure.math :as math]))

(defn big-xor 
  "Crazy but true"
  [a b]
  (.xor (BigInteger. (str a)) (BigInteger. (str b))))

(defn read-data [f]
  (->> f
       util/read-lines
       (map edn/read-string)))

(defn next-secret
  "Calculate the next secret number via the algorithm"
  [secret]
  (let [a (-> secret
              (* 64N)
              (big-xor secret)
              (mod 16777216))
        b (-> a
              (math/floor-div 32)
              (big-xor a)
              (mod 16777216))]
    (-> b
        (* 2048N)
        (big-xor b)
        (mod 16777216))))

(defn iterate-secrets
  [n secret]
  (->> secret
       (iterate next-secret)
       (take (inc n))))

(defn find-opt-seq
  [secret]
  (let [ones (->> secret
                  (iterate-secrets 2000)
                  (map #(mod (int %) 10)))
        delta (map - (rest ones) (butlast ones))]
    delta))

(defn part1
  [f]
  (let [numbers (read-data f)]
    (transduce
     (map (comp last (partial iterate-secrets 2000)))
     +
     numbers)))

(defn part2
  [f]
  (let [numbers (read-data f)
        ones (->> numbers
                  (map find-opt-seq))]
    ones))

(comment
  (def testf "data/day22-test.txt")
  (def inputf "data/day22-input.txt")
  (part1 testf)
  (time (part1 inputf))
  (time (part2 testf))
  #_(part2 inputf))