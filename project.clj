(defproject yoose "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  
  :url "http://example.com/FIXME"
  
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.async "0.3.443"]]

  :source-paths ["src"]

  :profiles {:dev {:dependencies [[org.clojure/test.check "0.10.0-alpha2"]]

                   :source-paths ["src" "test"]

                   :plugins [[venantius/ultra "0.5.1"]
                             [lein-kibit "0.1.6-beta2"]]}})
