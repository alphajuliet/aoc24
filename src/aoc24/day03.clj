(ns aoc24.day03
  (:require [aoc24.util :as util]))

(defn find-muls
  "Find all the muls in a line"
  [line]
  (->> line
       (re-seq #"mul\(([0-9]+),([0-9]+)\)")
       (map (fn [[_ x y]] (* (Integer/parseInt x) (Integer/parseInt y))))
       (reduce +)))

(defn find-commands
  "Find all the commands in a line and only add the mul results that are not disabled."
  [line flag]
  (let [cmds (re-seq #"do\(\)|don't\(\)|mul\(([0-9]+),([0-9]+)\)" line)]
    (reduce
     (fn [acc [cmd x y]]
       (case cmd
         "do()" (assoc acc :flag true)
         "don't()" (assoc acc :flag false)
         (if (:flag acc)
           (update acc :sum + (* (Integer/parseInt x) (Integer/parseInt y)))
           acc)))
     {:sum 0 :flag flag}
     cmds)))
       
(defn part1
  [f]
  (->> f
       util/read-lines
       (map find-muls)
       (reduce +)))

(defn part2
  [f]
  (->> f
       util/read-lines
       (reduce
        (fn [acc line]
          (let [result (find-commands line (:flag acc))]
            (-> result
                (update :sum + (:sum acc))
                (assoc :flag (:flag result)))))
        {:sum 0, :flag true})
       :sum))

(comment
  (def testf "data/day03-test.txt")
  (def test2f "data/day03-test2.txt")
  (def inputf "data/day03-input.txt")

  (part1 testf)
  (part1 inputf)
  
  (part2 test2f)
  (part2 inputf))
  
;; The End