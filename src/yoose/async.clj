(ns yoose.async
  (:require [clojure.core.async :as async :refer [go <! <!! >!]]
            [yoose.core :refer [UseCase]]))


(defn make-use-case
  "Creates a new use case backed by core.async"
  [in out]
  (reify UseCase
    (-push! [this value] (do (async/put! in value) this))
    (-pull! [this fn1-handler] (do (go (fn1-handler (<! out))) this))
    (-pull!! [_] (<!! out))
    (-<in [this] (throw (ex-info "must use <in macro" {:use-case this})))
    (->out [this value] (throw (ex-info "must use >out macro" {:use-case this
                                                               :value    value})))
    (-in [_] in)
    (-out [_] out)
    (-close! [_] (doseq [c [in out]] (async/close! c)))))


(defmacro <in [use-case]
  `(<! (yoose.core/in ~use-case)))


(defmacro >out [use-case val]
  `(>! (yoose.core/out ~use-case) ~val))
