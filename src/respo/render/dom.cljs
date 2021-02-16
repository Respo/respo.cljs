
(ns respo.render.dom
  (:require [clojure.string :as string]
            [respo.util.format
             :refer
             [dashed->camel event->prop ensure-string get-style-value]]
            [respo.util.detect :refer [component?]]))

(defn make-element [virtual-element listener-builder coord]
  (if (component? virtual-element)
    (recur (:tree virtual-element) listener-builder (conj coord (:name virtual-element)))
    (let [tag-name (name (:name virtual-element))
          attrs (:attrs virtual-element)
          style (:style virtual-element)
          children (:children virtual-element)
          element (.createElement js/document tag-name)
          child-elements (->> children
                              (map
                               (fn [[k child]]
                                 (when (some? child)
                                   (make-element child listener-builder (conj coord k))))))]
      (doseq [entry attrs]
        (let [k (dashed->camel (name (first entry))), v (last entry)]
          (if (some? v) (aset element k v))))
      (doseq [entry style]
        (let [style-name (name (first entry)), k (dashed->camel style-name), v (last entry)]
          (aset (aget element "style") k (get-style-value v style-name))))
      (doseq [event-name (keys (:event virtual-element))]
        (let [name-in-string (event->prop event-name)]
          (comment println "listener:" event-name name-in-string)
          (aset
           element
           name-in-string
           (fn [event] ((listener-builder event-name) event coord) (.stopPropagation event)))))
      (doseq [child-element child-elements] (.appendChild element child-element))
      element)))

(defn style->string [styles]
  (->> styles
       (map
        (fn [entry]
          (let [k (first entry)
                style-name (name k)
                v (get-style-value (last entry) style-name)]
            (str (name k) ":" v ";"))))
       (string/join "")))
