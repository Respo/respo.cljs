
(ns respo.comp.text (:require [respo.alias :refer [create-comp span code]]))

(def comp-text
  (create-comp
   :text
   (fn [content style]
     (fn [state mutate!] (span {:attrs {:inner-text content}, :style style})))))

(def comp-code
  (create-comp
   :code
   (fn [content style]
     (fn [state mutate!] (code {:attrs {:inner-text content}, :style style})))))
