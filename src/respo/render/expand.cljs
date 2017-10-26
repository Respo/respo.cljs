
(ns respo.render.expand
  (:require [clojure.string :as string]
            [polyfill.core :refer [io-get-time*]]
            [respo.util.detect :refer [component? element? dsl? =seq]]
            [respo.util.list :refer [filter-first pick-attrs arrange-children-old]]
            [respo.util.alias :refer [parse-alias]]
            [respo.schema :as schema]))

(declare render-component)

(declare render-children)

(declare render-element)

(declare render-markup)

(declare render-dsl)

(defn render-dsl [markup coord comp-coord cursor old-element]
  (comment println "render DSL:" markup)
  (if (zero? (count markup)) (throw (js/Error. (str "Empty markup: " markup))))
  (if (fn? (nth markup 0))
    (throw js/Error. "Respo does not support [c x] for component, please use (c x)"))
  (let [alias (nth markup 0)
        alias-detail (parse-alias (name alias))
        has-props? (and (>= (count markup) 2)
                        (map? (nth markup 1))
                        (not (component? (nth markup 1))))
        props (if has-props? (nth markup 1) {})
        children (arrange-children-old
                  (->> (subvec markup (if has-props? 2 1))
                       (map
                        (fn [x]
                          (if (string? x)
                            (merge schema/element {:name :span, :attrs {:inner-text x}})
                            x)))))]
    (comment println "children to render:" children)
    {:name (:name alias-detail),
     :coord coord,
     :attrs (pick-attrs (merge (dissoc alias-detail :name) props)),
     :style (if (contains? props :style) (sort-by first (:style props)) (list)),
     :event (or (:on props) (:event props) {}),
     :children (render-children children coord comp-coord cursor (:children old-element))}))

(defn render-markup [markup coord comp-coord cursor old-element]
  (comment println "render markup:" markup)
  (if (dsl? markup)
    (render-dsl markup coord comp-coord cursor old-element)
    (if (component? markup)
      (render-component markup coord cursor old-element)
      (render-element markup coord comp-coord cursor old-element))))

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
    (doall
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
                  nil)])))))))

(defn render-component [markup coord cursor old-element]
  (if (and (some? old-element)
           (=seq (:args markup) (:args old-element))
           (identical? (:render markup) (:render old-element)))
    (do (comment println "not changed" coord) old-element)
    (let [begin-time (io-get-time*)
          args (:args markup)
          component (first markup)
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
          cost (- (io-get-time*) begin-time)]
      (comment println "markup tree:" (pr-str markup-tree))
      (comment println "no cache:" coord)
      (assoc markup :coord coord :tree tree :cost cost :cursor new-cursor))))

(defn render-app [markup old-element] (render-markup markup [] [] [] old-element))
