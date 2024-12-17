(ns aoc24.day15 
  (:require
   [aoc24.util :as util]
   [clojure.string :as str]))

(defn- get-robot-locn [warehouse]
  (first (util/mat-find-all (map #(str/split % #"") warehouse) "@")))

(defn read-data
  [f]
  (let [data (util/read-lines f)
        warehouse (->> data
                       (take-while #(pos-int? (count %)))
                       vec)
        moves (->> data
                   (drop-while #(pos-int? (count %)))
                   (str/join "")
                   #_(map move-dir))
        robot (get-robot-locn warehouse)]
    {:warehouse warehouse
     :moves moves
     :robot robot}))

(defn transpose
  "Transpose a vector of strings"
  [grid]
  (mapv str/join (util/T grid)))

(defn right
  [str]
  (str/replace str #"(.*)@(O*)\." "$1.@$2"))

(defn left
  [str]
  (str/replace str #"\.(O*)@(.*)" "$1@.$2"))

(defn move-robot
  [{:keys [robot] :as st} dir]
  (let [[r c] robot
        st' (case dir
              \> (update-in st [:warehouse r] right)
              \< (update-in st [:warehouse r] left)
              \^ (-> st
                     (update :warehouse transpose)
                     (update-in [:warehouse c] left)
                     (update :warehouse transpose))
              \v (-> st
                     (update :warehouse transpose)
                     (update-in [:warehouse c] right)
                     (update :warehouse transpose))
              :else st)]
    (assoc st' :robot (get-robot-locn (:warehouse st')))))

(defn score-boxes
  [grid]
  (let [coords (util/mat-find-all (map #(str/split % #"") grid) "O")]
    (reduce (fn [acc [r c]]
              (+ acc (+ (* 100 r) c)))
            0
            coords)))

(defn part1
  [f]
  (let [{:keys [moves] :as st} (read-data f)]
    (->> moves
         (reduce move-robot st)
         (:warehouse)
         score-boxes)))

(comment
  (def pp clojure.pprint/pprint)
  (def test1f "data/day15-test1.txt")
  (def test2f "data/day15-test2.txt")
  (def inputf "data/day15-input.txt")
  (part1 test1f)
  (part1 test2f)
  (part1 inputf))

;; The End