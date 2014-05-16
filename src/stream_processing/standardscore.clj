(ns stream-processing.standardscore
  (require [clojure.math.numeric-tower :refer [expt sqrt]]))

(defn zscore [observation population]
  (let [size (count population)]
    (if (= 0 size)
      0
      (let [size (count population)
            average (/ (reduce + population) size)
            dev (map (fn [x] (expt (- x average) 2)) population)
            std-dev (sqrt (/ (reduce + dev) size))] 
        (if (= 0 std-dev) 
          0
          (/ (- observation average) std-dev))))))

(defn find-count [value col]
  (let [matches (filter (fn [x] (= (:item x) value)) col)]
    (if (= 1 (count matches))
      (:count (first matches))
      0
      )))

(defn find-counts [value history]
  (map (fn [sample] (find-count value sample)) history))

(defn zscores [sample history]
  (map (fn [item]
         (let [term (:item item)
               population (find-counts term history)
               score (zscore (:count item) population) 
               ]
           {:item term 
            :score score}
           )) sample))

;http://stackoverflow.com/a/826509
;
;def zscore(obs, pop):
;    # Size of population.
;    number = float(len(pop))
;    # Average population value.
;    avg = sum(pop) / number
;    # Standard deviation of population.
;    std = sqrt(sum(((c - avg) ** 2) for c in pop) / number)
;    # Zscore Calculation.
;    return (obs - avg) / std
