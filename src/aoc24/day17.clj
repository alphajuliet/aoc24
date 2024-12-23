(ns aoc24.day17
  (:require [clojure.edn :as edn]
            [clojure.math :as math]
            [clojure.string :as str]))

(defn read-data
  [f]
  (->> f
       slurp
       (re-seq #"\d+")
       (mapv edn/read-string)))

(defn init-state
  [[a b c & code]]
  {:a a, :b b, :c c, :ip 0, :code (vec code), :out []})

(defn fetch
  [{:keys [a b c] :as state} operand]
  (cond
    (<= 0 operand 3) operand
    (= 4 operand) a
    (= 5 operand) b
    (= 6 operand) c
    :else (throw (Exception. (str "Invalid operand: " operand)))))

(defn- jnz
  "Jump if not zero instruction"
  [{:keys [a] :as state} operand]
  (if (zero? a)
    (update state :ip + 2)
    (assoc state :ip operand)))

(defn exec
  "Execute one instruction"
  [{:keys [a b c ip code] :as state}]
  (let [opcode (get code ip)
        operand (get code (inc ip))]
    #_(println opcode operand)
    (case opcode
      ;; adv
      0 (-> state 
            (assoc :a (int (/ a (math/pow 2 (fetch state operand)))))
            (update :ip + 2))
      ;; bxl
      1 (-> state
            (update :b bit-xor operand)
            (update :ip + 2))
      ;; bst
      2 (-> state
            (assoc :b (mod (fetch state operand) 8))
            (update :ip + 2))
      ;; jnz
      3 (-> state 
            (jnz operand))
      ;; bxc
      4 (-> state
            (update :b bit-xor c)
            (update :ip + 2))
      ;;out
      5 (-> state 
            (update :out conj (mod (fetch state operand) 8))
            (update :ip + 2))
      ;;bdv
      6 (-> state 
            (assoc :b (int (/ a (math/pow 2 (fetch state operand)))))
            (update :ip + 2))
      ;; cdv
      7 (-> state 
            (assoc :c (int (/ a (math/pow 2 (fetch state operand)))))
            (update :ip + 2)) 
      :else (throw (Exception. (str "Invalid opcode: " opcode))))))

(defn execute-program
  "Execute the program in the codespace"
  [state0]
  (reduce 
   (fn [st _]
     (let [{:keys [ip code out] :as st'} (exec st)]
       (if (>= ip (count code))
         (reduced st')
         (do
           #_(println st')
           st'))))
   state0
   (range 1000)))

(defn part1
  [f]
  (let [program (read-data f)]
    (->> program
         init-state
         execute-program
         :out
         (str/join ","))))

(defn part2
  [f a]
  (let [state (->> f read-data init-state)
        code (:code state)]
    (-> state
        (assoc :a a)
        execute-program)))

(comment
  (def testf "data/day17-test.txt")
  (def test2f "data/day17-test2.txt")
  (def test3f "data/day17-test3.txt")
  (def inputf "data/day17-input.txt")
  (part1 testf)
  (part1 inputf)
  (part2 test2f 0345300)
  (part2 test3f 01000002724)
  #_(part2 inputf)

;; The End