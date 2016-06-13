
(ns respo.controller.resolver
  (:require [clojure.string :as string]
            [respo.util.format :refer [purify-element]]
            [respo.util.detect :refer [component? element?]]
            [respo.util.error :refer [raise]]))

(def cached-nodes (atom (list)))

(defn register-node! [markup coord node]
  (swap! cached-nodes (fn [caches] (cons [markup coord node] caches))))

(defn look-up-node [caches markup coord]
  (if (= (count caches) 0)
    nil
    (let [cursor (first caches)
          old-markup (get cursor 0)
          old-coord (get cursor 1)
          old-node (get cursor 2)]
      (if (and (identical? old-markup markup) (= old-coord coord))
        old-node
        (recur (rest caches) markup coord)))))

(defn perform-gc! [])

(defn get-markup-at [markup coord]
  (comment println "get markup:" (pr-str coord))
  (let [maybe-node (look-up-node @cached-nodes markup coord)]
    (if (some? maybe-node)
      maybe-node
      (let [node (if (= coord [])
                   markup
                   (if (component? markup)
                     (get-markup-at (:tree markup) (subvec coord 1))
                     (let [child (->>
                                   (:children markup)
                                   (filter
                                     (fn 
                                       [child-entry]
                                       (=
                                         (first child-entry)
                                         (first coord))))
                                   (first))]
                       (if (some? child)
                         (get-markup-at (val child) (subvec coord 1))
                         (raise
                           (str
                             "child not found:"
                             coord
                             (map first (:children markup))))))))]
        (register-node! markup coord node)
        node))))

(defn find-event-target [element coord event-name]
  (let [target-element (get-markup-at element coord)
        element-exists? (some? target-element)]
    (comment
      println
      "target element:"
      (pr-str (:c-coord target-element) event-name))
    (if (and
          element-exists?
          (contains? (:event target-element) event-name))
      target-element
      (if (= coord [])
        nil
        (if element-exists?
          (recur
            element
            (subvec coord 0 (- (count coord) 1))
            event-name)
          nil)))))
