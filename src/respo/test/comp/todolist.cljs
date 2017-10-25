
(ns respo.test.comp.todolist
  (:require [respo.test.comp.task :refer [comp-task]]
            [respo.macros :refer-macros (defcomp div)]))

(def style-todolist {:color :blue, :font-family "\"微软雅黑\", Verdana"})

(defcomp
 comp-todolist
 (tasks)
 (div {:style style-todolist} (->> tasks (map (fn [task] [(:id task) (comp-task task)])))))
