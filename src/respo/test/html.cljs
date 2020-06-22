
(ns respo.test.html
  (:require [cljs.test :refer [deftest is testing run-tests]]
            [respo.core :refer [html head title script style meta' div link body textarea]]
            [respo.test.comp.todolist :refer [comp-todolist]]
            [respo.render.html :refer [make-string]]
            ["fs" :as fs]))

(defn slurp [file-path] (.readFileSync fs file-path "utf8"))

(deftest
 html-quote-test
 (let [tree-demo (div {:value "a\"b\"c", :x "y", :style {:content "d\"e\"f"}})]
   (testing
    "HTML contains quotes"
    (is (= (slurp "test/examples/quote.html") (make-string tree-demo))))))

(def todolist-store [{:id 101, :text "101"} {:id 102, :text "102"}])

(deftest
 html-test
 (let [todo-demo (comp-todolist todolist-store)]
   (testing
    "test generated HTML from component"
    (is (= (slurp "test/examples/demo.html") (make-string todo-demo))))))

(deftest
 nil-prop-test
 (let [piece (script {:src "a.js", :defer? nil})]
   (testing
    "test generate script with nil"
    (is (= "<script src=\"a.js\"></script>" (make-string piece))))))

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
    "test generated HTML from tree"
    (is (= (slurp "test/examples/simple.html") (make-string tree-demo))))))

(deftest
 textarea-test
 (let [piece (textarea {:value "a\nb\nc\n\"\nd"})]
   (testing
    "test generated HTML from component"
    (is
     (=
      "<textarea value=\"a\\nb\\nc\\n&quot;\\nd\">a\nb\nc\n&quot;\nd</textarea>"
      (make-string piece))))))

(defn main! [] (run-tests))
