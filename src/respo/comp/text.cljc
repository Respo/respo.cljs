
(ns respo.comp.text (:require [respo.alias :refer [create-comp span code]]))

(def comp-code
  (create-comp
   :code
   (fn [content style]
     (fn [state mutate!] (code {:style style, :attrs {:inner-text content}})))))

(def comp-text
  (create-comp
   :text
   (fn [content style]
     (fn [state mutate!] (span {:style style, :attrs {:inner-text content}})))))
