
(ns respo.app.comp.container
  (:require-macros [respo.core :refer [defcomp div span <> cursor->]])
  (:require [respo.core :refer [create-comp create-element]]
            [respo.app.comp.todolist :refer [comp-todolist]]))

(def style-states {:padding 8})

(def style-global {:font-family "Avenir,Verdana"})

(defcomp
 comp-container
 (store)
 (let [state (:states store)]
   (div
    {:style style-global}
    (cursor-> :todolist comp-todolist state (:tasks store))
    (div {:style style-states} (<> span (pr-str (:states store)) nil)))))
