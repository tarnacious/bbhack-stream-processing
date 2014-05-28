(ns stream-processing.webserver 
  (:use [ring.middleware reload params resource file-info]
        [ring.adapter.jetty :only [run-jetty]]
        )
  (require [clojure.core.async :refer [<!! >!! chan go timeout]]
           [clojure.data.json :as json]
           [stream-processing.core :refer [summaries]])
  (:import (org.webbitserver WebServer
                             WebServers
                             WebSocketHandler)
           (org.webbitserver.handler StaticFileHandler)))

;===============================================================================
; Handling Ring Requests
;===============================================================================


(defn render-notfound [req]
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body (str "path not found: " (:uri req))})

(defn- ring-handler [req]
  (let [hf (case (req :uri)
             render-notfound)]
    (hf req)))

(def ^:private ring-app*
  (-> #'ring-handler
    (wrap-resource "web")
    (wrap-file-info)
    (wrap-params)
    (wrap-reload '(iteractive.chat))))

;===============================================================================
; Webbit Handler / WebSockets
;===============================================================================

(def ^:private ws-conns* (ref #{}))
(def offers (atom []))
(def connections (atom (hash-map)))

(defn- ws-stats [] (println "websocket count:" (count @ws-conns*)))

(defn- ws-open [c]
  (dosync (alter ws-conns* conj c))
  (ws-stats)
  
  (let [chan (summaries)]
      (loop [summary (<!! chan)]
              (println "")
              (println "")
             
              (println (:last-date summary))
              (doseq [score (take 20 (:zscores summary))] 
                (println score))
              (.send c (json/write-str summary))
        (recur (<!! chan))))
  )

(defn- ws-close [c]
  (dosync (alter ws-conns* disj c))

  (ws-stats))

(defn- ws-message [c m]
    )

(def ^:private ws-handler*
  (proxy [WebSocketHandler] []
    (onOpen [c] (ws-open c))
    (onClose [c] (ws-close c))
    (onMessage [c m] (ws-message c m))))

;===============================================================================
; Main
;===============================================================================

(defn -main []
  (println "starting jetty server...")
  (run-jetty #'ring-app* {:port 8080 :join? false})
  (println "starting webbit server...")
  (doto (WebServers/createWebServer 8081)
    (.add "/websocket" ws-handler*)
    (.start)))
