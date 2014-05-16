(ns stream-processing.cardinality
  (:require [clojure.core.async :refer [<!! >!! chan go timeout]])
  
  (import com.clearspring.analytics.stream.cardinality.HyperLogLog)
  
  )

(defn proc []
  (let [log (HyperLogLog. 10)]
        (.offer log "e")
        (.offer log "b")
        (.offer log "c")
        (.offer log "e")
        (.offer log "e")
        (.cardinality log)))
