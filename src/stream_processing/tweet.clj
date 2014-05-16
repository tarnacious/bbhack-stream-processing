(ns stream-processing.tweet
  (:require [clojure.data.json :as json]
            [clj-time.format :as f]
            ))

(defn parse-date [s]
  (f/parse (f/formatter "E MMM dd HH:mm:ss Z YYYY") s))

(defn parse-tweet [json]
  (let [tweet (json/read-str json)
        text (get tweet "text")]
    (if text
      (let [entities (get tweet "entities")
            hashtags (map (fn [x] (get x "text")) (get entities "hashtags"))
            urls (map (fn [x] (get x "url")) (get entities "urls"))
            date (parse-date (get tweet "created_at"))
            result { :text text
                     :hashtags hashtags           
                     :urls urls
                     :date date }]  
        ;(println tweet)
        result
      )
      nil)))


