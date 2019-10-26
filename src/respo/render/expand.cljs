
(ns respo.render.expand
  (:require [clojure.string :as string]
            [respo.util.detect :refer [component? element? effect? =seq]]
            [respo.util.list :refer [filter-first pick-attrs filter-first]]
            [respo.schema :as schema]))

(declare render-children)

(declare render-component)

(declare render-element)

(declare render-markup)

(defn render-markup [markup coord comp-coord cursor old-element]
  (cond
    (and (component? markup) (component? old-element))
      (render-component markup coord cursor old-element)
    (and (component? markup) (or (element? old-element) (nil? old-element)))
      (render-component markup coord cursor nil)
    (and (element? markup) (element? old-element))
      (render-element markup coord comp-coord cursor old-element)
    (and (element? markup) (or (component? old-element) (nil? old-element)))
      (render-element markup coord comp-coord cursor nil)
    :else
      (do
       (js/console.log "Markup:" markup)
       (throw (js/Error. (str "expects component or element!"))))))

(defn render-element [markup coord comp-coord cursor old-element]
  (let [children (:children markup)
        child-elements (render-children
                        children
                        coord
                        comp-coord
                        cursor
                        (:children old-element))]
    (comment js/console.log "children should have order:" children child-elements markup)
    (assoc markup :coord coord :children child-elements)))

(defn render-component [markup coord cursor old-element]
  (if (and (some? old-element)
           (= (:name markup) (:name old-element))
           (or (and (empty? (:cursor markup)) (empty? (:cursor old-element)))
               (= (:cursor markup) (:cursor old-element)))
           (=seq (:args markup) (:args old-element)))
    (do (comment println "not changed" (:name markup) (:args markup)) old-element)
    (let [args (:args markup)
          new-coord (conj coord (:name markup))
          new-cursor (or (:cursor markup) cursor)
          render (:render markup)
          half-render (apply render args)
          markup-tree (half-render new-cursor)
          local (if (sequential? markup-tree)
                  (or (:local old-element)
                      (do (println (:name markup) "create local") (atom {})))
                  nil)]
      (comment println "render component" (:name markup) (:name old-element))
      (comment js/console.log markup old-element)
      (comment println "no cache:" coord)
      (cond
        (or (component? markup-tree) (element? markup-tree))
          (merge
           markup
           {:coord coord,
            :tree (render-markup
                   markup-tree
                   new-coord
                   new-coord
                   new-cursor
                   (:tree old-element)),
            :cursor new-cursor,
            :local local})
        (sequential? markup-tree)
          (let [node-tree (filter-first (fn [x] (or (component? x) (element? x))) markup-tree)
                effects-list (->> markup-tree (filter effect?) (vec))]
            (merge
             markup
             {:coord coord,
              :tree (render-markup
                     node-tree
                     new-coord
                     new-coord
                     new-cursor
                     (:tree old-element)),
              :cursor new-cursor,
              :effects effects-list,
              :local local}))
        :else (do (js/console.warn "Unknown markup:" markup) nil)))))

(defn render-children [children coord comp-coord cursor old-children]
  (comment println "render children:" children)
  (let [mapped-cache (into {} old-children)]
    (doall
     (->> children
          (map
           (defn render-child [[k child-element]]
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
