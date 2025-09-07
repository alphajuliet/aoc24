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
               (conj disk [id' n])   ; id and size
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

(defn next-free
  "Find the next free block from the left"
  [fs a]
  (loop [i a]
    (let [i' (inc i)]
      (cond
        (= i' (count fs)) a
        (= -1 (first (nth fs i'))) i'
        :else (recur i')))))

(defn prev-block
  "Find the next block from the right"
  [fs b]
  (loop [i b]
    (let [i' (dec i)]
      (cond
        (neg-int? i') b
        (nat-int? (first (nth fs i'))) i'
        :else (recur i')))))

(defn step
  "Execute one step on the file defrag process:
   - get the first free block at the start (at location a')
   - get the next available block from the end (at location b')
   - move it if it fits
   - consolidate the empty space if needed"
  [{:keys [fs a b] :as state}]
  (let [a' (next-free fs a)
        b' (prev-block fs b)
        st' (assoc state :a a' :b b')
        [_ len-a] (nth fs a')
        [val-b len-b] (nth fs b')]
    (cond
      (<= b' a') state
      (<= len-b len-a) (-> st'
                           (assoc-in [:fs a' 0] val-b)
                           (assoc-in [:fs b' 0] -1))
      :else st')))


(defn defrag-files
  "File defrag algorithm"
  [{:keys [fs a b] :as state}]
  (step state))

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
  (let [fs (->> f
                read-data
                to-files)]
    (->> {:fs fs, :a 0, :b (dec (count fs))}
         defrag-files
         #_checksum)))

(comment
  (def testf "data/day09-test.txt")
  (def test2f "data/day09-test2.txt")
  (def inputf "data/day09-input.txt")
  (time (part1 testf))
  (time (part1 inputf))
  (part2 testf)
  (part2 inputf))
  
;;  The End