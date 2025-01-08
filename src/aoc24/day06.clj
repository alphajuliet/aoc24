(ns aoc24.day06
  (:require
   [aoc24.util :as util]
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
     :distance 0
     :trail (vector guard)
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

(defn extend-segment
  "Extend the segment forwards and backwards until it hits an obstacle coordinate or the edge of the room, 
   and return the new start and end locations"
  [[rmax cmax] obstacles {:keys [start end dir] :as segment}]
  (let [start' (take-while (fn [[r c :as loc]]
                             (and (<= 0 r rmax) (<= 0 c cmax)
                                  (not (some #(= % loc) obstacles))))
                           (iterate #(mapv - % dir) start))
        end' (take-while (fn [[r c :as loc]]
                           (and (<= 0 r rmax) (<= 0 c cmax)
                                (not (some #(= % loc) obstacles))))
                         (iterate #(mapv + % dir) end))]
    (assoc segment :a (last start') :z (last end'))))

(defn find-vectors
  "Find all the segments and their extensions"
  [{:keys [dims obstacles trail]}]
  (let [segments (partition-by :dir (butlast trail))]
    (for [s segments
          :let [seg {:start (:loc (first s))
                     :end (:loc (last s))
                     :dir (:dir (first s))}]]
      (extend-segment (mapv dec dims) obstacles seg))))

(defn all-coords
  "List all the coords from start to end exclusive, in the given direction"
  [start end dir]
  (take-while #(not= % end)
              (iterate #(mapv + % dir) start)))

(defn find-intersection
  "Return any intersection between the segment and the vector in the right directions"
  [s v]
  (let [[dr dc :as dir1] (:dir s)
        dir2 (:dir v)
        intersect (set/intersection (set (all-coords (:start s) (:end s) dir1))
                                    (set (all-coords (:a v) (:z v) dir2)))]
    (when (and intersect (= dir2 [dc (- dr)]))
      (first intersect))))

(defn find-intersections
  "Find all the intersections of a segment between :start and :end and previous vector between :a and :z,
   and return the location of the new obstacle."
  [segments]
  (for [v (range (count segments))
        s (range (inc v) (count segments))
        :when (< v s)
        :let [intersect (find-intersection (nth segments s) (nth segments v))]
        :when intersect]
    (mapv + (:dir (nth segments s)) intersect)))

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
  (let [result (->> f 
                    read-data 
                    traverse-room)]
    (->> result
         find-vectors
         find-intersections
         count)))

(comment
  (def testf "data/day06-test.txt")
  (def inputf "data/day06-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 testf)
  (part2 inputf))
;; 371 is too low!

;; The End