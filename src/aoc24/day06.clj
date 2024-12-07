(ns aoc24.day06
  (:require [aoc24.util :as util]))

(defn occurrences
  "Return the locations of all occurrences of the element in the collection"
  [elt coll]
  (keep-indexed #(when (= %2 elt) %1) coll))

(defn find-item
  "Find the locations of all occurrences of the item in the matrix"
  [lines item]
  (let [rows (count lines)
        hashes (mapv (fn [e] (mapv #(vector e %) (occurrences item (nth lines e))))
                     (range rows))]
    (apply concat hashes)))

(defn read-data
  "Return the data in a more useful format"
  [f]
  (let [lines (util/read-lines f)
        rows (count lines)
        cols (count (first lines))
        guard {:loc (first (find-item lines \^)) :dir [-1 0]}]
    {:dims [rows cols]
     :distance 0
     :trail #{guard}
     :obstacles (find-item lines \#)
     :guard guard}))

(defn turn-right
  [[dr dc]]
  [dc (- dr)])

(defn move-guard
  "Move the guard one step and return the new state"
  [{:keys [obstacles guard] :as state}]
  (let [next-loc (mapv + (:loc guard) (:dir guard))]
    (if (some #(= next-loc %) obstacles)
      (update-in state [:guard :dir] turn-right)
      ;; else
      (-> state
          (assoc-in [:guard :loc] next-loc)
          (update :trail conj {:loc next-loc :dir (:dir guard)})
          (update :distance inc)))))

(defn traverse-room
  "Traverse the room until we leave or run out of time"
  [{:keys [dims] :as state}]
  (reduce (fn [st _]
            (let [st' (move-guard st)
                  [rmax cmax] dims
                  [r c] (get-in st' [:guard :loc])]
              (if (or (neg-int? r) (neg-int? c)
                      (>= r rmax) (>= c cmax))
                (reduced st')
                st')))
          state
          (range 10000)))

(defn add-obstacles 
  "Find all the potential locations for obstacles that force the guard into a loop.
   => find where the trail intersects with a 90º difference in direction"
  [state]
  (let [{:keys [trail]} state]
    (group-by :loc trail)))
    
(defn part1
  [f]
  (->> f
       read-data
       traverse-room
       (:trail)
       (map :loc)
       set
       count
       dec))

(defn part2
  [f]
  (->> f
       read-data
       traverse-room
       add-obstacles))

(comment
  (def testf "data/day06-test.txt")
  (def inputf "data/day06-input.txt")

  (part1 testf)
  (part1 inputf)
  (part2 testf))

;; The End