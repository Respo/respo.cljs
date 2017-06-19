
(ns respo.test-component.todolist
  (:require-macros (respo.macros :refer (div)))
  (:require [respo.alias :refer [create-comp]]
            [respo.test-component.task :refer [comp-task]]))

(def style-todolist {:color :blue, :font-family "\"微软雅黑\", Verdana"})

(def comp-todolist
  (create-comp
   :todolist
   (fn [tasks]
     (fn [cursor]
       (div
        {:style style-todolist}
        (->> tasks (map (fn [task] [(:id task) (comp-task task)]))))))))
