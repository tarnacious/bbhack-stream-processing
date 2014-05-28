(ns stream-processing.sockets
  (:require [zeromq.zmq :as zmq]
            [clojure.core.async :refer [>! >!! <!! chan go timeout close!]]
            [stream-processing.tweet :refer [parse-tweet]]
            [clojure.string :as str]))

(defn tweet-stream []
  (let [chan (chan)]
    (go
      (let [context (zmq/zcontext)]
      (with-open [subscriber (doto (zmq/socket context :sub)
                               (zmq/connect "tcp://144.76.187.43:5555")
                               (zmq/subscribe "tweet.stream"))]
        (loop []
          (let [tweet (zmq/receive-str subscriber)]
            (if (not= tweet "tweet.stream")
              (let [tweet (parse-tweet (str tweet))]
                (if (= "en" (:lang tweet))
                  (>! chan tweet)))) 
          (recur))))))
      chan 
    ))
