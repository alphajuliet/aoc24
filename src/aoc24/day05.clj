(ns aoc24.day05
  (:require [aoc24.util :as util]
            [clojure.string :as str]))

(defn read-data
  "Read and format the data"
  [f]
  (let [data (util/read-lines f)
        rules (->> data
                   (take-while #(pos-int? (count %)))
                   (map #(str/split % #"\|"))
                   (util/mapmap Integer/parseInt))
        updates (->> data
                     (drop-while #(pos-int? (count %)))
                     rest
                     (map #(str/split % #","))
                     (util/mapmap Integer/parseInt))]
    {:rules rules, :updates updates}))

(defn create-poset
   "Create a partially ordered set from the rules provided"
   [rules]
   (reduce (fn [acc [k v]]
             (update acc k #(conj % v)))
           {}
           rules))

(defn is-ordered?
  "Is a > b according to the poset?"
  [poset [a b]]
  (if (contains? poset a)
    (util/coll-contains? (vec (get poset a)) b)
    false))

(defn is-ordered-coll?
  "Is the collection ordered according to the poset?"
  [poset coll]
  (let [pairs (partition 2 1 coll)]
    (->> pairs
         (map #(is-ordered? poset %))
         (every? true?))))

(defn get-middle-element
  [coll]
  (let [median (/ (count coll) 2)]
    (nth coll (int median))))

(defn part1
  [f]
  (let [{:keys [rules updates]} (read-data f)
        poset (create-poset rules)]
    (->> updates
         (filter #(is-ordered-coll? poset %))
         (map get-middle-element)
         (reduce +))))
         
(comment
  (def testf "data/day05-test.txt")
  (def inputf "data/day05-input.txt")
  
  (part1 testf)
  (part1 inputf))