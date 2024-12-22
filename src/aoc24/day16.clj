(ns aoc24.day16
  (:require
   [aoc24.util :as util]
   [clojure.data.priority-map :as pm]))

(defn dimensions
  [grid]
  [(count grid) (count (first grid))])

(defn find-all-elements
  "Find all occurrences of the character e in grid"
  [grid e]
  {:pre [(char? e)]}
  (let [[rmax cmax] (dimensions grid)]
    (for [r (range rmax)
          c (range cmax)
          :when (= e (get-in grid [r c]))]
      [r c])))

(defn read-data
  [f]
  (->> f
       util/read-lines))

(defn get-next-pos
  [r c orientation]
  (case orientation
    0 [r (inc c)]      ; east
    1 [(inc r) c]      ; south
    2 [r (dec c)]      ; west
    3 [(dec r) c]      ; north
    (throw (Exception. "Invalid orientation"))))

(defn neighbours
  [grid [rmax cmax] [r c orientation]]
  (let [possible-turns [(mod (dec orientation) 4)  ; turn left
                        orientation                  ; go straight
                        (mod (inc orientation) 4)]   ; turn right
        next-states (for [new-orientation possible-turns
                          :let [[new-r new-c] (get-next-pos r c new-orientation)]
                          :when (and (>= new-r 0) (< new-r rmax)
                                     (>= new-c 0) (< new-c cmax)
                                     (not= \# (get-in grid [new-r new-c])))]
                      [new-r new-c new-orientation])]
    next-states))

(defn cost
  [[_ _ o1] [_ _ o2]]
  (let [base-cost 1
        turn-cost (if (= o1 o2)
                    0  ; no turn
                    1000) ; turning left or right costs 1000
        total-cost (+ base-cost turn-cost)]
    total-cost))

(defn shortest-path
  [grid cost-fn max-states start-state goal-pos]
  (let [[rmax cmax] (dimensions grid)]
    (loop [max-states max-states
          frontier (pm/priority-map start-state 0)
          came-from {}
          cost-so-far {start-state 0}]
     (if (or (neg? max-states)
             (empty? frontier)
             (= (take 2 (first (peek frontier))) goal-pos))
       [came-from cost-so-far]
       (let [current (first (peek frontier))
             current-cost (cost-so-far current)
             children (set (neighbours grid [rmax cmax] current))
             children-costs (reduce #(assoc %1 %2 (+ current-cost (cost-fn current %2))) {} children)
             children-to-add (filter #(or (not (contains? cost-so-far %))
                                          (< (children-costs %) (cost-so-far %))) children)
             new-cost-so-far (reduce #(assoc %1 %2 (children-costs %2)) cost-so-far children-to-add)
             new-frontier (reduce #(assoc %1 %2 (children-costs %2)) (pop frontier) children-to-add)
             new-came-from (reduce #(assoc %1 %2 current) came-from children-to-add)]
         (recur (- max-states (count children-to-add))
                new-frontier
                new-came-from
                new-cost-so-far))))))

(defn extract-path
  [came-from start-state goal-state]
  (loop [current-state goal-state
         path []]
    (cond
      (nil? current-state) nil
      (= current-state start-state) (reverse (conj path start-state))
      :else (recur (came-from current-state)
                   (conj path current-state)))))

(defn find-path
  [grid [start-r start-c] [end-r end-c]]
  (let [start-state [start-r start-c 0]  ; Start facing east
        [came-from costs] (shortest-path
                           grid
                           cost
                           100000
                           start-state
                           [end-r end-c])
        end-states (filter #(= (take 2 %) [end-r end-c]) (keys costs))
        best-end-state (apply min-key #(get costs %) end-states)
        path (extract-path came-from start-state best-end-state)]
    {:path path :cost (get costs best-end-state)}))

(defn part1
  [f]
  (let [grid (read-data f)
        start (first (find-all-elements grid \S))
        end (first (find-all-elements grid \E))]
    (-> grid
        (find-path start end)
        :cost)))

(comment
  (def test1f "data/day16-test1.txt")
  (def test2f "data/day16-test2.txt")
  (def inputf "data/day16-input.txt")
  (part1 test1f)
  (part1 test2f)
  (part1 inputf))

;; The End