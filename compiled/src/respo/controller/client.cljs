
(ns respo.controller.client
  (:require [respo.render.patcher :refer [apply-dom-changes]]
            [cljs.reader :refer [read-string]]
            [respo.util.time :refer [io-get-time]]
            [respo.util.format :refer [event->string event->edn]]
            [respo.render.make-dom :refer [make-element]]
            [respo.util.information :refer [bubble-events
                                            no-bubble-events]]))

(defonce dom-registry (atom {}))

(defn read-coord [target]
  (read-string (aget (.-dataset target) "coord")))

(defn read-events [target]
  (read-string (aget (.-dataset target) "event")))

(defn maybe-trigger [target event-name simple-event deliver-event]
  (let [coord (read-coord target) active-events (read-events target)]
    (comment .log js/console coord active-events event-name)
    (if (some
          (fn [defined-event] (= event-name defined-event))
          active-events)
      (deliver-event coord event-name simple-event)
      (if (> (count coord) 1)
        (recur
          (.-parentElement target)
          event-name
          simple-event
          deliver-event)))))

(defn build-listener [event-name deliver-event]
  (fn [event]
    (let [target (.-target event)
          coord (read-coord target)
          active-events (read-events target)
          simple-event (event->edn event)]
      (maybe-trigger target event-name simple-event deliver-event))))

(defn activate-instance [entire-dom mount-point deliver-event]
  (let [no-bubble-collection (->>
                               no-bubble-events
                               (map
                                 (fn 
                                   [event-name]
                                   [event-name
                                    (build-listener
                                      event-name
                                      deliver-event)]))
                               (into {}))]
    (set! (.-innerHTML mount-point) "")
    (.appendChild
      mount-point
      (make-element entire-dom no-bubble-collection))))

(defn patch-instance [changes mount-point deliver-event]
  (let [no-bubble-collection (->>
                               no-bubble-events
                               (map
                                 (fn 
                                   [event-name]
                                   [event-name
                                    (build-listener
                                      event-name
                                      deliver-event)]))
                               (into {}))]
    (apply-dom-changes changes mount-point no-bubble-collection)))

(defn initialize-instance [mount-point deliver-event]
  (let [bubble-collection (->>
                            bubble-events
                            (map
                              (fn [event-name] [event-name
                                                (build-listener
                                                  event-name
                                                  deliver-event)]))
                            (into {}))]
    (doall
      (->>
        bubble-collection
        (map
          (fn [entry]
            (let [event-string (name (key entry)) listener (val entry)]
              (.addEventListener
                mount-point
                event-string
                listener))))))
    (swap!
      dom-registry
      assoc
      mount-point
      {:listeners bubble-collection})))

(defn release-instance [mount-point]
  (set! (.-innerHTML mount-point) "")
  (doall
    (->>
      (:listeners (get @dom-registry mount-point))
      (map
        (fn [entry]
          (let [event-string (name (key entry)) listener (key entry)]
            (.removeEventListener
              mount-point
              event-string
              listener))))))
  (swap! dom-registry dissoc mount-point))
