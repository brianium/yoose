(ns yoose.async-test
  (:require [clojure.test :refer :all]
            [clojure.test.check]
            [clojure.spec.test.alpha :as st]
            [clojure.core.async :refer [<!! go chan <! >!]]
            [yoose.core :refer :all :exclude [<in >out]]
            [yoose.async :refer :all]
            [yoose.spec :as yoose]
            [yoose.async.spec :as async]
            [yoose.async.generators :as yoose-gen]))


(defn test-async
  [ch]
  (<!! ch))


(deftest test-push!
  (let [in       (chan)
        out      (chan)
        use-case (make-use-case in out)]
    (push! use-case "hello")
    (test-async
      (go (is (= "hello" (<in use-case)))))))


(deftest test-pull!
  (let [in         (chan)
        out        (chan)
        use-case (make-use-case in out)]
    (go (>out use-case "hello"))
    (test-async
      (go (pull! use-case #(is (= "hello" %)))))))


(deftest test-pull!!
  (let [in       (chan)
        out      (chan)
        use-case (make-use-case in out)]
    (go (>out use-case "hello"))
    (is (= "hello" (pull!! use-case)))))


(def gen-overrides {::yoose/use-case yoose-gen/use-case
                    ::async/chan     yoose-gen/chan})


(deftest generated-tests
  (doseq [test-output (-> (st/enumerate-namespace 'yoose.async)
                          (st/check {:gen gen-overrides}))
          sym (-> test-output :sym name)]
    (testing sym
      (is (true? (-> test-output :clojure.spec.test.check/ret :result)) test-output))))
