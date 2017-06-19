
(ns respo.comp.text
  (:require-macros (respo.macros :refer ()))
  (:require [respo.alias :refer [create-comp span code]]))

(def comp-text
  (create-comp
   :text
   (fn [content style] (fn [cursor] (span {:inner-text content, :style style})))))

(def comp-code
  (create-comp
   :code
   (fn [content style] (fn [cursor] (code {:attrs {:inner-text content}, :style style})))))
