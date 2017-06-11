
(ns respo.controller.deliver
  (:require [respo.controller.resolver
             :refer
             [find-event-target get-markup-at get-component-at]]
            [respo.util.detect :refer [component? element?]]))

(defn all-component-coords [markup]
  (if (component? markup)
    (cons (:coord markup) (all-component-coords (:tree markup)))
    (->> (:children markup)
         (map (fn [child-entry] (all-component-coords (val child-entry))))
         (apply concat))))

(defn build-deliver-event [ref-element dispatch!]
  (fn [coord event-name simple-event]
    (let [target-element (find-event-target @ref-element coord event-name)
          target-component (get-component-at @ref-element coord)
          target-listener (get (:event target-element) event-name)]
      (if (some? target-listener)
        (do
         (comment println "listener found:" coord event-name)
         (target-listener simple-event dispatch!))
        (comment println "found no listener:" coord event-name)))))
