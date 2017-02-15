
(ns respo.controller.client
  (:require [respo.render.patcher :refer [apply-dom-changes]]
            [polyfill.core :refer [read-string*]]
            [respo.util.format :refer [event->string event->edn]]
            [respo.render.make-dom :refer [make-element]]
            [respo.util.information :refer [no-bubble-events]]))

(defn build-listener [event-name deliver-event]
  (fn [event coord]
    (let [simple-event (event->edn event)] (deliver-event coord event-name simple-event))))

(defn release-instance [mount-point] (set! (.-innerHTML mount-point) ""))

(defn patch-instance [changes mount-point deliver-event]
  (let [no-bubble-collection (->> no-bubble-events
                                  (map
                                   (fn [event-name]
                                     [event-name (build-listener event-name deliver-event)]))
                                  (into {}))]
    (apply-dom-changes changes mount-point no-bubble-collection)))

(defn initialize-instance [mount-point deliver-event] )

(defn activate-instance [entire-dom mount-point deliver-event]
  (let [no-bubble-collection (->> no-bubble-events
                                  (map
                                   (fn [event-name]
                                     [event-name (build-listener event-name deliver-event)]))
                                  (into {}))]
    (set! (.-innerHTML mount-point) "")
    (.appendChild mount-point (make-element entire-dom no-bubble-collection))))
