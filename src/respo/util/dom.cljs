
(ns respo.util.dom (:require [clojure.string :as string]))

(defn compare-to-dom! [vdom element]
  (comment println "compare" (:name vdom) (map :name (vals (:children vdom))))
  (comment .log js/console element)
  (let [virtual-name (name (:name vdom)), real-name (string/lower-case (.-tagName element))]
    (when (not= virtual-name real-name)
      (.warn js/console "Names not matching:" (dissoc vdom :children) element)))
  (if (not= (count (:children vdom)) (.-length (.-children element)))
    (do
     (.error js/console "Does not have same count of children:")
     (.log js/console "virtual:" (:children vdom))
     (.log js/console "real:" (.-children element)))
    (let [real-children (.-children element)]
      (loop [acc 0, other-children (:children vdom)]
        (when (not (empty? other-children))
          (compare-to-dom! (last (first other-children)) (aget real-children acc))
          (recur (inc acc) (rest other-children)))))))

(def shared-canvas-context
  (if (and (exists? js/window) (exists? js/document))
    (.getContext (.createElement js/document "canvas") "2d")
    nil))

(defn text-width [content font-size font-family]
  (if (some? shared-canvas-context)
    (do
     (set! (.-font shared-canvas-context) (str font-size "px " font-family))
     (.-width (.measureText shared-canvas-context content)))
    nil))
