
(ns respo.render.expander
  (:require [clojure.string :as string]
            [polyfill.core :refer [io-get-time*]]
            [respo.util.format :refer [purify-element]]
            [respo.util.detect :refer [component? element? =seq]]
            [respo.util.list :refer [filter-first]]))

(declare render-component)

(declare render-children)

(declare render-element)

(declare render-markup)

(defn render-markup [markup coord component-coord cursor old-element]
  (if (component? markup)
    (render-component markup coord cursor old-element)
    (render-element markup coord component-coord cursor old-element)))

(defn render-element [markup coord comp-coord cursor old-element]
  (let [children (:children markup)
        child-elements (render-children
                        children
                        coord
                        comp-coord
                        cursor
                        (:children old-element))]
    (comment
     .log
     js/console
     "children should have order:"
     (pr-str children)
     (pr-str child-elements)
     (pr-str markup))
    (assoc markup :coord coord :children child-elements)))

(defn render-children [children coord comp-coord cursor old-children]
  (comment println "render children:" children)
  (let [mapped-cache (into {} old-children)]
    (->> children
         (map
          (fn [child-entry]
            (let [k (first child-entry)
                  child-element (last child-entry)
                  old-child (get mapped-cache k)]
              (comment
               if
               (nil? old-child)
               (do (println "old child:" coord (some? old-child))))
              [k
               (if (some? child-element)
                 (render-markup child-element (conj coord k) comp-coord cursor old-child)
                 nil)]))))))

(defn render-component [markup coord cursor old-element]
  (if (and (some? old-element)
           (=seq (:args markup) (:args old-element))
           (identical? (:render markup) (:render old-element)))
    (do (comment println "not changed" coord) old-element)
    (let [begin-time (io-get-time*)
          args (:args markup)
          component (first markup)
          new-coord (conj coord (:name markup))
          new-cursor (let [cursor-name (:cursor markup)]
                       (if (some? cursor-name) (conj cursor cursor-name) cursor))
          render (:render markup)
          half-render (apply render args)
          markup-tree (half-render new-cursor)
          tree (render-markup
                markup-tree
                new-coord
                new-coord
                new-cursor
                (:tree old-element))
          cost (- (io-get-time*) begin-time)]
      (comment println "markup tree:" (pr-str markup-tree))
      (comment println "no cache:" coord)
      (assoc markup :coord coord :tree tree :cost cost))))

(defn render-app [markup old-element] (render-markup markup [] [] [] old-element))
