
(ns respo.render.expand
  (:require [clojure.string :as string]
            [respo.util.detect :refer [component? element? =seq]]
            [respo.util.list :refer [filter-first pick-attrs]]
            [respo.schema :as schema]))

(declare render-children)

(declare render-component)

(declare render-element)

(declare render-markup)

(defn render-markup [markup coord comp-coord cursor old-element]
  (comment println "render markup:" markup)
  (assert (map? markup) (str "expects component or element, but got: " (pr-str markup)))
  (if (component? markup)
    (render-component markup coord cursor old-element)
    (render-element markup coord comp-coord cursor old-element)))

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

(defn render-component [markup coord cursor old-element]
  (assert (map? markup) (str "component markup should be a map, but got: " (pr-str markup)))
  (if (and (some? old-element)
           (= (:name markup) (:name old-element))
           (=seq (:args markup) (:args old-element)))
    (do (comment println "not changed" (:name markup) (:args markup)) old-element)
    (let [begin-time (.valueOf (js/Date.))
          args (:args markup)
          new-coord (conj coord (:name markup))
          new-cursor (or (:cursor markup) cursor)
          render (:render markup)
          half-render (apply render args)
          markup-tree (half-render new-cursor)
          tree (render-markup
                markup-tree
                new-coord
                new-coord
                new-cursor
                (:tree old-element))
          cost (- (.valueOf (js/Date.)) begin-time)]
      (comment println "markup tree:" (pr-str markup-tree))
      (comment println "no cache:" coord)
      (assoc markup :coord coord :tree tree :cost cost :cursor new-cursor))))

(defn render-children [children coord comp-coord cursor old-children]
  (comment println "render children:" children)
  (let [mapped-cache (into {} old-children)]
    (doall
     (->> children
          (map
           (fn [[k child-element]]
             (let [old-child (get mapped-cache k)]
               (comment
                if
                (nil? old-child)
                (do (println "old child:" coord (some? old-child))))
               [k
                (if (some? child-element)
                  (render-markup child-element (conj coord k) comp-coord cursor old-child)
                  nil)])))))))

(defn render-app [markup old-element] (render-markup markup [] [] [] old-element))
