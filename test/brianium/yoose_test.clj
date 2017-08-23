(ns brianium.yoose-test
  (:require [clojure.test :refer :all]
            [clojure.test.check]
            [clojure.spec.test.alpha :as st]
            [brianium.yoose :refer :all]
            [brianium.yoose.spec :as yoose]
            [brianium.yoose.generators :as yoose-gen]))


(def gen-overrides {::yoose/use-case     (yoose-gen/use-case 1500)
                    ::yoose/pull-handler yoose-gen/pull-handler
                    ::yoose/port-value   yoose-gen/port-value})


(deftest generated-tests
  (doseq [test-output (st/check (st/enumerate-namespace 'brianium.yoose) {:gen gen-overrides})
          sym (-> test-output :sym name)]
    (testing sym
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
