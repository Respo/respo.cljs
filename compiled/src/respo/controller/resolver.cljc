
(ns respo.controller.resolver
  (:require [clojure.string :as string]
            [respo.util.format :refer [purify-element]]
            [respo.util.detect :refer [component? element?]]
            [respo.util.error :refer [raise]]))

(defn get-markup-at [markup coord]
  (comment println "get markup:" (pr-str coord))
  (if (= coord [])
    markup
    (if (component? markup)
      (recur (:tree markup) (subvec coord 1))
      (let [coord-first (first coord)
            child (get-in markup [:children coord-first])]
        (if (some? child)
          (recur child (subvec coord 1))
          (raise
            (str "child not found:" coord (purify-element markup))))))))

(defn find-event-target [element coord event-name]
  (let [target-element (get-markup-at element coord)
        element-exists? (some? target-element)]
    (comment
      println
      "target element:"
      (pr-str (:c-coord target-element) event-name))
    (if (and
          element-exists?
          (contains? (:event target-element) event-name))
      target-element
      (if (= coord [])
        nil
        (if element-exists?
          (recur
            element
            (subvec coord 0 (- (count coord) 1))
            event-name)
          nil)))))
