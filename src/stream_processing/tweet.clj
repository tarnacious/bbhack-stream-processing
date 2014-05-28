(ns stream-processing.tweet
  (:use [clj-time.core]
     [clj-time.format]
     [clj-time.coerce])
  (:require [clojure.data.json :as json]
            [clj-time.format :as f]

            )
  (:import java.util.Locale)
  )

(def twitter_formatter (with-locale (clj-time.format/formatter "EEE MMM dd HH:mm:ss Z yyyy") Locale/ENGLISH))
(defn parse-date [data_string]
  (try
    (clj-time.coerce/to-string (clj-time.format/parse
      twitter_formatter
      data_string))
  (catch Exception e
     (println "Date conversion failed:" e)
     "1970-01-01T00:00:01Z")))

(defn parse-tweet [json]
  (let [tweet (json/read-str json)
        text (get tweet "text")]
    (if text
      (let [entities (get tweet "entities")
            hashtags (map (fn [x] (get x "text")) (get entities "hashtags"))
            urls (map (fn [x] (get x "url")) (get entities "urls"))
            date (parse-date (get tweet "created_at"))
            lang (get tweet "lang")
            result { :text text
                     :hashtags hashtags           
                     :urls urls
                     :date date
                     :lang lang }]  
        ;(println tweet)
        result
      )
      nil)))


