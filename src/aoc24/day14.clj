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
    :position #(vector %2 %1) ;; Convert from [x y] to [r c]
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

(defn safety-score
  [dims robots]
  (let [midp (mapv #(math/floor (/ % 2.0)) dims)]
    (->> robots
         (map first)
         (keep #(to-quadrant midp %))
         frequencies
         vals
         (apply *))))

(defn move-robots-until-egg
  [dims robots]
  (reduce (fn [st t] 
            (let [st' (map #(move dims %) st)]
              ;; (if (< (x-variance st') 600)
              (if (and (> t 6000) (< (safety-score dims st) 60000000))
                (reduced t)
                st')))
          robots
          (range 20000)))
          
(defn to-quadrant
  [[r-mid c-mid] [r c]]
  (cond
    (and (< r r-mid) (< c c-mid)) 1
    (and (< r r-mid) (> c c-mid)) 2
    (and (> r r-mid) (> c c-mid)) 3
    (and (> r r-mid) (< c c-mid)) 4
    :else nil))

(defn safety-score
  [dims robots]
  (let [midp (mapv #(math/floor (/ % 2.0)) dims)]
    (->> robots
         (map first)
         (keep #(to-quadrant midp %))
         frequencies
         vals
         (apply *))))

(defn part1
  [f dims]
  (let [data (read-data f)
        midp (mapv #(math/floor (/ % 2.0)) dims)]
    (->> data
         (move-robots 100 dims)
         (safety-score dims))))

(defn part2
  [f dims]
  (->> (read-data f)
       (move-robots-until-egg dims)))

(comment
  (def testf "data/day14-test.txt")
  (def inputf "data/day14-input.txt")
  (part1 testf [7 11])
  (part1 inputf [103 101])
  (part2 inputf [103 101]))
  
;; The End