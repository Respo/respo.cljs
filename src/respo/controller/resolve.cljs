
(ns respo.controller.resolve
  (:require [clojure.string :as string]
            [respo.util.detect :refer [component? element?]]
            [respo.util.list :refer [filter-first]]))

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
          (throw (js/Error. (str "child not found:" coord (map first (:children markup))))))))))

(defn find-event-target [element coord event-name]
  (let [target-element (get-markup-at element coord), element-exists? (some? target-element)]
    (comment println "target element:" (pr-str event-name))
    (if (and element-exists? (some? (get (:event target-element) event-name)))
      target-element
      (if (= coord [])
        nil
        (if element-exists?
          (recur element (subvec coord 0 (- (count coord) 1)) event-name)
          nil)))))

(defn build-deliver-event [*global-element dispatch!]
  (fn [coord event-name simple-event]
    (let [target-element (find-event-target @*global-element coord event-name)
          target-listener (get (:event target-element) event-name)]
      (if (some? target-listener)
        (do
         (comment println "listener found:" coord event-name)
         (target-listener simple-event dispatch!))
        (comment println "found no listener:" coord event-name)))))
