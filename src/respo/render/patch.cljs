
(ns respo.render.patch
  (:require [clojure.string :as string]
            [respo.util.format :refer [dashed->camel event->prop ensure-string]]
            [respo.render.dom :refer [make-element style->string]]
            [respo.schema.op :as op]))

(defn add-element [target op listener-builder]
  (let [new-element (make-element op listener-builder)
        parent-element (.-parentElement target)]
    (.insertBefore parent-element new-element target)))

(defn add-event [target op-data listener-builder]
  (let [[event-name coord] op-data, event-prop (event->prop event-name)]
    (aset
     target
     event-prop
     (fn [event] ((listener-builder event-name) event coord) (.stopPropagation event)))))

(defn add-prop [target op]
  (let [prop-name (dashed->camel (name (key op))), prop-value (val op)]
    (case prop-name
      "style" (aset target prop-name (style->string prop-value))
      (aset target prop-name prop-value))))

(defn add-style [target op]
  (let [style-name (dashed->camel (name (key op))), style-value (ensure-string (val op))]
    (aset (.-style target) style-name style-value)))

(defn append-element [target op listener-builder]
  (let [new-element (make-element op listener-builder)] (.appendChild target new-element)))

(defn find-target [root coord]
  (if (empty? coord)
    root
    (let [index (first coord), child (aget (.-children root) index)]
      (recur child (rest coord)))))

(defn replace-element [target op listener-builder]
  (let [new-element (make-element op listener-builder)
        parent-element (.-parentElement target)]
    (.insertBefore parent-element new-element target)
    (.remove target)))

(defn replace-prop [target op]
  (let [prop-name (dashed->camel (name (key op))), prop-value (val op)]
    (if (= prop-name "value")
      (if (not= prop-value (.-value target)) (aset target prop-name prop-value))
      (aset target prop-name prop-value))))

(defn replace-style [target op]
  (let [style-name (dashed->camel (name (key op))), style-value (ensure-string (val op))]
    (aset (.-style target) style-name style-value)))

(defn rm-element [target op]
  (if (some? target)
    (.remove target)
    (.warn js/console "Respo: Element already removed! Probably by :inner-text.")))

(defn rm-event [target event-name]
  (let [event-prop (event->prop event-name)] (aset target event-prop nil)))

(defn rm-prop [target op] (let [k (dashed->camel (name op))] (aset target k nil)))

(defn rm-style [target op]
  (let [style-name (dashed->camel (name op))] (aset (.-style target) style-name nil)))

(defn run-effect [target op-data] (op-data target))

(defn apply-dom-changes [changes mount-point listener-builder]
  (let [root (.-firstElementChild mount-point)]
    (doseq [op changes]
      (let [[op-type coord op-data] op, target (find-target root coord)]
        (comment println op-type target op-data)
        (cond
          (= op-type op/replace-prop) (replace-prop target op-data)
          (= op-type op/add-prop) (add-prop target op-data)
          (= op-type op/rm-prop) (rm-prop target op-data)
          (= op-type op/add-style) (add-style target op-data)
          (= op-type op/replace-style) (replace-style target op-data)
          (= op-type op/rm-style) (rm-style target op-data)
          (= op-type op/set-event) (add-event target op-data listener-builder)
          (= op-type op/rm-event) (rm-event target op-data)
          (= op-type op/add-element) (add-element target op-data listener-builder)
          (= op-type op/rm-element) (rm-element target op-data)
          (= op-type op/replace-element) (replace-element target op-data listener-builder)
          (= op-type op/append-element) (append-element target op-data listener-builder)
          (= op-type op/run-effect) (run-effect target op-data)
          :else (println "not implemented:" op-type coord op-data))))))
