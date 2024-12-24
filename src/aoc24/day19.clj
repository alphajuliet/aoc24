(ns aoc24.day19
  (:require [clojure.string :as str]
            [instaparse.core :as insta]
            [aoc24.util :as util]))

(defn read-data
  [f]
  (let [[symbols words] (str/split (slurp f) #"\n\n+")
        grammar (as-> symbols <>
                  (str/split <> #",\s")
                  (map #(str "'" % "'") <>)
                  (str/join "|" <>)
                  (str "<word> := (" <> ")+"))
        word-list (str/split words #"\n")]
    [grammar word-list]))

(defn parseable?
  [grammar word]
  (if (map? (grammar word)) false true))

(defn part1
  [f]
  (let [[grammar word-list] (read-data f)
        parser (insta/parser grammar)]
    (->> word-list
         (util/count-if (partial parseable? parser)))))

(comment
  (def testf "data/day19-test.txt")
  (def inputf "data/day19-input.txt")
  (part1 testf)
  (part1 inputf))