(ns aoc24.day24
  (:require [aoc24.util :as util]
            [clojure.string :as str]))

(defn swap [f a b] (f b a))

(defn read-value
  "Read the initial value into a vector 2-tuple"
  [line]
  (let [[name value] (str/split line #" ")]
     [(apply str (butlast name))
      {:type "INPUT" :value (Integer/parseInt value)}]))

(defn read-gate
  [line]
  (let [[in1 op in2 _ out] (str/split line #" ")]
    [out {:type op :inputs [in1 in2]}]))

(defn read-data
  "Create the logic circuit"
  [f]
  (let [data (util/read-lines f)
        inputs (->> data
                    (take-while #(pos-int? (count %)))
                    (map read-value)
                    (into {}))
        gates (->> data
                   (drop-while #(pos-int? (count %)))
                   rest
                   (map read-gate)
                   (into {}))]
    (into gates inputs)))

(def operations
  "Define the logic operations"
  {"AND" #(every? pos-int? %)
   "OR"  #(some pos-int? %)
   "XOR" #(odd? (count (filter pos-int? %)))})

(defn evaluate-circuit
  "Evaluates a circuit where input values are embedded in the circuit definition"
  [circuit]
  (let [cache (atom {})]
    (letfn [(eval-node [node-id]
              (if-let [cached (@cache node-id)]
                cached
                (let [node (get circuit node-id)
                      result (if (= "INPUT" (:type node))
                               (:value node)
                               (let [op (operations (:type node))
                                     input-vals (map eval-node (:inputs node))]
                                 (if (op input-vals) 1 0)))]
                  (swap! cache assoc node-id result)
                  result)))]
      (into {} (for [[id _] circuit]
                 [id (eval-node id)])))))

(defn bin->dec
  [bits]
  (reduce (fn [acc bit]
            (+ (* 2 acc) bit))
          0 
          bits))

(defn part1
  [f]
  (let [circuit (read-data f)
        outputs (filter #(str/starts-with? % "z") (keys circuit))]
    (->> circuit
         evaluate-circuit
         (swap select-keys outputs)
         (into (sorted-map))
         vals
         reverse
         bin->dec)))

(comment
  (def test1f "data/day24-test1.txt")
  (def test2f "data/day24-test2.txt")
  (def inputf "data/day24-input.txt")
  
  (part1 test1f)
  (part1 test2f)
  (part1 inputf))

;; The End