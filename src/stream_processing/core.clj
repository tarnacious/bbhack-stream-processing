(ns stream-processing.core
  (:require [stream-processing.database :as db]
            [clojure.data.json :as json]
            [clojure.core.async :refer [<!! >!! chan go timeout]]
))


(defn get-stream [] 
  (let [c (chan 10000) 
        tweets (db/get-tweets)]
    (go
    (loop [tweet (<!! tweets)
           total 0]
     (if (= (mod total 1000) 0)
        (println total))
     ;(println "<")
     (>! c tweet)
    (recur (<!! tweets) (+ total 1))))
    c
    ))


(defn parse-tweet [json]
  (let [t (json/read-str json)]  
  ))


(defn -main [& args]
  (println "Working!")
  (let [c (get-stream)]
    (loop [_ (<!! c)] 
      (recur (<!! c))))
  (println "DONE")
)
