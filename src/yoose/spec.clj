(ns yoose.spec
  (:require [clojure.spec.alpha :as s]
            [yoose.core :as yoose]))


(s/def ::use-case yoose/use-case?)


(s/def ::pull-handler (s/fspec :args (s/cat :x any?)))


(s/def ::port-value (complement nil?))


(s/fdef yoose/push!
  :args (s/cat :use-case ::use-case :value ::port-value)
  :ret  ::use-case)


(s/fdef yoose/pull!
  :args (s/cat :use-case ::use-case :fn1-handler ::pull-handler)
  :ret  ::use-case)


(s/fdef yoose/pull!!
  :args (s/cat :use-case ::use-case)
  :ret  ::port-value)


(s/fdef yoose/<in
  :args (s/cat :use-case ::use-case)
  :ret  ::port-value)


(s/fdef yoose/>out
  :args (s/cat :use-case ::use-case :value ::port-value)
  :ret  ::use-case)


(s/fdef yoose/close!
  :args (s/cat :use-case ::use-case)
  :ret  nil?)
