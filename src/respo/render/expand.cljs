
(ns respo.render.expand
  (:require [clojure.string :as string]
            [respo.util.detect :refer [component? element? effect? =seq]]
            [respo.util.list :refer [filter-first pick-attrs filter-first]]
            [respo.schema :as schema]))

(declare render-children)

(declare render-component)

(declare render-element)

(declare render-markup)

(defn render-markup [markup coord]
  (cond
    (component? markup) (render-component markup coord)
    (element? markup) (render-element markup coord)
    :else
      (do
       (js/console.log "Markup:" markup)
       (throw (js/Error. (str "expects component or element!"))))))

(defn render-element [markup coord]
  (let [children (:children markup), child-elements (render-children children coord)]
    (comment js/console.log "children should have order:" children child-elements markup)
    (assoc markup :coord coord :children child-elements)))

(defn render-component [markup coord]
  (let [new-coord (conj coord (:name markup)), markup-tree (:tree markup)]
    (comment println "render component" (:name markup))
    (comment println "no cache:" coord)
    (cond
      (or (component? markup-tree) (element? markup-tree))
        (merge markup {:coord coord, :tree (render-markup markup-tree new-coord)})
      (sequential? markup-tree)
        (let [node-tree (filter-first (fn [x] (or (component? x) (element? x))) markup-tree)
              effects-list (->> markup-tree (filter effect?) (vec))]
          (merge
           markup
           {:coord coord, :tree (render-markup node-tree new-coord), :effects effects-list}))
      :else (do (js/console.warn "Unknown markup:" markup) nil))))

(defn render-children [children coord]
  (comment println "render children:" children)
  (doall
   (->> children
        (map
         (fn [[k child-element]]
           [k (if (some? child-element) (render-markup child-element (conj coord k)) nil)])))))

(defn render-app [markup] (render-markup markup []))
