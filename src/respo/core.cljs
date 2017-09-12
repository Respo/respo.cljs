
(ns respo.core
  (:require [respo.env :refer [element-type]]
            [respo.render.expand :refer [render-app]]
            [respo.controller.resolve :refer [build-deliver-event]]
            [respo.render.diff :refer [find-element-diffs]]
            [respo.util.format :refer [purify-element mute-element]]
            [respo.controller.client :refer [activate-instance! patch-instance!]]
            [respo.util.list :refer [pick-attrs arrange-children]]
            [respo.util.detect :refer [component?]]))

(defonce *changes-logger (atom nil))

(defn create-element [tag-name props & children]
  (let [attrs (pick-attrs props)
        styles (if (contains? props :style) (sort-by first (:style props)) (list))
        event (or (:on props) (:event props) {})
        children (arrange-children children)]
    {:name tag-name,
     :coord nil,
     :attrs attrs,
     :style styles,
     :event event,
     :children children}))

(defonce *dom-element (atom nil))

(defn render-element [markup] (render-app markup @*dom-element))

(defonce *global-element (atom nil))

(defn mount-app! [target markup dispatch!]
  (assert (instance? element-type target) "1st argument should be an element")
  (assert (component? markup) "2nd argument should be a component")
  (let [element (render-element markup)
        deliver-event (build-deliver-event *global-element dispatch!)]
    (comment println "mount app")
    (activate-instance! (purify-element element) target deliver-event)
    (reset! *global-element element)
    (reset! *dom-element element)))

(defn rerender-app! [target markup dispatch!]
  (let [element (render-element markup)
        deliver-event (build-deliver-event *global-element dispatch!)
        *changes (atom [])
        collect! (fn [x] (swap! *changes conj x))]
    (comment println @*global-element)
    (comment println "Changes:" (pr-str (mapv (partial take 2) @*changes)))
    (find-element-diffs collect! [] @*global-element element)
    (let [logger @*changes-logger]
      (if (some? logger) (logger @*global-element element @*changes)))
    (patch-instance! @*changes target deliver-event)
    (reset! *global-element element)
    (reset! *dom-element element)))

(defn render! [target markup dispatch!]
  (if (some? @*global-element)
    (rerender-app! target markup dispatch!)
    (mount-app! target markup dispatch!)))

(defn create-comp [comp-name render]
  (comment println "create component:" comp-name)
  (let [initial-comp {:name comp-name,
                      :coord nil,
                      :args [],
                      :render render,
                      :tree nil,
                      :cost nil,
                      :cursor nil}]
    (fn [& args] (assoc initial-comp :args args))))

(defn realize-ssr! [target markup dispatch!]
  (assert (instance? element-type target) "1st argument should be an element")
  (assert (component? markup) "2nd argument should be a component")
  (let [element (render-element markup)]
    (reset! *global-element (mute-element element))
    (reset! *dom-element element)))

(defn clear-cache! [] (reset! *dom-element nil))
