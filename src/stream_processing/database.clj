(ns stream-processing.database
  (:require [clojure.java.jdbc :as sql]
            [stream-processing.tweet :refer [parse-tweet]]
            [clojure.java.io :as io]
            [clojure.core.async :refer [>! >!! <!! chan go timeout close!]])
  )

(def db-spec 
  {:classname "org.postgresql.Driver" ; must be in classpath
   :subprotocol "postgresql"
   :subname (str "//localhost:5432/socialurls")
   :user "socialurls"})


(defn get-rows [db-spec query result-fn]
  (let [db-connection (doto (sql/get-connection db-spec) (.setAutoCommit false))
        statement (sql/prepare-statement db-connection query :fetch-size 1000 )]
    (sql/db-query-with-resultset db-spec [statement] (fn [x] 
      (result-fn (sql/result-set-seq x))))))

(defn read-tweets [rows result-fn]
  (doseq [row rows] 
    (let [json (get row :json)
          tweet (parse-tweet json)]
      (if tweet (result-fn tweet)))))

(defn tweet-chan []
  (let [chan (chan)]
    (go
      (get-rows db-spec "select * from tweets" (fn [rows]
         (read-tweets rows (fn [tweet] 
           (>!! chan tweet)
           ;(go (>! chan tweet))   ; async fun 
         ))
         (close! chan))))
    chan))

(defn write-hashtags []
  (let [tweets (tweet-chan)]
    (with-open [wrt (io/writer "hashtags")]
      (loop [tweet (<!! tweets)]
        (if tweet 
          (let [hashtags (get tweet :hashtags)
                date (get tweet :date)]
              (doseq [hashtag hashtags] 
                (.write wrt (str date " " hashtag "\n")))
            (recur (<!! tweets)))
            )))))
