
(ns respo.comp.wrap
  (:require [respo.alias :refer [create-comp div]] [respo.comp.text :refer [comp-text]]))

(def comp-wrap
  (create-comp :wrap (fn [] (fn [state mutate!] (comp-text "pure component component" nil)))))
