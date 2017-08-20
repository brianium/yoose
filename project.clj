(defproject yoose "0.1.0"
  :description "A small library for use case driven development"
  
  :url "https://github.com/brianium/yoose"
  
  :license {:name "MIT"}
  
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.async "0.3.443"]]

  :jar-exclusions [#"test" #"yussss.gif"]

  :source-paths ["src"]

  :profiles {:dev {:dependencies [[org.clojure/test.check "0.10.0-alpha2"]]

                   :source-paths ["src" "test"]

                   :plugins [[venantius/ultra "0.5.1"]
                             [lein-kibit "0.1.6-beta2"]]}})
