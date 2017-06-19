
(ns respo.comp.wrap
  (:require-macros (respo.macros :refer (div)))
  (:require [respo.alias :refer [create-comp]] [respo.comp.text :refer [comp-text]]))

(def comp-wrap
  (create-comp :wrap (fn [] (fn [cursor] (comp-text "pure component component" nil)))))
