(ns stream-processing.tweet-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [stream-processing.tweet :refer :all]))

(deftest a-test
  (let [json (slurp "resources/tweet.json")
        tweet (parse-tweet json)
        expected-date (t/date-time 2014 4 16 0 17 39)]
    (testing "hashtags" (is (= '("reddit" "bitcoin") (:hashtags tweet))))
    (testing "date" (is (= expected-date (:date tweet))))))
