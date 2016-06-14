
(ns respo.render.expander
  (:require [clojure.string :as string]
            [respo.util.time :refer [io-get-time]]
            [respo.util.format :refer [purify-element]]
            [respo.util.detect :refer [component? element? =vector]]))

(defn keyword->string [x] (subs (str x) 1))

(declare render-component)

(declare render-element)

(defn render-markup [markup states build-mutate coord component-coord]
  (if (component? markup)
    (render-component markup states build-mutate coord)
    (render-element markup states build-mutate coord component-coord)))

(defn render-children [children states build-mutate coord comp-coord]
  (comment println "render children:" children)
  (->>
    children
    (map
      (fn [child-entry]
        (let [k (first child-entry)
              child-element (last child-entry)
              inner-states (or (get states k) {})]
          [k
           (if (some? child-element)
             (render-markup
               child-element
               inner-states
               build-mutate
               (conj coord k)
               comp-coord)
             nil)])))
    (filter (fn [entry] (some? (last entry))))))

(defn render-element [markup states build-mutate coord comp-coord]
  (let [children (:children markup)
        child-elements (render-children
                         children
                         states
                         build-mutate
                         coord
                         comp-coord)]
    (comment
      println
      "children should have order:"
      (pr-str children)
      (pr-str child-elements)
      (pr-str markup))
    (assoc
      markup
      :coord
      coord
      :c-coord
      comp-coord
      :children
      child-elements)))

(defonce component-cached (atom []))

(defn get-component [cache-list markup states coord]
  (comment println "compare markup:" (:name markup) (first cache-list))
  (comment println (:args markup))
  (if (= (count cache-list) 0)
    nil
    (let [cursor (get cache-list 0)
          old-name (get cursor 0)
          old-args (get cursor 1)
          old-states (get cursor 2)
          old-coord (get cursor 3)
          old-result (get cursor 4)]
      (if (and
            (identical? states old-states)
            (identical? (:name markup) old-name)
            (=vector coord old-coord)
            (=vector (:args markup) old-args))
        old-result
        (recur (subvec cache-list 1) markup states coord)))))

(defn register-component [markup states coord result]
  (swap!
    component-cached
    conj
    [(:name markup) (:args markup) states coord result]))

(defn perform-gc! []
  (if (> (count @component-cached) 800)
    (do (swap! component-cached subvec 0 400))))

(defn render-component [markup states build-mutate coord]
  (let [maybe-component (get-component
                          @component-cached
                          markup
                          states
                          coord)]
    (if (some? maybe-component)
      (do (comment println "hitted cache:" coord) maybe-component)
      (let [begin-time (io-get-time)
            args (:args markup)
            component (first markup)
            init-state (:init-state markup)
            new-coord (conj coord (:name markup))
            inner-states (or (get states (:name markup)) {})
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
                   new-coord)
            cost (- (io-get-time) begin-time)
            result (assoc markup :coord coord :tree tree :cost cost)]
        (comment println "markup tree:" (pr-str markup-tree))
        (comment println "component state:" coord states)
        (comment println "no cache:" coord)
        (register-component markup states coord result)
        result))))

(defn render-app [markup states build-mutate]
  (comment println "render loop, states:" (pr-str states))
  (perform-gc!)
  (render-markup markup states build-mutate [] []))
