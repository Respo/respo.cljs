
(ns respo.test.comp.todolist
  (:require-macros (respo.macros :refer (div)))
  (:require [respo.test.comp.task :refer [comp-task]]
            [respo.core :refer [create-comp create-element]]))

(def style-todolist {:color :blue, :font-family "\"微软雅黑\", Verdana"})

(def comp-todolist
  (create-comp
   :todolist
   (fn [tasks]
     (fn [cursor]
       (div
        {:style style-todolist}
        (->> tasks (map (fn [task] [(:id task) (comp-task task)]))))))))
