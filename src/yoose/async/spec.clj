(ns yoose.async.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async.impl.protocols :refer [ReadPort WritePort Channel]]
            [yoose.core :as yoose]
            [yoose.async :as async]
            [yoose.spec :as yoose-spec]))


(defn chan?
  "Check if the given value is a ManyToMany channel"
  [value]
  (and
    (satisfies? ReadPort value)
    (satisfies? WritePort value)
    (satisfies? Channel value)))


(s/def ::chan chan?)


(s/def ::in ::chan)


(s/def ::out ::chan)


(s/fdef async/make-use-case
  :args (s/cat :in ::in :out ::out)
  :ret  ::yoose-spec/use-case)
