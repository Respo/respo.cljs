
(ns respo.html-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [respo.alias :refer [html
                                 head
                                 title
                                 script
                                 style
                                 meta'
                                 div
                                 link
                                 body]]
            [respo.test-component.todolist :refer [comp-todolist]]
            [respo.render.html :refer [make-string make-html]]))

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
      (is (= (slurp "test/examples/simple.html") (make-html tree-demo))))))

(run-tests)
