
(ns respo.test.comp.todolist
  (:require [respo.test.comp.task :refer [comp-task]]
            [respo.core :refer [defcomp div list->]]))

(def style-todolist {:color :blue, :font-family "\"微软雅黑\", Verdana"})

(defcomp
 comp-todolist
 (tasks)
 (list->
  {:style style-todolist}
  (->> tasks (map (fn [task] [(:id task) (comp-task task)])))))
