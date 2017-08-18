(ns yoose.async.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async.impl.protocols :refer [ReadPort WritePort Channel]]
            [yoose.core :as yoose]
            [yoose.async :as async]
            [yoose.spec :as yoose-spec]))


(s/def ::chan async/chan?)


(s/def ::in ::chan)


(s/def ::out ::chan)


(s/fdef async/make-use-case
  :args (s/cat :in ::in :out ::out)
  :ret  ::yoose-spec/use-case)


(s/def ::use-case-factory
  (s/fspec :args (s/cat
                   :in   ::in
                   :out  ::out
                   :deps (s/? any?))
           :ret  ::yoose-spec/use-case))
