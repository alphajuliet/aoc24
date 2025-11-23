(ns aoc24.day21 
  (:require
   [aoc24.util :as util]
   [clojure.math.combinatorics :as combo]
   [clojure.string :as str]
   [ubergraph.alg :as alg]
   [ubergraph.core :as uber]))

(defn append-to
  "Append s1 to s2"
  [s1 s2]
  (str s2 s1))

(defn read-data 
  [f]
  (->> f
       util/read-lines))

(defn numeric-keypad
  "Create a directed graph of the numeric keypad"
  []
  (-> (uber/digraph)
      (uber/add-edges
       ["7" "8" {:move ">"}] ["8" "9" {:move ">"}]
       ["8" "7" {:move "<"}] ["9" "8" {:move "<"}]
       ["7" "4" {:move "v"}] ["8" "5" {:move "v"}] ["9" "6" {:move "v"}]
       ["4" "7" {:move "^"}] ["5" "8" {:move "^"}] ["6" "9" {:move "^"}]
       ["4" "5" {:move ">"}] ["5" "6" {:move ">"}]
       ["5" "4" {:move "<"}] ["6" "5" {:move "<"}]
       ["4" "1" {:move "v"}] ["5" "2" {:move "v"}] ["6" "3" {:move "v"}]
       ["1" "4" {:move "^"}] ["2" "5" {:move "^"}] ["3" "6" {:move "^"}]
       ["1" "2" {:move ">"}] ["2" "3" {:move ">"}]
       ["2" "1" {:move "<"}] ["3" "2" {:move "<"}]
       ["2" "0" {:move "v"}] ["3" "A" {:move "v"}]
       ["0" "2" {:move "^"}] ["A" "3" {:move "^"}]
       ["0" "A" {:move ">"}]
       ["A" "0" {:move "<"}])))

(defn directional-keypad
  "Create a directed graph of the directional keypad"
  []
  (-> (uber/digraph)
      (uber/add-edges
       ["^" "A" {:move ">"}]
       ["A" "^" {:move "<"}]
       ["^" "v" {:move "v"}] ["A" ">" {:move "v"}]
       ["v" "^" {:move "^"}] [">" "A" {:move "^"}]
       ["<" "v" {:move ">"}] ["v" ">" {:move ">"}]
       ["v" "<" {:move "<"}] [">" "v" {:move "<"}])))

(defn optimise-path
  "Optimise the path by putting similar symbols together if possible"
  [start end path]
  (cond
    (and (= start "5") (= end "A")) "vv>"
    (and (= start "4") (= end "A")) ">>vv"
    (and (= start "A") (= end "8")) "^^^<"
    (and (= start "8") (= end "A")) "vvv>"
    (and (= start "A") (= end "1")) "^<<"
    (and (= start "A") (= end "4")) "^^<<"
    (and (= start "6") (= end "7")) "<<^"   ; !!
    (and (= start "1") (= end "A")) ">>v"   ; !!
    (and (= start "2") (= end "9")) "^^>"
    (and (= start "3") (= end "7")) "<<^^"
    
    (and (= start "A") (= end "<")) "v<<"  ; !!

    (= path "^<^") "<^^"
    (= path "^>^") ">^^"
    (= path "v>v") ">vv"
    (= path "v>v") ">vv"
    (= path ">^>") ">>^" ; !!
    (= path ">v>") ">>v"
    (= path "<^<") "<<^"
    (= path "<v<") "<<v"

    ;; (= path "^<<") "<<^"
    ;; (= path ">>v") "v>>"

    (= path "v>") ">v"
    (= path "^<") "<^" ; !!
    ;; (= path "v<") "<v"
    :else path))

(defn key-presses
  "Find the key presses to go from start to end. Prefer similar symbols together."
  [g start end]
  (->> (alg/shortest-path g start end)
       (alg/edges-in-path)
       (map (partial uber/edge-with-attrs g))
       (map #(nth % 2))
       (map :move)
       (str/join)
       (optimise-path start end)))

(defn edges->keys
  "Convert a list of nodes into the corresponding key presses."
  ;; edges->keys : Graph -> List String -> String 
  [g node-list]
  (->> node-list
       (util/nodes->edges g)
       (map (partial uber/edge-with-attrs g))
       (map #(nth % 2))
       (map :move)
       str/join
       (append-to "A")))

(defn all-key-presses
  "Find all the possible key presses to traverse the keys from start to end"
  ;; all-key-presses : Graph -> String -> String -> List String 
  [g start end]
  (->> (util/all-shortest-paths g start end)
       (map (partial edges->keys g))))

(defn all-key-sequences
  "Find the shortest key sequences through all the keys in the code.
   Add a start node, break into pairs, convert to strings, find all the keypresses for each pair,
   take the cartesian product of all the paths, and join into strings." 
  ;; all-key-sequences : Graph -> String -> List String
  [g code]
  (->> code
     (str "A")
     (partition 2 1)
     (util/mapmap str)
     (map #(all-key-presses g (first %) (second %)))
     (apply combo/cartesian-product)
     (map str/join)))
         
(defn key-sequence
  "Find the path to all the keys"
  ;; key-sequence :: Graph -> String -> String
  [g codes]
  (let [pairs (util/mapmap str (partition 2 1 codes))]
    (->> pairs
         (map #(key-presses g (first %) (second %)))
         (str/join "A")
         (append-to "A"))))

(defn apply-remote
  "Remotely drive the numeric keypad through the directional keypads"
  [code]
  (let [n (numeric-keypad)
        d (directional-keypad)]
    (->> code
         (str "A")
         (key-sequence n)
         (str "A")
         (key-sequence d)
         (str "A")
         (key-sequence d))))

(defn test-seq 
  [code]
  (let [d (directional-keypad)]
    (->> code
         (str "A")
         (key-sequence d)
         (str "A")
         (key-sequence d)
         count)))
;;=> #'aoc24.day21/apply-remote
(defn complexity
  [code key-sequence]
  (let [n (->> code
               butlast
               (apply str)
               Integer/parseInt)]
    #_[(count key-sequence) n]
    (* n (count key-sequence))))

(defn part1
  [f]
  (let [codes (read-data f)
        presses (map apply-remote codes)]
    (->> presses
         (map complexity codes)
         (reduce +))))

(comment
  (def testf "data/day21-test.txt")
  (def inputf "data/day21-input.txt")
  (part1 testf)
  (part1 inputf)
  ;; not: 295616, 292932, 289060, 281968
  #_(part2 testf)
  #_(part2 inputf))

;; The End