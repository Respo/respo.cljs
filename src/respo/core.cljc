
(ns respo.core
  (:require [respo.alias :refer [create-comp]]
            [respo.render.expander :refer [render-app]]
            [respo.controller.deliver :refer [build-deliver-event]]
            [respo.render.differ :refer [find-element-diffs]]
            [respo.util.format :refer [purify-element mute-element]]
            [respo.controller.client :refer [activate-instance patch-instance]]
            [polyfill.core :refer [log*]]))

(defonce ref-element (atom nil))

(defonce cache-element (atom nil))

(defn render-element [markup] (render-app markup @cache-element))

(defn mount-app! [markup target dispatch!]
  (let [element (render-element markup)
        deliver-event (build-deliver-event ref-element dispatch!)]
    (comment println "mount app")
    (activate-instance (purify-element element) target deliver-event)
    (reset! ref-element element)
    (reset! cache-element element)))

(defn rerender-app! [markup target dispatch!]
  (let [element (render-element markup)
        deliver-event (build-deliver-event ref-element dispatch!)
        changes-ref (atom [])
        collect! (fn [x] (swap! changes-ref conj x))]
    (comment println @ref-element)
    (comment println "Changes:" (pr-str (mapv (partial take 2) @changes-ref)))
    (find-element-diffs collect! [] @ref-element element)
    (patch-instance @changes-ref target deliver-event)
    (reset! ref-element element)
    (reset! cache-element element)))

(defn render! [markup target dispatch]
  (if (some? @ref-element)
    (rerender-app! markup target dispatch)
    (mount-app! markup target dispatch)))

(defn falsify-stage! [target element dispatch!]
  (reset! ref-element (mute-element element))
  (reset! cache-element element))

(defn clear-cache! [] (reset! cache-element nil))
