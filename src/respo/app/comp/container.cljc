
(ns respo.app.comp.container
  (:require-macros [respo.macros :refer [defcomp div span <>]])
  (:require [respo.cursor :refer [with-cursor]]
            [respo.core :refer [create-comp]]
            [respo.app.comp.todolist :refer [comp-todolist]]))

(def style-states {:padding 8})

(def style-global {:font-family "Avenir,Verdana"})

(defcomp
 comp-container
 (store)
 (let [state (:states store)]
   (div
    {:style style-global}
    (with-cursor :todolist (comp-todolist (:todolist state) (:tasks store)))
    (div {:style style-states} (<> span (pr-str (:states store)) nil)))))
