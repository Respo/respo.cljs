
(ns respo.test.main
  (:require [respo.test.html :as html]
            [cljs.test :refer [deftest testing is run-tests]]
            [respo.util.list :refer [pick-attrs pick-event]]))

(defn main! [] (html/main!) (run-tests))

(defn reload! [] (main!))

(deftest
 test-pick-event
 (testing
  "test event"
  (let [f (fn [] )]
    (is (= (pick-event {:value "a", :on-click f}) {:click f}))
    (is (= (pick-event {:value "a", :on-click f, :on {:input f}}) {:click f, :input f})))))

(deftest
 test-pick-attrs
 (is (= (pick-attrs {:value "string", :on-click (fn [] )}) (list [:value "string"]))))
