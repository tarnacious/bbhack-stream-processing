(defproject stream-processing "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main stream-processing.core
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.256.0-1bf8cf-alpha"]
                 [org.clojure/data.json "0.2.4"]
                 [clj-time "0.7.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [org.zeromq/cljzmq "0.1.4"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [com.clearspring.analytics/stream "2.7.0-SNAPSHOT"]
                 ])

