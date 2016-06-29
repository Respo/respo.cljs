
(ns respo.controller.resolver
  (:require [clojure.string :as string]
            [respo.util.format :refer [purify-element]]
            [respo.util.detect :refer [component? element?]]
            [respo.util.error :refer [raise]]
            [respo.util.list :refer [filter-first]]))

(defn get-component-at
  ([markup coord] (get-component-at nil markup coord))
  ([acc markup coord]
    (if (= (count coord) 0)
      acc
      (let [coord-head (first coord)]
        (if (component? markup)
          (if (= (:name markup) coord-head)
            (recur markup (:tree markup) (subvec coord 1))
            nil)
          (let [child-pair (filter-first
                             (fn [child-entry]
                               (= (get child-entry 0) coord-head))
                             (:children markup))]
            (if (some? child-pair)
              (recur acc (last child-pair) (subvec coord 1))
              nil)))))))

(defn get-markup-at [markup coord]
  (comment println "markup:" (pr-str coord))
  (if (= coord [])
    markup
    (if (component? markup)
      (recur (:tree markup) (subvec coord 1))
      (let [coord-head (first coord)
            child-pair (filter-first
                         (fn [child-entry]
                           (= (get child-entry 0) coord-head))
                         (:children markup))]
        (if (some? child-pair)
          (get-markup-at (get child-pair 1) (subvec coord 1))
          (raise
            (str
              "child not found:"
              coord
              (map first (:children markup)))))))))

(defn find-event-target [element coord event-name]
  (let [target-element (get-markup-at element coord)
        element-exists? (some? target-element)]
    (comment println "target element:" (pr-str event-name))
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
