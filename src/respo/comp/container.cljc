
(ns respo.comp.container
  (:require [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.todolist :refer [comp-todolist]]))

(defn render [store states]
  (fn [state mutate!] (div {} (comp-todolist store) (comp-text (pr-str states) nil))))

(def comp-container (create-comp :container render))
