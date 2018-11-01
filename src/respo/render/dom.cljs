
(ns respo.render.dom
  (:require [clojure.string :as string]
            [respo.util.format :refer [dashed->camel event->prop ensure-string]]))

(defn make-element [virtual-element listener-builder]
  (let [tag-name (name (:name virtual-element))
        attrs (:attrs virtual-element)
        style (:style virtual-element)
        children (:children virtual-element)
        element (.createElement js/document tag-name)
        child-elements (->> children
                            (map (fn [entry] (make-element (last entry) listener-builder))))]
    (doseq [entry attrs]
      (let [k (dashed->camel (name (first entry))), v (last entry)]
        (if (some? v) (aset element k v))))
    (doseq [entry style]
      (let [k (dashed->camel (name (first entry))), v (last entry)]
        (aset (aget element "style") k (if (keyword? v) (name v) v))))
    (doseq [event-name (:event virtual-element)]
      (let [name-in-string (event->prop event-name)]
        (comment println "listener:" event-name name-in-string)
        (aset
         element
         name-in-string
         (fn [event]
           ((listener-builder event-name) event (:coord virtual-element))
           (.stopPropagation event)))))
    (doseq [child-element child-elements] (.appendChild element child-element))
    element))

(defn style->string [styles]
  (->> styles
       (map
        (fn [entry]
          (let [k (first entry), v (ensure-string (last entry))] (str (name k) ":" v ";"))))
       (string/join "")))
