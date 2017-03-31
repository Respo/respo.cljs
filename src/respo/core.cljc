
(ns respo.core
  (:require [respo.alias :refer [create-comp div span]]
            [respo.render.expander :refer [render-app]]
            [respo.controller.deliver :refer [build-deliver-event]]
            [respo.render.differ :refer [find-element-diffs]]
            [respo.util.format :refer [purify-element mute-element]]
            [respo.controller.client :refer [activate-instance patch-instance]]
            [polyfill.core :refer [log*]]
            [respo.util.gc :refer [find-removed apply-remove]]))

(defonce global-element (atom nil))

(defonce cache-element (atom nil))

(defn render-element [markup] (render-app markup @cache-element))

(defn mount-app! [markup target dispatch!]
  (let [element (render-element markup)
        deliver-event (build-deliver-event global-element dispatch!)]
    (comment println "mount app")
    (activate-instance (purify-element element) target deliver-event)
    (reset! global-element element)
    (reset! cache-element element)))

(defn rerender-app! [markup target dispatch!]
  (let [element (render-element markup)
        deliver-event (build-deliver-event global-element dispatch!)
        changes-ref (atom [])
        collect! (fn [x] (swap! changes-ref conj x))]
    (comment println @global-element)
    (comment println "Changes:" (pr-str (mapv (partial take 2) @changes-ref)))
    (find-element-diffs collect! [] @global-element element)
    (patch-instance @changes-ref target deliver-event)
    (reset! global-element element)
    (reset! cache-element element)))

(defn render! [markup target dispatch]
  (if (some? @global-element)
    (rerender-app! markup target dispatch)
    (mount-app! markup target dispatch)))

(defn falsify-stage! [target element dispatch!]
  (reset! global-element (mute-element element))
  (reset! cache-element element))

(defn clear-cache! [] (reset! cache-element nil))

(defn gc-states! [states-ref]
  (let [removed-paths (find-removed @states-ref @global-element [])]
    (if (not (empty? removed-paths))
      (reset! states-ref (apply-remove @states-ref removed-paths)))))
