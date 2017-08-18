(ns yoose.async
  (:require [clojure.core.async :as async :refer [go go-loop <! <!! >!]]
            [clojure.core.async.impl.protocols :refer [ReadPort WritePort Channel]]
            [yoose.core :refer [UseCase out in]]))


(defn chan?
  "Check if the given value is a ManyToMany channel"
  [value]
  (and
    (satisfies? ReadPort value)
    (satisfies? WritePort value)
    (satisfies? Channel value)))


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
  "Replaces yoose.core/<in with a macro to circumvent
  issues with go macros stopping at function creation
  boundaries"
  `(<! (in ~use-case)))


(defmacro >out [use-case val]
  "Replaces yoose.core/>out with a macro to circumvent
  issues with go macros stopping at function creation
  boundaries"
  `(>! (out ~use-case) ~val))


(defmacro defusecase
  "Defines an async use case. A use case is really just a function
  that executes its body in the context of a go loop"
  ([name [self-binding arg-binding] & body]
   (let [bindings (if (nil? arg-binding) ['in 'out] ['in 'out arg-binding])]
     `(def ~name (fn ~bindings
                   (let [~self-binding (make-use-case ~'in ~'out)]
                     (go-loop []
                       ~@body
                       (recur))
                     ~self-binding))))))

