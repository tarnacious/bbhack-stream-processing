(ns stream-processing.standardscore-test
  (:require [clojure.test :refer :all]
            [stream-processing.standardscore :refer :all]))

(deftest standardscore-test 
    (is (= (/ 7 2) (zscore 12 [2, 4, 4, 4, 5, 5, 7, 9]))))
    (is (= 0.07392212709545729 (zscore 20 [21, 22, 19, 18, 17, 22, 20, 20]))
    (is (= 1.0030359923410903 (zscore 20 [21, 22, 19, 18, 17, 22, 20, 20, 1, 2, 3, 1, 2, 1, 0, 1]))))

(deftest find-count-test
  (let [data [{:item "cat" :count 100} {:item "dog" :count 200}]]
    (is (= (find-count "cat" data) 100))
    (is (= (find-count "dog" data) 200))
    (is (= (find-count "elephant" data) 0))))

(deftest find-counts-test
  (let [data [[{:item "cat" :count 10} {:item "dog" :count 100}]
              [{:item "cat" :count 5} {:item "dog" :count 200}]
              [{:item "cat" :count 4} {:item "dog" :count 300}]]]

    (is (= (find-counts "cat" data)) [10 5 4])
    (is (= (find-counts "dog" data)) [100 200 300])))


