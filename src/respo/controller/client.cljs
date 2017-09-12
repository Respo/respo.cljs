
(ns respo.controller.client
  (:require [respo.render.patch :refer [apply-dom-changes]]
            [polyfill.core :refer [read-string*]]
            [respo.util.format :refer [event->string event->edn]]
            [respo.render.dom :refer [make-element]]))

(defn build-listener [event-name deliver-event]
  (fn [event coord]
    (let [simple-event (event->edn event)] (deliver-event coord event-name simple-event))))

(defn patch-instance! [changes mount-point deliver-event]
  (let [listener-builder (fn [event-name] (build-listener event-name deliver-event))]
    (apply-dom-changes changes mount-point listener-builder)))

(defn activate-instance! [entire-dom mount-point deliver-event]
  (let [listener-builder (fn [event-name] (build-listener event-name deliver-event))]
    (set! (.-innerHTML mount-point) "")
    (.appendChild mount-point (make-element entire-dom listener-builder))))
