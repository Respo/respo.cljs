
(ns respo.test.html
  (:require-macros [respo.macros :refer [html
                                         head
                                         title
                                         script
                                         style
                                         meta'
                                         div
                                         link
                                         body]])
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [respo.test.comp.todolist :refer [comp-todolist]]
            [respo.render.html :refer [make-string]]))

(def todolist-store
 (atom [{:id 101, :text "101"} {:id 102, :text "102"}]))

(def fs (js/require "fs"))

(defn slurp [file-path]
  (.readFileSync fs file-path "utf8"))

(deftest
  html-test
  (let [todo-demo (comp-todolist @todolist-store)]
    (testing
      "test generated HTML"
      (is (= (slurp "test/examples/demo.html") (make-string todo-demo))))))

(deftest
  simple-html-test
  (let [tree-demo (html
                    {}
                    (head
                      {}
                      (title {:innerHTML "Demo"})
                      (link {:rel "icon", :type "image/png"})
                      (script {:innerHTML "{}"}))
                    (body {} (div {:id "app"} (div {}))))]
    (testing
      "test generated HTML"
      (is (= (slurp "test/examples/simple.html") (make-string tree-demo))))))


(deftest html-quote-test
  (let [tree-demo (div {:value "a\"b\"c"
                        :x "y"
                        :style {:content "d\"e\"f"}})]
    (testing "HTML contains quotes"
      (is (= (slurp "test/examples/quote.html") (make-string tree-demo))))))

(defn main! []
  (run-tests))

(defn reload! []
  (run-tests))
