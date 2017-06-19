
(ns respo.comp.space
  (:require-macros (respo.macros :refer (div)))
  (:require [respo.alias :refer [create-comp]]))

(defn style-space [w h]
  (if (some? w)
    {:width w, :height "1px", :display :inline-block}
    {:width "1px", :height h, :display :inline-block}))

(def comp-space
  (create-comp :space (fn [w h] (fn [cursor] (div {:style (style-space w h)})))))
