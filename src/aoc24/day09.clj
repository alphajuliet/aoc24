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

(defn to-files
  "Convert the disk map into contiguous files"
  [disk-map]
  (loop [s disk-map
         disk []
         even? true
         id 0]
    (if (empty? s)
      disk
      ;; else
      (let [n (- (int (first s)) 48)    ; length of added list
            id' (if even? id -1)]         ; number to add
        (recur (subs s 1)
               (conj disk (list id' n))   ; id and size
               (not even?)
               (if even? (inc id) id))))))

(defn defrag-blocks
  [blocks]
  (loop [b blocks]
    (if (neg-int? (.indexOf b -1))
      b
      (let [i (.indexOf b -1)]
        (recur (-> b
                   (assoc i (last b))
                   pop))))))

(defn defrag-files
  "Part 2 algorithm"
  [files]
  (let []
    files))

(defn checksum
  [v]
  (->> (map * v (range (count v)))
       (reduce +)))
      
(defn part1
  [f]
  (let [data (read-data f)]
    (->> data
         to-disk
         defrag-blocks
         checksum)))

(defn part2
  [f]
  (let [data (read-data f)]
    (->> data
         to-files
         defrag-files
         #_checksum)))

(comment
  (def testf "data/day09-test.txt")
  (def inputf "data/day09-input.txt")
  (time (part1 testf))
  (time (part1 inputf))
  (part2 testf)
  (part2 inputf))
  
;; The End