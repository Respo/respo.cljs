
(ns respo.render.patch
  (:require [clojure.string :as string]
            [respo.util.format
             :refer
             [dashed->camel event->prop ensure-string get-style-value]]
            [respo.render.dom :refer [make-element style->string]]
            [respo.schema.op :as op]))

(defn add-element [target op listener-builder coord]
  (let [new-element (make-element op listener-builder coord)
        parent-element (.-parentElement target)]
    (.insertBefore parent-element new-element target)))

(defn add-event [target event-name listener-builder coord]
  (let [event-prop (event->prop event-name)]
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
  (let [style-name (name (key op))
        style-prop (dashed->camel style-name)
        style-value (get-style-value (val op) style-prop)]
    (aset (.-style target) style-prop style-value)))

(defn append-element [target op listener-builder coord]
  (let [new-element (make-element op listener-builder coord)]
    (.appendChild target new-element)))

(defn find-target [root coord]
  (cond
    (empty? coord) root
    :else
      (let [index (first coord), child (aget (.-children root) index)]
        (if (some? child) (recur child (rest coord)) nil))))

(defn replace-element [target op listener-builder coord]
  (let [new-element (make-element op listener-builder coord)
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

(defn run-effect [target op-data coord]
  (if (some? target)
    (op-data target)
    (js/console.warn "Unknown effects target:" (pr-str coord))))

(defn apply-dom-changes [changes mount-point listener-builder]
  (let [root (.-firstElementChild mount-point)]
    (doseq [op changes]
      (let [[op-type coord n-coord op-data] op, target (find-target root n-coord)]
        (comment println op-type target op-data)
        (cond
          (= op-type op/replace-prop) (replace-prop target op-data)
          (= op-type op/add-prop) (add-prop target op-data)
          (= op-type op/rm-prop) (rm-prop target op-data)
          (= op-type op/add-style) (add-style target op-data)
          (= op-type op/replace-style) (replace-style target op-data)
          (= op-type op/rm-style) (rm-style target op-data)
          (= op-type op/set-event) (add-event target op-data listener-builder coord)
          (= op-type op/rm-event) (rm-event target op-data)
          (= op-type op/add-element) (add-element target op-data listener-builder coord)
          (= op-type op/rm-element) (rm-element target op-data)
          (= op-type op/replace-element)
            (replace-element target op-data listener-builder coord)
          (= op-type op/append-element)
            (append-element target op-data listener-builder coord)
          (= op-type op/effect-mount) (run-effect target op-data coord)
          (= op-type op/effect-unmount) (run-effect target op-data coord)
          (= op-type op/effect-update) (run-effect target op-data coord)
          (= op-type op/effect-before-update) (run-effect target op-data coord)
          :else (println "not implemented:" op-type coord op-data))))))
