(ns aoc24.day14
  (:require [clojure.edn :as edn]
            [clojure.math :as math]
            [instaparse.core :as insta]))

(def parse-robots
  (insta/parser 
   "<robots> := robot+
    robot := position <space> velocity <nl>
    position := <'p='> coord
    velocity := <'v='> coord
    <coord> := number <','> number
    number := #'-?\\d+'
    <space> := #'\\s+'
    <nl> := '\\n'"))

(defn transform-rules
  [data]
  (insta/transform
   {:robot vector
    :position #(vector %2 %1)
    :velocity #(vector %2 %1)
    :number edn/read-string}
   data))

(defn read-data
  [f]
  (->> f
       slurp
       parse-robots
       transform-rules))

(defn move
   "Move a robot one step"
   [dims [_ vel :as state]]
   (update state 0 (fn [pos] (as-> pos <>
                               (mapv + vel <>)
                               (mapv mod <> dims)))))

(defn move-robots
  "Move robots for n steps"
  [n dims robots]
  (reduce (fn [st _] 
            (map #(move dims %) st))
          robots
          (range n)))
          
(defn to-quadrant
  [[r-mid c-mid] [r c]]
  (cond
    (and (< r r-mid) (< c c-mid)) 1
    (and (< r r-mid) (> c c-mid)) 2
    (and (> r r-mid) (> c c-mid)) 3
    (and (> r r-mid) (< c c-mid)) 4
    :else nil))

(defn part1
  [f dims]
  (let [data (read-data f)
        midp (mapv #(math/floor (/ % 2.0)) dims)]
    (->> data
         (move-robots 100 dims)
         (map first)
         (keep #(to-quadrant midp %))
         frequencies
         vals
         (apply *))))

(comment
  (def testf "data/day14-test.txt")
  (def inputf "data/day14-input.txt")
  (part1 testf [7 11])
  (part1 inputf [103 101])
  #_(part2 testf)
  #_(part2 inputf))
  
;; The End