(ns aoc24.day20 
  (:require
   [aoc24.util :as util]
   [clojure.string :as str]))

(defn dims
  "Return the number of rows and columns"
  [grid]
  [(count grid) (count (first grid))])

(defn read-data
  "Return the grid as a matrix"
  [f]
  (->> f
       util/read-lines
       (mapv #(str/split % #""))))

(defn neighbours
  [grid [r c]]
  (filter (fn [[r' c']]
            (not= "#" (get-in grid [r' c'])))
            [[(dec r) c] [(inc r) c] [r (dec c)] [r (inc c)]]))

(defn traverse-track
  "Traverse the track and record the path"
  [grid]
  (let [start (first (util/mat-find-all grid "S"))
        end (first (util/mat-find-all grid "E"))]
    (loop [current start
           visited #{}
           path []
           i 0]
      (if (= current end)
        (conj path current)
        (let [nn (neighbours grid current)
              ;; Find the neighbour that has not been visited
              next-rc (first (filter #(not (visited %)) nn))]
          #_(println next-rc)
          (if (or (> i 10000)
                  (nil? next-rc)) 
            path
            (recur next-rc
                   (conj visited current)
                   (conj path current)
                   (inc i))))))))

(defn adjacent?
  "Is there an adjacent path cell on the other side of the wall in the given direction?"
  [grid [r c] [dr dc]]
  (let [[rmax cmax] (dims grid)]
    (and (= "#" (get-in grid [(+ r dr) (+ c dc)]))
         (<= 0 (+ r dr dr) (dec rmax))
         (<= 0 (+ c dc dc) (dec cmax))
         (not= "#" (get-in grid [(+ r dr dr) (+ c dc dc)])))))

(defn adjacents
  "Find adjacent path cells on the other side of the wall."
  [grid [r c]]
  (let [dirs [[-1 0] [0 -1] [0 1] [1 0]]]
    (->> dirs
         (filter (partial adjacent? grid [r c]))
         (map #(mapv + [r c] % %)))))

(defn path-saving
  "Calculate the saving by taking a shortcut to the adjacent cell"
  [path current adjacent]
  (let [i (.indexOf path current)
        j (.indexOf path adjacent)]
    (- j i 2)))

(defn find-adjacents
  "Find all the savings"
  [grid path [r c]]
  (let [aa (adjacents grid [r c])]
   (map (partial path-saving path [r c]) aa)))

(defn part1
  [n f]
  (let [grid (read-data f)
        path (traverse-track grid)]
    (->> path
         (map (partial find-adjacents grid path))
         flatten
         (filter #(>= % n))
         frequencies
         vals
         (reduce +))))

(comment
  (def testf "data/day20-test.txt")
  (def inputf "data/day20-input.txt")
  (part1 0 testf)
  (part1 100 inputf))

;; The End