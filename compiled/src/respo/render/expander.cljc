
(ns respo.render.expander
  (:require [clojure.string :as string]
            [respo.util.time :refer [io-get-time]]
            [respo.util.format :refer [purify-element]]
            [respo.util.detect :refer [component? element? =vector]]
            [respo.util.error :refer [raise]]
            [respo.util.list :refer [filter-first]]))

(defn keyword->string [x] (subs (str x) 1))

(declare render-component)

(declare render-element)

(defn render-markup [markup
                     states
                     build-mutate
                     coord
                     component-coord
                     old-element]
  (if (component? markup)
    (render-component markup states build-mutate coord old-element)
    (render-element
      markup
      states
      build-mutate
      coord
      component-coord
      old-element)))

(defn render-children [children
                       states
                       build-mutate
                       coord
                       comp-coord
                       old-children]
  (comment println "render children:" children)
  (->>
    children
    (mapv
      (fn [child-entry]
        (let [k (first child-entry)
              child-element (last child-entry)
              inner-states (or (get states k) {})
              old-pair (filter-first
                         (fn [pair] (identical? (first pair) k))
                         old-children)
              old-child (get old-pair 1)]
          (comment
            if
            (nil? old-child)
            (do (println "old child:" coord (some? old-child))))
          [k
           (if (some? child-element)
             (render-markup
               child-element
               inner-states
               build-mutate
               (conj coord k)
               comp-coord
               old-child)
             nil)])))))

(defn render-element [markup
                      states
                      build-mutate
                      coord
                      comp-coord
                      old-element]
  (let [children (:children markup)
        child-elements (render-children
                         children
                         states
                         build-mutate
                         coord
                         comp-coord
                         (:children old-element))]
    (comment
      .log
      js/console
      "children should have order:"
      (pr-str children)
      (pr-str child-elements)
      (pr-str markup))
    (assoc markup :coord coord :children child-elements)))

(defn render-component [markup states build-mutate coord old-element]
  (let [raw-states (get states (:name markup))]
    (if (and
          (some? old-element)
          (identical? raw-states (:raw-states old-element))
          (=vector (:args markup) (:args old-element))
          (identical? (:render markup) (:render old-element)))
      (do (comment println "not changed" coord) old-element)
      (let [begin-time (io-get-time)
            args (:args markup)
            component (first markup)
            init-state (:init-state markup)
            new-coord (conj coord (:name markup))
            inner-states (or raw-states {})
            state (or (get inner-states 'data) (apply init-state args))
            render (:render markup)
            half-render (apply render args)
            mutate (build-mutate new-coord)
            markup-tree (half-render state mutate)
            tree (render-element
                   markup-tree
                   inner-states
                   build-mutate
                   new-coord
                   new-coord
                   (:tree old-element))
            cost (- (io-get-time) begin-time)]
        (comment println "markup tree:" (pr-str markup-tree))
        (comment println "component state:" coord states)
        (comment println "no cache:" coord)
        (assoc
          markup
          :coord
          coord
          :tree
          tree
          :cost
          cost
          :raw-states
          raw-states)))))

(defn render-app [markup states build-mutate old-element]
  (comment println "render loop, states:" (pr-str states))
  (render-markup markup states build-mutate [] [] old-element))
