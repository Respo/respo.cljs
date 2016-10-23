
(ns respo.comp.text
  (:require [respo.alias :refer [create-comp span]]))

(defn render [content style]
  (fn [state mutate!] (span {:style style, :attrs {:inner-text content}})))

(def comp-text (create-comp :text render))
