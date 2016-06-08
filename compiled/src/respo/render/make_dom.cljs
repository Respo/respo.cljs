
(ns respo.render.make-dom
  (:require [clojure.string :as string]
            [respo.util.format :refer [dashed->camel event->prop]]))

(defn style->string [styles]
  (string/join
    ""
    (->>
      styles
      (map
        (fn [entry]
          (let [k (first entry) v (last entry)]
            (str (name k) ":" v ";")))))))

(defn make-element [virtual-element no-bubble-collection]
  (let [tag-name (name (:name virtual-element))
        attrs (:attrs virtual-element)
        style (:style virtual-element)
        children (into (sorted-map) (:children virtual-element))
        element (.createElement js/document tag-name)
        child-elements (->>
                         children
                         (map
                           (fn [entry]
                             (let [item (last entry)]
                               (if
                                 (string? item)
                                 (.createTextNode js/document item)
                                 (make-element
                                   item
                                   no-bubble-collection))))))
        event-keys (into [] (keys (:event virtual-element)))]
    (set!
      (->> element (.-dataset) (.-coord))
      (pr-str (:coord virtual-element)))
    (set! (->> element (.-dataset) (.-event)) (pr-str event-keys))
    (doall
      (->>
        attrs
        (map
          (fn [entry]
            (let [k (dashed->camel (name (first entry)))
                  v (last entry)]
              (.setAttribute element k v)
              (aset element k v))))))
    (.setAttribute element "style" (style->string style))
    (doall
      (->>
        (:event virtual-element)
        (map
          (fn [entry]
            (comment .log js/console "Looking into event:" entry)
            (let [event-name (key entry)
                  name-in-string (event->prop event-name)
                  maybe-listener (get no-bubble-collection event-name)]
              (if (some? maybe-listener)
                (aset element name-in-string maybe-listener)))))))
    (doall
      (->>
        child-elements
        (map
          (fn [child-element] (.appendChild element child-element)))))
    element))
