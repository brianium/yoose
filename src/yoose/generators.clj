(ns yoose.generators
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [yoose.core :as yoose]))


(defn- pop-and-return
  [*queue]
  (let [value (peek @*queue)]
    (swap! *queue pop)
    value))


(defn- pop-and-call
  [*queue fn1-handler]
  (fn1-handler (pop-and-return *queue)))


(defn fake-use-case
  ([*queue-in *queue-out]
   (reify yoose/UseCase
     (-push! [this value] (do (swap! *queue-in conj value) this))
     (-pull! [this fn1-handler] (do (pop-and-call *queue-out fn1-handler) this))
     (-pull!! [_] (pop-and-return *queue-out))
     (-<in [_] (pop-and-return *queue-in))
     (->out [this value] (do swap! *queue-out conj value) this)
     (-in [_] *queue-in)
     (-out [_] *queue-out)
     (-close! [_] (doseq [q [*queue-in *queue-out]]
                    (reset! q clojure.lang.PersistentQueue/EMPTY))
                  nil)))
  ([]
   (fake-use-case
     (atom clojure.lang.PersistentQueue/EMPTY)
     (atom clojure.lang.PersistentQueue/EMPTY))))


(defn pull-handler []
  (s/gen #{ identity }))


(defn port-value []
  (s/gen string?))


(defn- fill-queue
  [queue count]
  (->> (repeatedly count #(gen/generate (port-value)))
       (reduce conj queue)))


(defn use-case [queue-size]
  (fn []
    (let [in  (clojure.lang.PersistentQueue/EMPTY)
          out (clojure.lang.PersistentQueue/EMPTY)]
      (s/gen #{ (fake-use-case (atom (fill-queue in queue-size)) (atom (fill-queue out queue-size))) }))))
