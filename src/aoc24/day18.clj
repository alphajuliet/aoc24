(ns aoc24.day18
  (:require
   [aoc24.search :as sch]
   [clojure.edn :as edn]))

(defn read-data
  [f]
  (->> f
       slurp
       (re-seq #"\d+")
       (map edn/read-string)
       (partition 2)))

(defn neighbours
  [bytes [rmax cmax] [r c]]
  (let [dirs [[-1 0] [0 -1] [0 1] [1 0]]
        nn (map (partial mapv + [r c]) dirs)]
    (filter (fn [[r c]]
              (and (<= 0 r rmax)
                   (<= 0 c cmax)
                   (not (some #{[r c]} bytes))))
            nn)))

(defn find-path
  [bytes max-rc start end]
  (let [[came-from _] (sch/shortest-path
                       (partial neighbours bytes max-rc)
                       (constantly 1)
                       10000
                       start
                       end)]
    (sch/extract-path came-from start end)))

(defn part1
  [max-rc n f]
  (let [bytes (read-data f)]
    (->> (find-path (take n bytes) max-rc [0 0] max-rc)
         count
         dec)))

#_(defn part2
  [max-rc n f]
  (let [bytes (read-data f)
        [came-from _] (sch/find-all-paths
                       (partial neighbours (take n bytes) max-rc)
                       (constantly 1)
                       10000
                       [0 0]
                       max-rc)
        remainder (drop n bytes)
        paths (sch/extract-all-paths came-from [0 0] max-rc)]
      ;; Return the first byte in the `remainder` list that blocks the last remaining path in `paths`
      (count paths)))

(defn part2
  [max-rc n f]
  (let [bytes (read-data f)]
    (loop [n' n]
      (let [p (find-path (take n' bytes) max-rc [0 0] max-rc)]
        (println n')
        (if (nil? p)
          (nth bytes (dec n'))
          (recur (inc n')))))))

(comment
  (def testf "data/day18-test.txt")
  (def inputf "data/day18-input.txt")
  (part1 [6 6] 12 testf)
  (part1 [70 70] 1024 inputf)
  (part2 [6 6] 12 testf)
  (part2 [70 70] 1024 inputf))