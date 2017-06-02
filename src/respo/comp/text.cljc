
(ns respo.comp.text (:require [respo.alias :refer [create-comp span code]]))

(def comp-text
  (create-comp
   :text
   (fn [content style] (fn [cursor] (span {:inner-text content, :style style})))))

(def comp-code
  (create-comp
   :code
   (fn [content style] (fn [cursor] (code {:attrs {:inner-text content}, :style style})))))
