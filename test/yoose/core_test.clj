(ns yoose.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check]
            [clojure.spec.test.alpha :as st]
            [yoose.core :refer :all]
            [yoose.spec :as yoose]
            [yoose.generators :as yoose-gen]))


(def gen-overrides {::yoose/use-case     (yoose-gen/use-case 1500)
                    ::yoose/pull-handler yoose-gen/pull-handler
                    ::yoose/port-value   yoose-gen/port-value})


(deftest generated-tests
  (doseq [test-output (-> (st/enumerate-namespace 'yoose.core)
                          (st/check {:gen gen-overrides}))
          sym (-> test-output :sym name)]
    (testing sym
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
