(ns brianium.yoose.async.generators
  (:require [clojure.core.async :as async]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [brianium.yoose.async :as yoose-async]))


(defn chan []
  (s/gen #{ (async/chan) }))


(defn use-case []
  (let [in         (gen/generate (chan))
        out        (gen/generate (chan))
        yoose-case (yoose-async/make-use-case in out)]
    (s/gen #{ yoose-case  })))
