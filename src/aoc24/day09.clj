(ns aoc24.day09
  (:require [clojure.string :as str]))

(defn read-data
  [f]
  (->> f
       slurp
       str/trim-newline))

(defn to-disk
  "Convert the disk map into the disk blocks"
  [disk-map]
  (loop [s disk-map
         disk []
         even? true
         id 0]
    (if (empty? s)
      disk
      ;; else
      (let [n (- (int (first s)) 48)  ; length of added list
            x (if even? id -1)]         ; number to add
        (recur (subs s 1)
               (into disk (repeat n x))
               (not even?)
               (if even? (inc id) id))))))

(defn defrag
  "One pass of the defrag algorithm"
  [v]
  (loop [u v]
    (if (neg-int? (.indexOf u -1))
      u
      (let [i (.indexOf u -1)]
        (recur (-> u
                   (assoc i (last u))
                   pop))))))

(defn checksum
  [v]
  (->> (map * v (range (count v)))
       (reduce +)))
      
(defn part1
  [f]
  (let [data (read-data f)]
    (->> data
         to-disk
         defrag
         checksum)))

(defn part2
  [f])

(comment
  (def testf "data/day09-test.txt")
  (def inputf "data/day09-input.txt")
  (time (part1 testf))
  (time (part1 inputf))
  (part2 testf)
  (part2 inputf))
  
;; The End