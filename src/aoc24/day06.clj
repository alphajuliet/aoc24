(ns aoc24.day06
  (:require [aoc24.util :as util]
            [clojure.set :as set]))

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
    ;;  :distance 0
     :trail (vector guard)
     :obstacles (find-item lines \#)
     :guard guard
     :exit-status :ok}))

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
          (update :trail conj {:loc next-loc :dir (:dir guard)})))))

(defn traverse-room
  "Traverse the room until we leave, run out of time, or revisit any location+direction"
  [state]
  (reduce (fn [st _]
            (let [st' (move-guard st)
                  [rows cols] (:dims st') 
                  current-loc-dir (:guard st')
                  [r c] (:loc current-loc-dir)]
              (cond 
                (or (neg-int? r) (neg-int? c) (>= r rows) (>= c cols))
                  (-> st'
                      (assoc :exit-status :escaped)
                      reduced)
                (some #{current-loc-dir} (butlast (:trail st')))
                  (-> st'
                      (assoc :exit-status :looped)
                      reduced)
                :else st')))
          state
          (range 10000)))

(defn- test-for-loop
  "Test for a loop from turning right at step i in the trail. Don't repeat an existing obstacle."
  [state i]
  (let [{:keys [loc dir]} (nth (:trail state) i)
        [maxr maxc] (:dims state)
        [obs-r obs-c :as new-obs] (mapv + loc dir)
        new-trail (vec (take (inc i) (:trail state)))]
    (if (or (neg-int? obs-r) (neg-int? obs-c)
            (>= obs-r maxr) (>= obs-c maxc)
            (some #{new-obs} (map :loc new-trail)) 
            (some #{new-obs} (:obstacles state)))
      nil
      (-> state
          (assoc :trail new-trail) ;; truncate trail to the current position
          (update :obstacles conj new-obs) ;; add an obstacle
          (assoc :guard {:loc loc :dir dir})
          traverse-room))))

(defn find-loops
  "Given the guard's trail, at each step check if turning right and traversing the room 
   results in revisiting a location and direction"
  [state]
  (for [i (range 2 (dec (count (:trail state))))
        :let [st (test-for-loop state i)]
        :when (not (nil? st))
        :when (= :looped (:exit-status st))]
    (nth (:trail st) i)))

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
       find-loops
       count))

(comment
  (def testf "data/day06-test.txt")
  (def test2f "data/day06-test2.txt")
  (def test4f "data/day06-test4.txt")
  (def inputf "data/day06-input.txt")
  (part1 testf)
  (part1 test2f)
  (time (part1 inputf))
  (time (part2 testf))
  (part2 test2f)
  (part2 test4f)
  (time (part2 inputf)))
;; 371, 372, 373 are too low!
;; 1549, 1738 are incorrect

;; The End