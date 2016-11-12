
(ns respo.comp.text (:require [respo.alias :refer [create-comp span code]]))

(defn render-code [content style]
  (fn [state mutate!] (code {:style style, :attrs {:inner-text content}})))

(def comp-code (create-comp :code render-code))

(defn render [content style]
  (fn [state mutate!] (span {:style style, :attrs {:inner-text content}})))

(def comp-text (create-comp :text render))
