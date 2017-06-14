
(ns respo.controller.resolver
  (:require [clojure.string :as string]
            [respo.util.detect :refer [component? element?]]
            [polyfill.core :refer [raise*]]
            [respo.util.list :refer [filter-first]]))

(defn get-component-at
  ([markup coord] (get-component-at nil markup coord))
  ([acc markup coord]
   (if (empty? coord)
     acc
     (let [coord-head (first coord)]
       (if (component? markup)
         (if (= (:name markup) coord-head) (recur markup (:tree markup) (rest coord)) nil)
         (let [child-pair (filter-first
                           (fn [child-entry] (= (get child-entry 0) coord-head))
                           (:children markup))]
           (if (some? child-pair) (recur acc (last child-pair) (rest coord)) nil)))))))

(defn get-markup-at [markup coord]
  (comment println "markup:" (pr-str coord))
  (if (empty? coord)
    markup
    (if (component? markup)
      (recur (:tree markup) (rest coord))
      (let [coord-head (first coord)
            child-pair (filter-first
                        (fn [child-entry] (= (get child-entry 0) coord-head))
                        (:children markup))]
        (if (some? child-pair)
          (get-markup-at (get child-pair 1) (rest coord))
          (raise* (str "child not found:" coord (map first (:children markup)))))))))

(defn find-event-target [element coord event-name]
  (let [target-element (get-markup-at element coord), element-exists? (some? target-element)]
    (comment println "target element:" (pr-str event-name))
    (if (and element-exists? (contains? (:event target-element) event-name))
      target-element
      (if (= coord [])
        nil
        (if element-exists?
          (recur element (subvec coord 0 (- (count coord) 1)) event-name)
          nil)))))
