(ns aoc24.day11
  (:require [clojure.string :as str]
            [clojure.edn :as edn]))

(defn read-data
  [f]
  (->> (str/split (slurp f) #"\s+")
       (map edn/read-string)))

(defn blink
  [x]
  (let [strx (str x)
        mid (/ (count strx) 2)]
    (cond
      (zero? x) 1
      (even? (count strx)) (list (Integer/parseInt (subs strx 0 mid))
                                 (Integer/parseInt (subs strx mid)))
      :else (* x 2024))))

(defn blink-coll
  "n iterations of a blink"
  [n coll]
  (reduce (fn [coll' _]
            (->> coll' 
                 (map blink) 
                 flatten))
          coll
          (range n)))

(defn part1
  [f]
  (let [data (read-data f)]
    (->> data
         (blink-coll 25)
         count)))
    
(defn part2
  [f]
  (let [data (read-data f)]
    (->> data
         (blink-coll 25)
         count)))

(comment
  (def testf "data/day11-test.txt")
  (def inputf "data/day11-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 testf)
  #_(part2 inputf))

;; The End
