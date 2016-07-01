
(ns respo.core
  (:require [respo.alias :refer [create-comp div span]]
            [respo.render.expander :refer [render-app]]
            [respo.controller.deliver :refer [build-deliver-event
                                              mutate-factory]]
            [respo.render.differ :refer [find-element-diffs]]
            [respo.util.format :refer [purify-element restrain-element]]
            [respo.controller.client :refer [initialize-instance
                                             activate-instance
                                             patch-instance]]))

(defonce global-element (atom nil))

(defn render-element [markup states-ref]
  (let [build-mutate (mutate-factory global-element states-ref)]
    (render-app markup @states-ref build-mutate @global-element)))

(defn mount-app [markup target dispatch! states-ref]
  (let [element (render-element markup states-ref)
        deliver-event (build-deliver-event
                        global-element
                        states-ref
                        dispatch!)]
    (initialize-instance target deliver-event)
    (activate-instance (purify-element element) target deliver-event)
    (reset! global-element element)))

(defn rerender-app [markup target dispatch! states-ref]
  (let [element (render-element markup states-ref)
        deliver-event (build-deliver-event
                        global-element
                        states-ref
                        dispatch!)
        changes (find-element-diffs [] [] @global-element element)]
    (comment println "changes:" changes)
    (patch-instance changes target deliver-event)
    (reset! global-element element)))

(defn activate-app [markup target dispatch! states-ref]
  (let [element (render-element markup states-ref)
        deaf-element (restrain-element element)
        deliver-event (build-deliver-event
                        global-element
                        states-ref
                        dispatch!)
        changes (find-element-diffs [] [] deaf-element element)]
    (comment
      println
      "changes:"
      (pr-str (map (fn [change] (subvec change 0 2)) changes)))
    (patch-instance changes target deliver-event)
    (reset! global-element element)))

(defn render! [markup target dispatch states-ref]
  (if (some? @global-element)
    (rerender-app markup target dispatch states-ref)
    (mount-app markup target dispatch states-ref)))

(defn render' [markup target dispatch states-ref]
  (comment println "warning: this function is not tested!")
  (if (some? @global-element)
    (rerender-app markup target dispatch states-ref)
    (activate-app markup target dispatch states-ref)))
