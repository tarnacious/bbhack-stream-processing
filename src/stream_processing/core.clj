(ns stream-processing.core
  (:require [stream-processing.database :as db]
            [stream-processing.tweet :refer [parse-tweet]]
            [stream-processing.standardscore :refer [zscores]]
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
        sample-size 5000
        total-log (HyperLogLog. 10)
        total-summary (StreamSummary. 100)
        tweets (db/tweet-chan)]
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

(defn -main [& args]
  (println "Working!")
  (let [c (write-topk)]
    (loop [summary (<!! c)
           samples []] 
        
      (let [top-k (:sample-top-k summary)
            top-k-history (map (fn [x] (:sample-top-k x)) samples)
            scores (sort-by :score > (zscores top-k top-k-history))
            ]
          
          (println "")
          ;(println (:sample-top-k summary))
          (println (:last-date summary))
          (doseq [score (take 20 scores)] 
            (println score)

            )
          (println "")
        )
      (recur (<!! c) (take 30 (cons summary samples)))))
  
  (println "DONE")
)
