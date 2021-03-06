(ns stream-processing.core
  (:require [stream-processing.database :as db]
            [stream-processing.tweet :refer [parse-tweet]]
            [stream-processing.standardscore :refer [zscores]]
            [stream-processing.sockets :as sockets]
            [clojure.core.async :refer [<!! >!! chan go timeout]])
  (import com.clearspring.analytics.stream.cardinality.HyperLogLog)
  (import com.clearspring.analytics.stream.StreamSummary)
  )


(defn map-topk [topk]
  (map (fn [item] { 
    :item (.getItem item)
    :count (.getCount item)
  }) topk))

(defn write-topk [] 
  (let [chan (chan)
        sample-size 1000
        total-log (HyperLogLog. 10)
        total-summary (StreamSummary. 100)
       ; tweets (db/tweet-chan)
        tweets (sockets/tweet-stream)
         
        ]
    (go 
      (loop [tweet (<!! tweets)
             log (HyperLogLog. 10)
             summary (StreamSummary. 100)
             total 0]
        (if tweet
          (do 
            (let [hashtags (get tweet :hashtags)]
              (doseq [hashtag hashtags] 
                (.offer log (.toLowerCase hashtag))
                (.offer summary (.toLowerCase hashtag))
                (.offer total-log (.toLowerCase hashtag))
                (.offer total-summary (.toLowerCase hashtag))))
            
            (if (= (mod total sample-size) 0)
              (do 
                (>! chan { 
                          :total-tweets total
                          :last-date (str (get tweet :date))
                          :sample-count sample-size
                          :sample-unique (.cardinality log)
                          :sample-top-k (map-topk (.topK summary 100))
                          :total-unique (.cardinality total-log)
                          :total-top-k (map-topk (.topK total-summary 10))
                          })
                (recur (<!! tweets) (HyperLogLog. 10) (StreamSummary. 100) (+ total 1)))
              (recur (<!! tweets) log summary (+ total 1)))))))
    chan
    )) 


(defn summaries []
  (let [chan (chan)]
    (go
      (let [c (write-topk)]
        (loop [summary (<!! c)
               samples []] 
            
          (let [top-k (:sample-top-k summary)
                top-k-history (map (fn [x] (:sample-top-k x)) samples)
                scores (sort-by :score > (zscores top-k top-k-history))
                ]
              
              ;(println "")
              ;(println (:sample-top-k summary))
              ;(println (:last-date summary))
              (let [all-data (assoc summary :zscores scores)]
                (>!! chan all-data))
              ;(println "")
            )
          (recur (<!! c) (take 30 (cons summary samples))))))
    chan))

  

(defn -main [& args]
  (println "Working!")
  (let [c (summaries)]
      (loop [summary (<!! c)]
              (println "")
              (println "")
             
              (println (:last-date summary))
              (doseq [score (take 20 (:zscores summary))] 
                (println score))

                
        (recur (<!! c)))
    )
  (println "DONE")
)
