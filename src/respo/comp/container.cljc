
(ns respo.comp.container
  (:require [respo.alias :refer [create-comp with-cursor div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.todolist :refer [comp-todolist]]))

(def style-states {:padding 8})

(def comp-container
  (create-comp
   :container
   (fn [store]
     (fn [cursor]
       (let [state (:states store)]
         (div
          {}
          (with-cursor :todolist (comp-todolist (:todolist state) (:tasks store)))
          (div {:style style-states} (comp-text (pr-str (:states store)) nil))))))))
