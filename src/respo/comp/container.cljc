
(ns respo.comp.container
  (:require [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.todolist :refer [comp-todolist]]))

(def style-states {:padding 8})

(def comp-container
  (create-comp
   :container
   (fn [store states]
     (fn [state mutate!]
       (div
        {}
        (comp-todolist store)
        (div {:style style-states} (comp-text (pr-str states) nil)))))))
