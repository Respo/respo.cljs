
(ns respo.comp.space (:require [respo.core :refer [div defcomp]]))

(def style-space {:height 1, :width 1, :display :inline-block})

(defcomp
 comp-space
 (w h)
 (div {:style (if (some? w) (assoc style-space :width w) (assoc style-space :height h))}))

(defn =< [w x] (comp-space w x))
