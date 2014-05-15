(ns stream-processing.database
  (:require [clojure.java.jdbc :as sql]
            [clojure.core.async :refer [>! >!! <!! chan go timeout]]))

(def db-spec 
  {:classname "org.postgresql.Driver" ; must be in classpath
   :subprotocol "postgresql"
   :subname (str "//localhost:5432/tweets")
   :user "socialurls"})

(defn get-tweets []
  (let [chan (chan)]
    (go
    (let [db-connection (doto (sql/get-connection db-spec) (.setAutoCommit false))
          query "select * from tweets"
          statement (sql/prepare-statement db-connection query :fetch-size 1000 )]
      (sql/db-query-with-resultset db-spec [statement] (fn [x] 
        (let [lazy (sql/result-set-seq x)] 
          (println "got seq")
          (doseq [row lazy] 
              ;(println row)
              ;(println ">")
              ;(<!! (timeout 10))
              (go (>! chan row))
                ))))))
    chan))
