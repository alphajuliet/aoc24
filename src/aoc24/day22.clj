(ns aoc24.day22
  (:require [aoc24.util :as util]
            [clojure.edn :as edn]
            [clojure.math :as math]))

(defn big-xor 
  "Crazy but true"
  [a b]
  (.xor (BigInteger. (str a)) (BigInteger. (str b))))

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
  (last (take (inc n) (iterate next-secret secret))))

(defn part1
  [f]
  (let [data (->> f
                  util/read-lines
                  (map edn/read-string))]
    (->> data
         (map (partial iterate-secrets 2000))
         (reduce +))))

(comment
  (def testf "data/day22-test.txt")
  (def inputf "data/day22-input.txt")
  (part1 testf)
  (part1 inputf)
  #_(part2 testf)
  #_(part2 inputf))