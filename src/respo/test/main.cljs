
(ns respo.test.main
  (:require [respo.test.html :as html]
            [cljs.test :refer [deftest testing is run-tests]]
            [respo.util.list :refer [pick-attrs pick-event]]
            [respo.util.format :refer [path-data]]))

(defn main! [] (html/main!) (run-tests))

(defn reload! [] (main!))

(deftest
 test-path-data
 (is (= "M0,0" (path-data :M 0 0)))
 (is (= "M0,0 L4,4" (path-data :M 0 0 :L 4 4)))
 (is (= "M0,0 Z" (path-data :M 0 0 :Z)))
 (is (= "M20,20 c4,4,4,4,4,4" (path-data :M 20 20 :c 4 4 4 4 4 4)))
 (is (= "M0,0 A1,2,3,4,5,6,7" (path-data :M 0 0 :A 1 2 3 4 5 6 7))))

(deftest
 test-pick-attrs
 (is (= (pick-attrs {:value "string", :on-click (fn [] )}) (list [:value "string"]))))

(deftest
 test-pick-event
 (testing
  "test event"
  (let [f (fn [] )]
    (is (= (pick-event {:value "a", :on-click f}) {:click f}))
    (is (= (pick-event {:value "a", :on-click f, :on {:input f}}) {:click f, :input f})))))
