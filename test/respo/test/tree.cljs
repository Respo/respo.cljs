
(ns respo.test.tree
  (:require [cljs.test :refer-macros [deftest is run-tests]]
            [respo.render.diff :refer [find-element-diffs
                                         find-props-diffs]]))

(def example-2
 {:coord [], :children (sorted-map), :name :span, :props (sorted-map)})

(def example-5
 {:coord [],
  :children
  (sorted-map
    1
    {:coord [1],
     :children (sorted-map),
     :name :div,
     :props (sorted-map)}),
  :name :div,
  :props (sorted-map)})

(def example-4
 {:coord [],
  :children (sorted-map),
  :name :div,
  :props (sorted-map :class "another")})

(def example-1
 {:coord [], :children (sorted-map), :name :div, :props (sorted-map)})

(def example-3
 {:coord [],
  :children (sorted-map),
  :name :div,
  :props (sorted-map :class "demo")})

(def example-6
 {:coord [],
  :children
  (sorted-map
    1
    {:coord [1],
     :children (sorted-map),
     :name :div,
     :props (sorted-map :style (sorted-map :color "red"))}),
  :name :div,
  :props (sorted-map :class "example-6")})

(def example-7
 {:coord [],
  :children
  (sorted-map
    1
    {:coord [1],
     :children
     (sorted-map
       0
       {:coord [1 0],
        :children (sorted-map),
        :name :span,
        :props (sorted-map)}),
     :name :div,
     :props
     (sorted-map
       :style
       (sorted-map :color "yellow" :display "inline-block"))}),
  :name :div,
  :props (sorted-map :class "example-7" :spell-check false)})

(defn diff-demos []
  (println
    "DOM diff 1->2:"
    (find-element-diffs [] [] example-1 example-2))
  (newline)
  (println
    "DOM diff 1->3:"
    (find-element-diffs [] [] example-1 example-3))
  (newline)
  (println
    "DOM diff 1->4:"
    (find-element-diffs [] [] example-1 example-4))
  (newline)
  (println
    "DOM diff 1->5:"
    (find-element-diffs [] [] example-1 example-5))
  (newline)
  (println
    "DOM diff 3->4:"
    (find-element-diffs [] [] example-3 example-4))
  (newline)
  (println
    "DOM diff 3->5:"
    (find-element-diffs [] [] example-3 example-5))
  (newline)
  (println
    "DOM diff 6->7:"
    (find-element-diffs [] [] example-6 example-7)))

(def props-demo-1
 {:placeholder "Task", :value "", :style {:color "red"}})

(def props-demo-2
 {:placeholder "Task", :value "d", :style {:color "red"}})

(defn diff-props-demos []
  (println
    "props diff:"
    (find-props-diffs [] [] props-demo-1 props-demo-2)))
