
(ns respo.render.patcher
  (:require [clojure.string :as string]
            [polyfill.core :refer [read-string*]]
            [respo.util.format :refer [dashed->camel event->prop ensure-string]]
            [respo.render.make-dom :refer [make-element style->string]]))

(defn rm-event [target event-name]
  (let [event-prop (event->prop event-name)] (aset target event-prop nil)))

(defn replace-style [target op]
  (let [style-name (dashed->camel (name (key op))), style-value (ensure-string (val op))]
    (aset (.-style target) style-name style-value)))

(defn replace-element [target op listener-builder]
  (let [new-element (make-element op listener-builder)
        parent-element (.-parentElement target)]
    (.insertBefore parent-element new-element target)
    (.remove target)))

(defn append-element [target op listener-builder]
  (let [new-element (make-element op listener-builder)] (.appendChild target new-element)))

(defn add-event [target op-data listener-builder]
  (let [[event-name coord] op-data, event-prop (event->prop event-name)]
    (aset
     target
     event-prop
     (fn [event] ((listener-builder event-name) event coord) (.stopPropagation event)))))

(defn rm-prop [target op] (aset target (dashed->camel (name op)) nil))

(defn add-prop [target op]
  (let [prop-name (dashed->camel (name (key op))), prop-value (val op)]
    (case prop-name
      "style" (aset target prop-name (style->string prop-value))
      (aset target prop-name prop-value))))

(defn replace-prop [target op]
  (let [prop-name (dashed->camel (name (key op))), prop-value (val op)]
    (if (= prop-name "value")
      (if (not= prop-value (.-value target)) (aset target prop-name prop-value))
      (aset target prop-name prop-value))))

(defn add-style [target op]
  (let [style-name (dashed->camel (name (key op))), style-value (ensure-string (val op))]
    (aset (.-style target) style-name style-value)))

(defn rm-style [target op]
  (let [style-name (dashed->camel (name op))] (aset (.-style target) style-name nil)))

(defn rm-element [target op] (.remove target))

(defn find-target [root coord]
  (if (empty? coord)
    root
    (let [index (first coord), child (aget (.-children root) index)]
      (recur child (rest coord)))))

(defn add-element [target op listener-builder]
  (let [new-element (make-element op listener-builder)
        parent-element (.-parentElement target)]
    (.insertBefore parent-element new-element target)))

(defn apply-dom-changes [changes mount-point listener-builder]
  (let [root (.-firstChild mount-point)]
    (doseq [op changes]
      (let [[op-type coord op-data] op, target (find-target root coord)]
        (comment println op-type target op-data)
        (case op-type
          :replace-prop (replace-prop target op-data)
          :add-prop (add-prop target op-data)
          :rm-prop (rm-prop target op-data)
          :add-style (add-style target op-data)
          :replace-style (replace-style target op-data)
          :rm-style (rm-style target op-data)
          :add-event (add-event target op-data listener-builder)
          :rm-event (rm-event target op-data)
          :add (add-element target op-data listener-builder)
          :rm (rm-element target op-data)
          :replace (replace-element target op-data listener-builder)
          :append (append-element target op-data listener-builder)
          (println "not implemented:" op-type))))))
